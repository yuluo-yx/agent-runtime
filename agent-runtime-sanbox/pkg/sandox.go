// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package pkg

import (
	"context"
)

type ISandBox interface {
	Start(ctx context.Context) error
	Stop(ctx context.Context) error

	IsRunning(ctx context.Context) bool
	IsEnabled(ctx context.Context) bool

	SandBoxType() SandboxRuntimeType
	SandBox() Sandbox
}

type Sandbox struct {
	ID     string
	Name   string
	Type   SandboxType
	Status ContainerStatus
}

type SandboxType string

const (
	BrowserSandbox SandboxType = "browser"
	FSandbox       SandboxType = "fs"
	BaseSandbox    SandboxType = "base"
)

type SandboxRuntimeType string

const (
	Containerd SandboxRuntimeType = "Containerd"
	Podman     SandboxRuntimeType = "podman"
	Docker     SandboxRuntimeType = "docker"
)

type ContainerStatus string

const (
	Running ContainerStatus = "running"
	Stopped ContainerStatus = "stopped"
	Paused  ContainerStatus = "paused"
	Exited  ContainerStatus = "exited"
	Unknown ContainerStatus = "unknown"
)
