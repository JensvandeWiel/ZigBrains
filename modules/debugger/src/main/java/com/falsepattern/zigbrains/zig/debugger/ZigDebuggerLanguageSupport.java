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

import com.falsepattern.zigbrains.project.execution.configurations.ZigRunExecutionConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.jetbrains.cidr.execution.debugger.CidrDebuggerLanguageSupport;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ZigDebuggerLanguageSupport extends CidrDebuggerLanguageSupport {
    @Override
    public @NotNull Set<DebuggerDriver.DebuggerLanguage> getSupportedDebuggerLanguages() {
        return Set.of(ZigDebuggerLanguage.INSTANCE);
    }

    @Override
    public @Nullable XDebuggerEditorsProvider createEditor(@Nullable RunProfile profile) {
        if (!(profile instanceof ZigRunExecutionConfiguration)) {
            return null;
        }
        return createEditorProvider();
    }
}
