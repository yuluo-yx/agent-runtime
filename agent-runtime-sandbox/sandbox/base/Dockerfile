# Base image with Python and essential tools
FROM python:3.11-slim

# Set environment variables
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1 \
    WORKSPACE_DIR=/workspace \
    SESSION_ID="" \
    DEBIAN_FRONTEND=noninteractive

# Install system dependencies
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    git \
    vim \
    nano \
    htop \
    procps \
    net-tools \
    iputils-ping \
    telnet \
    unzip \
    zip \
    jq \
    tree \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Install Python packages
RUN pip install --no-cache-dir \
    fastapi==0.104.1 \
    uvicorn[standard]==0.24.0 \
    pydantic==2.5.0 \
    requests==2.31.0 \
    ipython==8.17.2 \
    jupyter==1.0.0 \
    numpy==1.24.3 \
    pandas==2.0.3 \
    matplotlib==3.7.2 \
    seaborn==0.12.2 \
    scikit-learn==1.3.0 \
    scipy==1.11.1 \
    plotly==5.15.0 \
    beautifulsoup4==4.12.2 \
    lxml==4.9.3 \
    openpyxl==3.1.2 \
    pillow==10.0.0 \
    python-multipart==0.0.6

# Create workspace directory
RUN mkdir -p $WORKSPACE_DIR
WORKDIR $WORKSPACE_DIR

# Create app directory
RUN mkdir -p /app
WORKDIR /app

# Copy sandbox server files
COPY sandbox_server/ /app/

# Make sure the server script is executable
RUN chmod +x /app/main.py

# Create a non-root user
RUN useradd -m -u 1000 sandbox && \
    chown -R sandbox:sandbox /app && \
    chown -R sandbox:sandbox $WORKSPACE_DIR

# Switch to non-root user
USER sandbox

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/healthz || exit 1

# Start the server
CMD ["python", "/app/main.py"]