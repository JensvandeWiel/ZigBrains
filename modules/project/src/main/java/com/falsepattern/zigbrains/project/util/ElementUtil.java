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

package com.falsepattern.zigbrains.project.util;

import lombok.val;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

public class ElementUtil {
    public static @Nullable String readString(Element element, String name) {
        return element.getChildren()
                      .stream()
                      .filter(it -> it.getName()
                                      .equals("ZigBrainsOption") &&
                                    it.getAttributeValue("name")
                                      .equals(name))
                      .findAny()
                      .map(it -> it.getAttributeValue("value"))
                      .orElse(null);
    }

    public static void writeString(Element element, String name, String value) {
        val option = new Element("ZigBrainsOption");
        option.setAttribute("name", name);
        option.setAttribute("value", value);

        element.addContent(option);
    }
}
