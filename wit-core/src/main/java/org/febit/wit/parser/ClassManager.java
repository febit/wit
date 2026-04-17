/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.parser;

import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.ir.Position;
import org.febit.wit.parser.support.ClassNameRope;
import org.febit.wit.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

public class ClassManager {

    private final Map<String, String> imported = new HashMap<>();

    public void imports(ClassNameRope rope, Position position) throws ScriptParseException {
        var simpleName = rope.simpleName();
        if (ClassUtils.primitiveType(simpleName) != null) {
            throw new ScriptParseException("Cannot import primitive type: " + simpleName, position);
        }
        var componentName = rope.componentName();
        var existing = imported.get(simpleName);
        if (existing != null) {
            if (existing.equals(componentName)) {
                return;
            }
            throw new ScriptParseException("Ambiguous import for class name: " + simpleName
                    + ", exists: " + existing + ", new: " + componentName, position);
        }
        imported.put(simpleName, componentName);
    }

    public Class<?> resolve(ClassNameRope rope, Position position) throws ScriptParseException {
        var compName = rope.componentName();
        var fullName = resolveFullName(compName);
        try {
            return ClassUtils.load(fullName, rope.arrayDepth());
        } catch (UncheckedException ex) {
            throw new ScriptParseException("Class<?> not found: " + fullName, ex.getCause(), position);
        }
    }

    public Class<?> load(String name) {
        int arrayDept = 0;
        int end = name.indexOf('[');
        if (end >= 0) {
            arrayDept++;
            var idx = end;
            for (; ; ) {
                idx = name.indexOf('[', idx + 1);
                if (idx < 0) {
                    break;
                }
                arrayDept++;
            }
            name = name.substring(0, end).trim();
        }
        var fullName = resolveFullName(name);
        try {
            return ClassUtils.load(fullName, arrayDept);
        } catch (UncheckedException ex) {
            throw new ScriptParseException("Class<?> not found: " + fullName, ex.getCause());
        }
    }

    private String resolveFullName(String name) {

        // 0. full name
        if (name.indexOf('.') >= 0) {
            return name;
        }

        //1. find from @imports
        String fullName = imported.get(name);
        if (fullName != null) {
            return fullName;
        }
        Class<?> cls;

        // 2. find as primitive type
        cls = ClassUtils.primitiveType(name);
        if (cls != null) {
            return name;
        }

        // 3. find as java.lang.*
        try {
            cls = ClassUtils.load("java.lang.".concat(name));
        } catch (Exception ignore) {
            // Ignore
        }
        if (cls != null) {
            return cls.getName();
        }

        // failed, just return
        return name;
    }
}
