package org.febit.wit.parser;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Feature;
import org.febit.wit.Presets;
import org.febit.wit.Script;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.ScriptAST;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.NativeStaticFieldValue;
import org.febit.wit.runtime.ast.expr.VariableHeapValue;
import org.febit.wit.runtime.ast.statement.NoopStatement;
import org.febit.wit.util.ClassNameRope;
import org.febit.wit.util.ClassUtils;
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
            return ClassUtils.loadByName(classFullName, arrayDept);
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Class<?> not found:" + classFullName, ex);
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
        cls = ClassUtils.findPrimitiveClass(className);
        if (cls != null) {
            return className;
        }

        // 3. find as java.lang.*
        try {
            cls = ClassUtils.loadByName("java.lang.".concat(className));
        } catch (Exception ignore) {
            // Ignore
        }
        if (cls != null) {
            return cls.getName();
        }

        // failed, just return
        return className;
    }

    public void importClass(ClassNameRope rope, Position position) throws ParseException {
        var simpleName = rope.simpleName();
        if (ClassUtils.findPrimitiveClass(simpleName) != null) {
            throw new ParseException("Cannot import primitive type:" + simpleName, position);
        }
        var componentName = rope.componentName();
        var existing = importedClasses.get(simpleName);
        if (existing != null) {
            if (existing.equals(componentName)) {
                return;
            }
            throw new ParseException("Ambiguous import for class name: " + simpleName
                    + ", exists: " + existing + ", new: " + componentName, position);
        }
        importedClasses.put(simpleName, componentName);
    }

    public int getLabelIndex(String label) {
        return labelIndexMap.computeIfAbsent(label,
                l -> nextLabelIndex.getAndIncrement());
    }

    public Class<?> toClass(ClassNameRope rope, Position position) throws ParseException {
        var compName = rope.componentName();
        var classFullName = resolveClassFullName(compName);
        try {
            return ClassUtils.loadByName(classFullName, rope.arrayDepth());
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Class<?> not found:" + classFullName, ex, position);
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

    public VariableHeapValue[] declareVarAndCreateContextValues(List<String> names, Position position) {
        var contextVars = new VariableHeapValue[names.size()];
        for (int i = 0; i < names.size(); i++) {
            contextVars[i] = declareVarAndCreateContextValue(names.get(i), position);
        }
        return contextVars;
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
            throw new ParseException("native static need a field name.", position);
        }
        var fieldName = rope.pop();
        var clazz = toClass(rope, position);

        var path = clazz.getName() + '.' + fieldName;
        if (!this.nativeLayout.security().allowed(path)) {
            throw new ParseException("Inaccessible native path: " + path, position);
        }
        if (Presets.CLASS.equals(fieldName)) {
            return new DirectValue(clazz, position);
        }
        final Field field;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new ParseException("No such field: " + path, ex, position);
        }
        if (!ClassUtils.isStatic(field)) {
            throw new ParseException("No a static field: " + path, position);
        }
        field.trySetAccessible();
        if (!ClassUtils.isFinal(field)) {
            return new NativeStaticFieldValue(field, position);
        }
        try {
            return new DirectValue(field.get(null), position);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new ParseException("Failed to get static field value: " + path, ex, position);
        }
    }

    public Expression createNewArrayNativeFunctionValue(Class<?> componentType, Position pos) {
        Class<?> classForCheck = componentType;
        while (classForCheck.isArray()) {
            classForCheck = classForCheck.getComponentType();
        }
        if (ClassUtils.isVoidType(classForCheck)) {
            throw new ParseException("ComponentType must not void", pos);
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
            throw new ParseException(ex.getMessage(), ex, position);
        }

        var func = this.nativeLayout.functions().method(method);
        return new DirectValue(func, position);
    }

    public Expression createMethodNativeFunctionValue(
            Class<?> clazz, String methodName, Position position) {
        this.nativeLayout.securityCheck(clazz.getName() + '.' + methodName, position);

        var methods = ClassUtils.methods(clazz, methodName)
                .filter(ClassUtils::isPublic)
                .toList();
        if (methods.isEmpty()) {
            throw new ParseException("No such method: " + clazz.getName() + '#' + methodName, position);
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
            throw new ParseException(ex.getMessage(), ex, position);
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
            throw new ParseException("Unhandled flow control: " + ctrl, ctrl.position());
        });
        if (batches.size() != 1) {
            throw new IllegalStateException("Unexpected batches size: " + batches.size());
        }
        return new ScriptAST(
                this.lastSourceVersion,
                this.varLayout.heapSize(),
                this.varLayout.buildScopedIndexers(),
                batches.get(0)
        );
    }
}
