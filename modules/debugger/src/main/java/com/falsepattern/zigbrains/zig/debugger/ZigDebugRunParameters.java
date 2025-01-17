/*
 * Copyright 2023-2024 FalsePattern
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.falsepattern.zigbrains.zig.debugger;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.jetbrains.cidr.execution.Installer;
import com.jetbrains.cidr.execution.RunParameters;
import com.jetbrains.cidr.execution.TrivialInstaller;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriverConfiguration;
import com.jetbrains.cidr.execution.debugger.backend.lldb.LLDBDriverConfiguration;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class ZigDebugRunParameters extends RunParameters {
    private final GeneralCommandLine cmd;
    @Override
    public @NotNull Installer getInstaller() {
        return new TrivialInstaller(cmd);
    }

    @Override
    public @NotNull DebuggerDriverConfiguration getDebuggerDriverConfiguration() {
        if (LLDBDriverConfiguration.hasBundledLLDB()) {
            return new LLDBDriverConfiguration();
        } else {
            throw new IllegalStateException("The bundled LLDB debugger is missing from your IDE!");
        }
    }

    @Override
    public @Nullable String getArchitectureId() {
        return null;
    }
}
