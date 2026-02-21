package org.febit.wit.parser;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Feature;
import org.febit.wit.Script;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.ScriptAST;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.ContextVar;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.JavaStaticFieldExpr;
import org.febit.wit.runtime.ast.stat.NoopStatement;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.febit.wit.util.ClassNameRope;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
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
    private final NativeFactory nativeFactory;
    /**
     * Current source version.
     */
    private final long lastSourceVersion;

    @Getter
    private final Script script;
    @Getter
    private final VarLayout varLayout;

    public Assembler(Script script) {
        var wit = script.wit();

        this.script = script;
        this.features = wit.features();
        this.templateTextFactory = wit.templateTextFactory();
        this.nativeFactory = wit.nativeFactory();

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

    public void registerClass(ClassNameRope rope, Position position) throws ParseException {
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

    public ContextVar declareVarAndCreateContextValue(String name, Position position) {
        return new ContextVar(varLayout.assignVar(name, position), position);
    }

    public ContextVar[] declareVarAndCreateContextValues(List<String> names, Position position) {
        var contextVars = new ContextVar[names.size()];
        for (int i = 0; i < names.size(); i++) {
            contextVars[i] = declareVarAndCreateContextValue(names.get(i), position);
        }
        return contextVars;
    }

    public Expression createContextValue(int frameOffset, String name, Position position) {
        var addr = varLayout.locate(name, frameOffset, !Feature.LOOSE_VAR.isEnabled(this.features), position);
        return Ast.readVar(addr, position);
    }

    public void assignConst(String name, Expression expr, Position position) {
        varLayout.assignConst(name, AstUtils.evalConst(expr), position);
    }

    public Expression createNativeStaticValue(ClassNameRope rope, Position position) {
        if (rope.size() <= 1) {
            throw new ParseException("native static need a field name.", position);
        }
        var fieldName = rope.build();
        var clazz = toClass(rope, position);
        var path = clazz.getName() + '.' + fieldName;
        if (!this.nativeFactory.security().allowed(path)) {
            throw new ParseException("Inaccessible native path: " + path, position);
        }
        final Field field;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new ParseException("No such field: " + path, ex, position);
        }
        if (ClassUtils.isStatic(field)) {
            ClassUtils.setAccessible(field);
            if (ClassUtils.isFinal(field)) {
                try {
                    return new DirectValue(field.get(null), position);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new ParseException("Failed to get static field value: " + path, ex, position);
                }
            } else {
                return new JavaStaticFieldExpr(field, position);
            }
        } else {
            throw new ParseException("No a static field: " + path, position);
        }
    }

    public Expression createNativeNewArrayDeclareExpression(Class<?> componentType, Position position) {
        return new DirectValue(this.nativeFactory.getNativeNewArrayMethodDeclare(componentType, position, true),
                position);
    }

    public Expression createNativeMethodDeclareExpression(
            Class<?> clazz, String methodName, @Nullable List<Class<?>> list, Position position) {
        return new DirectValue(this.nativeFactory.getNativeMethodDeclare(clazz, methodName,
                list == null ? new Class[0] : list.toArray(new Class[0]),
                position, true), position);
    }

    public Expression createMethodReference(String ref, Position position) {
        int split = ref.indexOf("::");
        String className = ref.substring(0, split).trim();
        String method = ref.substring(split + 2).trim();
        FunctionDeclare functionDeclare;
        Class<?> cls = toClass(className);
        if ("new".equals(method)) {
            if (cls.isArray()) {
                functionDeclare = this.nativeFactory.getNativeNewArrayMethodDeclare(cls.getComponentType(),
                        position, true);
            } else {
                functionDeclare = this.nativeFactory.getNativeConstructorDeclare(cls, position, true);
            }
        } else {
            functionDeclare = this.nativeFactory.getNativeMethodDeclare(cls, method, position, true);
        }
        return new DirectValue(functionDeclare, position);
    }

    public Expression createNativeConstructorDeclareExpression(
            Class<?> clazz, @Nullable List<Class<?>> list, Position position) {
        return new DirectValue(this.nativeFactory.getNativeConstructorDeclare(clazz,
                list == null ? new Class[0] : list.toArray(new Class[0]),
                position, true), position);
    }

    public Statement declareVar(String name, Position position) {
        //XXX: Should Check var used before init
        varLayout.assignVar(name, position);
        return NoopStatement.INSTANCE;
    }

    public ScriptAST buildAST(List<Statement> list) {
        var statements = Ast.flatStatements(list);
        var loops = AstUtils.collectLoopFlags(statements);
        if (!loops.isEmpty()) {
            throw new ParseException("loop overflow: " + loops);
        }
        return new ScriptAST(
                statements,
                this.varLayout.buildFrameIndexers(),
                this.varLayout.frameSize(),
                this.lastSourceVersion
        );
    }
}
