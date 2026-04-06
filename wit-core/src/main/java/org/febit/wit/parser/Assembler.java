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

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Feature;
import org.febit.wit.Presets;
import org.febit.wit.Script;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.ScriptAST;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.ExpressionArray;
import org.febit.wit.runtime.ast.expr.NativeStaticFieldValue;
import org.febit.wit.runtime.ast.expr.VariableHeapValue;
import org.febit.wit.runtime.ast.statement.NoopStatement;
import org.febit.wit.util.ClassNameRope;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.Modifiers;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Accessors(fluent = true)
public class Assembler {

    private final Map<String, String> importedClasses = new HashMap<>();
    private final Map<@Nullable String, Integer> labelIndexMap = new HashMap<>();
    private final AtomicInteger nextLabelIndex = new AtomicInteger();

    private final int features;

    @Getter
    private final TemplateTextFactory templateTextFactory;
    private final NativeLayout nativeLayout;
    /**
     * Current source version.
     */
    private final long lastSourceVersion;

    @Getter
    private final Script script;
    @Getter
    private final VarLayout varLayout;

    public Assembler(Script script) {
        var wit = script.engine();

        this.script = script;
        this.features = wit.features();
        this.templateTextFactory = wit.templateTextFactory();
        this.nativeLayout = wit.nativeLayout();

        this.varLayout = new VarLayout(wit);
        this.labelIndexMap.put(null, 0);
        this.nextLabelIndex.set(1);

        // TODO: get source version before open it, may less than actual value.
        this.lastSourceVersion = script.source().version();
    }

    public void onParserStarted() {
        this.templateTextFactory.onParserStarted(script);
    }

    public void onParserCompleted() {
        this.templateTextFactory.onParserCompleted(script);
    }

    public boolean isEnabled(Feature feature) {
        return feature.isEnabled(features);
    }

    private Class<?> toClass(String className) {
        int arrayDept = 0;
        int flag = className.indexOf('[');
        if (flag >= 0) {
            // figure out array dept
            for (char c : className.substring(flag).toCharArray()) {
                if (c == '[') {
                    arrayDept++;
                }
            }
            className = className.substring(0, flag).trim();
        }
        String classFullName = resolveClassFullName(className);
        try {
            return ClassUtils.load(classFullName, arrayDept);
        } catch (ClassNotFoundException ex) {
            throw new ScriptParseException("Class<?> not found:" + classFullName, ex);
        }
    }

    private String resolveClassFullName(String className) {

        // 0. full name
        if (className.indexOf('.') >= 0) {
            return className;
        }

        //1. find from @imports
        String fullName = importedClasses.get(className);
        if (fullName != null) {
            return fullName;
        }
        Class<?> cls;

        // 2. find as primitive type
        cls = ClassUtils.primitiveType(className);
        if (cls != null) {
            return className;
        }

        // 3. find as java.lang.*
        try {
            cls = ClassUtils.load("java.lang.".concat(className));
        } catch (Exception ignore) {
            // Ignore
        }
        if (cls != null) {
            return cls.getName();
        }

        // failed, just return
        return className;
    }

    public void importClass(ClassNameRope rope, Position position) throws ScriptParseException {
        var simpleName = rope.simpleName();
        if (ClassUtils.primitiveType(simpleName) != null) {
            throw new ScriptParseException("Cannot import primitive type:" + simpleName, position);
        }
        var componentName = rope.componentName();
        var existing = importedClasses.get(simpleName);
        if (existing != null) {
            if (existing.equals(componentName)) {
                return;
            }
            throw new ScriptParseException("Ambiguous import for class name: " + simpleName
                    + ", exists: " + existing + ", new: " + componentName, position);
        }
        importedClasses.put(simpleName, componentName);
    }

    public int getLabelIndex(String label) {
        return labelIndexMap.computeIfAbsent(label,
                l -> nextLabelIndex.getAndIncrement());
    }

    public Class<?> toClass(ClassNameRope rope, Position position) throws ScriptParseException {
        var compName = rope.componentName();
        var classFullName = resolveClassFullName(compName);
        try {
            return ClassUtils.load(classFullName, rope.arrayDepth());
        } catch (ClassNotFoundException ex) {
            throw new ScriptParseException("Class<?> not found:" + classFullName, ex, position);
        }
    }

    public Statement createTemplateText(char @Nullable [] text, Position position) {
        if (text == null || text.length == 0) {
            return NoopStatement.INSTANCE;
        }
        return this.templateTextFactory.create(script, text, position);
    }

    public VariableHeapValue declareVarAndCreateContextValue(String name, Position position) {
        return new VariableHeapValue(varLayout.assignVar(name, position), position);
    }

    public ExpressionArray declareVarAndCreateContextValues(List<String> names, Position position) {
        var contextVars = new Expression[names.size()];
        for (int i = 0; i < names.size(); i++) {
            contextVars[i] = declareVarAndCreateContextValue(names.get(i), position);
        }
        return ExpressionArray.of(contextVars);
    }

    public Expression createContextValue(int scopeOffset, String name, Position position) {
        var addr = varLayout.locate(name, scopeOffset, !Feature.LOOSE_VAR.isEnabled(this.features), position);
        return Ast.value(addr, position);
    }

    public void assignConst(String name, Expression expr, Position position) {
        varLayout.assignConst(name, StatementUtils.evalAsConst(expr), position);
    }

    public Expression createNativeStaticFieldValue(ClassNameRope rope, Position position) {
        if (rope.size() <= 1) {
            throw new ScriptParseException("native static need a field name.", position);
        }
        var fieldName = rope.pop();
        var clazz = toClass(rope, position);

        var path = clazz.getName() + '.' + fieldName;
        if (!this.nativeLayout.security().allowed(path)) {
            throw new ScriptParseException("Inaccessible native path: " + path, position);
        }
        if (Presets.CLASS.equals(fieldName)) {
            return new DirectValue(clazz, position);
        }
        final Field field;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new ScriptParseException("No such field: " + path, ex, position);
        }
        if (!Modifiers.isStatic(field)) {
            throw new ScriptParseException("No a static field: " + path, position);
        }
        field.trySetAccessible();
        if (!Modifiers.isFinal(field)) {
            return new NativeStaticFieldValue(field, position);
        }
        try {
            return new DirectValue(field.get(null), position);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new ScriptParseException("Failed to get static field value: " + path, ex, position);
        }
    }

    public Expression createNewArrayNativeFunctionValue(Class<?> componentType, Position pos) {
        Class<?> classForCheck = componentType;
        while (classForCheck.isArray()) {
            classForCheck = classForCheck.getComponentType();
        }
        if (ClassUtils.isVoidType(classForCheck)) {
            throw new ScriptParseException("ComponentType must not void", pos);
        }
        this.nativeLayout.securityCheck(classForCheck.getName() + ".[]", pos);

        var function = this.nativeLayout.functions().array(componentType);
        return new DirectValue(function, pos);
    }

    public Expression createMethodNativeFunctionValue(
            Class<?> clazz, String methodName, @Nullable List<Class<?>> paramTypes, Position position) {
        this.nativeLayout.securityCheck(clazz.getName() + '.' + methodName, position);

        Method method;
        try {
            method = clazz.getMethod(methodName,
                    paramTypes == null ? new Class[0] : paramTypes.toArray(new Class[0])
            );
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ScriptParseException(ex.getMessage(), ex, position);
        }

        var func = this.nativeLayout.functions().method(method);
        return new DirectValue(func, position);
    }

    public Expression createMethodNativeFunctionValue(
            Class<?> clazz, String methodName, Position position) {
        this.nativeLayout.securityCheck(clazz.getName() + '.' + methodName, position);

        var methods = NativeMethods.find(clazz, methodName)
                .filter(Modifiers::isPublic)
                .toList();
        if (methods.isEmpty()) {
            throw new ScriptParseException("No such method: " + clazz.getName() + '#' + methodName, position);
        }

        var func = this.nativeLayout.functions().method(methods);
        return new DirectValue(func, position);
    }

    public Expression createConstructorNativeFunctionValue(
            Class<?> clazz, @Nullable List<Class<?>> paramTypes, Position position) {
        this.nativeLayout.securityCheck(clazz.getName() + '.' + Presets.NEW, position);

        Constructor<?> constructor;
        try {
            constructor = clazz.getConstructor(
                    paramTypes == null ? new Class[0] : paramTypes.toArray(new Class[0])
            );
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ScriptParseException(ex.getMessage(), ex, position);
        }
        var func = this.nativeLayout.functions().constructor(constructor);
        return new DirectValue(func, position);
    }

    public Expression createConstructorNativeFunctionValue(
            Class<?> clazz, Position position) {
        this.nativeLayout.securityCheck(clazz.getName() + '.' + Presets.NEW, position);

        var constructors = clazz.getConstructors();
        var func = this.nativeLayout.functions().constructor(List.of(constructors));
        return new DirectValue(func, position);
    }

    public Expression createMethodReference(String ref, Position position) {
        int split = ref.indexOf("::");
        var className = ref.substring(0, split).trim();
        var cls = toClass(className);

        var method = ref.substring(split + 2).trim();
        if (!Presets.NEW.equals(method)) {
            return createMethodNativeFunctionValue(cls, method, position);
        }
        if (cls.isArray()) {
            return createNewArrayNativeFunctionValue(cls.getComponentType(), position);
        }
        return createConstructorNativeFunctionValue(cls, position);
    }

    public Statement declareVar(String name, Position position) {
        //XXX: Should Check var used before init
        varLayout.assignVar(name, position);
        return NoopStatement.INSTANCE;
    }

    public ScriptAST buildAST(List<Statement> list) {
        var batches = Ast.batch(list, ctrl -> {
            throw new ScriptParseException("Unhandled flow control: " + ctrl, ctrl.position());
        });
        if (batches.size() != 1) {
            throw new IllegalStateException("Unexpected batches size: " + batches.size());
        }
        return new ScriptAST(
                this.lastSourceVersion,
                this.varLayout.slotSize(),
                this.varLayout.buildScopeTables(),
                batches.get(0)
        );
    }
}
