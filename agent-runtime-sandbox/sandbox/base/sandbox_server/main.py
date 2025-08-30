#!/usr/bin/env python3
"""
Sandbox Server - Python service running inside Docker container
Receives HTTP requests and executes Python code or shell commands
"""

import os
import sys
import subprocess
import tempfile
import traceback
from typing import List, Dict, Any, Optional
from pathlib import Path

from fastapi import FastAPI, HTTPException, Depends, Security
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import uvicorn
from IPython.core.interactiveshell import InteractiveShell

# Initialize FastAPI app
app = FastAPI(
    title="AgentScope Sandbox Server",
    description="Python service for executing code in sandbox environment",
    version="1.0.0"
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Security
security = HTTPBearer(auto_error=False)

# Global IPython shell instance
ipython_shell = None

# Configuration
WORKSPACE_DIR = os.getenv("WORKSPACE_DIR", "/workspace")
SESSION_ID = os.getenv("SESSION_ID", "default")
SECRET_TOKEN = os.getenv("SECRET_TOKEN", "")


class TextContent(BaseModel):
    """Text content with type and description"""
    type: str
    text: str
    description: Optional[str] = None


class ExecutionResult(BaseModel):
    """Result of code execution"""
    content: List[TextContent]
    is_error: bool = False


class PythonRequest(BaseModel):
    """Request to execute Python code"""
    code: str
    split_output: bool = False


class ShellRequest(BaseModel):
    """Request to execute shell command"""
    command: str
    split_output: bool = False


def verify_token(credentials: Optional[HTTPAuthorizationCredentials] = Security(security)):
    """Verify authentication token"""
    if not SECRET_TOKEN:
        return True  # No token required if not configured
    
    if not credentials:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    if credentials.credentials != SECRET_TOKEN:
        raise HTTPException(status_code=401, detail="Invalid token")
    
    return True


def get_ipython_shell():
    """Get or create IPython shell instance"""
    global ipython_shell
    if ipython_shell is None:
        ipython_shell = InteractiveShell.instance()
        # Set working directory
        ipython_shell.magic(f"cd {WORKSPACE_DIR}")
    return ipython_shell


def execute_python_code(code: str, split_output: bool = False) -> ExecutionResult:
    """Execute Python code using IPython"""
    try:
        shell = get_ipython_shell()
        
        # Capture output
        from io import StringIO
        import sys
        
        old_stdout = sys.stdout
        old_stderr = sys.stderr
        
        stdout_capture = StringIO()
        stderr_capture = StringIO()
        
        sys.stdout = stdout_capture
        sys.stderr = stderr_capture
        
        try:
            # Execute code
            result = shell.run_cell(code)
            
            # Get output
            stdout_content = stdout_capture.getvalue()
            stderr_content = stderr_capture.getvalue()
            
            # Restore stdout/stderr
            sys.stdout = old_stdout
            sys.stderr = old_stderr
            
            content = []
            
            if split_output:
                if stdout_content:
                    content.append(TextContent(
                        type="stdout",
                        text=stdout_content,
                        description="Standard output"
                    ))
                if stderr_content:
                    content.append(TextContent(
                        type="stderr",
                        text=stderr_content,
                        description="Standard error"
                    ))
            else:
                combined_output = ""
                if stdout_content:
                    combined_output += stdout_content
                if stderr_content:
                    combined_output += stderr_content
                
                if combined_output:
                    content.append(TextContent(
                        type="output",
                        text=combined_output,
                        description="Execution output"
                    ))
            
            # Check for execution errors
            is_error = result.error_before_exec is not None or result.error_in_exec is not None
            
            if result.error_in_exec:
                error_content = str(result.error_in_exec)
                content.append(TextContent(
                    type="error",
                    text=error_content,
                    description="Execution error"
                ))
            
            return ExecutionResult(content=content, is_error=is_error)
            
        finally:
            sys.stdout = old_stdout
            sys.stderr = old_stderr
            
    except Exception as e:
        error_msg = f"Error executing Python code: {str(e)}\n{traceback.format_exc()}"
        return ExecutionResult(
            content=[TextContent(
                type="error",
                text=error_msg,
                description="Python execution error"
            )],
            is_error=True
        )


def execute_shell_command(command: str, split_output: bool = False) -> ExecutionResult:
    """Execute shell command"""
    try:
        # Execute command
        result = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            cwd=WORKSPACE_DIR,
            timeout=30  # 30 second timeout
        )
        
        content = []
        
        if split_output:
            if result.stdout:
                content.append(TextContent(
                    type="stdout",
                    text=result.stdout,
                    description="Standard output"
                ))
            if result.stderr:
                content.append(TextContent(
                    type="stderr",
                    text=result.stderr,
                    description="Standard error"
                ))
        else:
            combined_output = ""
            if result.stdout:
                combined_output += result.stdout
            if result.stderr:
                combined_output += result.stderr
            
            if combined_output:
                content.append(TextContent(
                    type="output",
                    text=combined_output,
                    description="Command output"
                ))
        
        # Add return code info
        content.append(TextContent(
            type="return_code",
            text=str(result.returncode),
            description="Command return code"
        ))
        
        is_error = result.returncode != 0
        
        return ExecutionResult(content=content, is_error=is_error)
        
    except subprocess.TimeoutExpired:
        return ExecutionResult(
            content=[TextContent(
                type="error",
                text="Command timed out after 30 seconds",
                description="Timeout error"
            )],
            is_error=True
        )
    except Exception as e:
        error_msg = f"Error executing shell command: {str(e)}\n{traceback.format_exc()}"
        return ExecutionResult(
            content=[TextContent(
                type="error",
                text=error_msg,
                description="Shell execution error"
            )],
            is_error=True
        )


@app.get("/healthz")
async def health_check():
    """Health check endpoint"""
    return "OK"


@app.get("/health")
async def detailed_health():
    """Detailed health status"""
    return {
        "status": "healthy",
        "session_id": SESSION_ID,
        "workspace_dir": WORKSPACE_DIR,
        "python_version": sys.version,
        "service": "sandbox-server"
    }


@app.post("/tools/run_ipython_cell", response_model=ExecutionResult)
async def run_ipython_cell(
    request: PythonRequest,
    _: bool = Depends(verify_token)
) -> ExecutionResult:
    """Execute Python code in IPython cell"""
    return execute_python_code(request.code, request.split_output)


@app.post("/tools/run_shell_command", response_model=ExecutionResult)
async def run_shell_command(
    request: ShellRequest,
    _: bool = Depends(verify_token)
) -> ExecutionResult:
    """Execute shell command"""
    return execute_shell_command(request.command, request.split_output)


@app.on_event("startup")
async def startup_event():
    """Initialize on startup"""
    # Ensure workspace directory exists
    Path(WORKSPACE_DIR).mkdir(parents=True, exist_ok=True)
    
    # Change to workspace directory
    os.chdir(WORKSPACE_DIR)
    
    print(f"Sandbox server started for session: {SESSION_ID}")
    print(f"Workspace directory: {WORKSPACE_DIR}")
    print(f"Python version: {sys.version}")


if __name__ == "__main__":
    # Run the server
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8000,
        log_level="info"
    )