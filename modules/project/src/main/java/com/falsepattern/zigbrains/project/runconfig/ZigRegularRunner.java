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

package com.falsepattern.zigbrains.project.runconfig;

import com.intellij.execution.executors.DefaultRunExecutor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ZigRegularRunner extends ZigExecutableRunner{
    public ZigRegularRunner() {
        super(DefaultRunExecutor.EXECUTOR_ID, "Unable to run zig");
    }

    @Override
    public @NotNull @NonNls String getRunnerId() {
        return "ZigRegularRunner";
    }
}
