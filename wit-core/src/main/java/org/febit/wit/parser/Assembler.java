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
import org.febit.wit.engine.ParseContext;
import org.febit.wit.engine.TemplateTextFactory;
import org.febit.wit.engine.nativex.NativeAccess;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.ExpressionArray;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.ScriptIR;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.StatementBatch;
import org.febit.wit.ir.expr.ConstantValue;
import org.febit.wit.ir.expr.HeapValue;
import org.febit.wit.ir.expr.StaticNativeFieldValue;
import org.febit.wit.ir.expr.VariableHeapFrameValue;
import org.febit.wit.ir.expr.VariableHeapValue;
import org.febit.wit.ir.statement.NoopStatement;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.parser.support.ClassNameRope;
import org.febit.wit.parser.support.VarLayout;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.MethodHandleUtils;
import org.febit.wit.util.Modifiers;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Accessors(fluent = true)
public class Assembler {

    private final Map<@Nullable String, Integer> labelIndexMap = new HashMap<>();
    private final AtomicInteger nextLabelIndex = new AtomicInteger();

    @Getter
    private final IRFactory ir = new IRFactory();
    @Getter
    private final ClassManager classes = new ClassManager();
    @Getter
    private final String scriptPath;
    @Getter
    private final ParseContext context;
    @Getter
    private final VarLayout vars;
    private final int features;

    private final TemplateTextFactory templateTextFactory;
    private final NativeAccess nativeAccess;
    /**
     * Current source version.
     */
    private final long lastSourceVersion;

    public Assembler(ParseContext context) {
        this.context = context;
        this.scriptPath = context.path();

        var engine = context.engine();
        this.features = engine.features();
        this.templateTextFactory = engine.templateTextFactory();
        this.nativeAccess = engine.nativeAccess();

        this.vars = new VarLayout(engine);
        this.labelIndexMap.put(null, 0);
        this.nextLabelIndex.set(1);

        // TODO: get source version before open it, may less than actual value.
        this.lastSourceVersion = context.source().version();
    }

    public void onParseStarted() {
        this.templateTextFactory.onParseStarted(context);
    }

    public void onParseCompleted() {
        this.templateTextFactory.onParseCompleted(context);
    }

    public boolean isEnabled(Feature feature) {
        return feature.isEnabled(features);
    }

    public int getLabelIndex(String label) {
        return labelIndexMap.computeIfAbsent(label,
                l -> nextLabelIndex.getAndIncrement());
    }

    public void assignConst(String name, Expression expr, Position position) {
        vars.assignConst(name, StatementUtils.evalAsConst(expr), position);
    }

    public class IRFactory {

        public Statement templateText(char @Nullable [] text, Position position) {
            if (text == null || text.length == 0) {
                return NoopStatement.INSTANCE;
            }
            return templateTextFactory.create(context, text, position);
        }

        public Statement declare(String name, Position position) {
            //XXX: Should Check var used before init
            vars.assignVar(name, position);
            return NoopStatement.INSTANCE;
        }

        public VariableHeapValue declareAndLocate(String name, Position position) {
            return new VariableHeapValue(vars.assignVar(name, position), position);
        }

        public ExpressionArray declareAndLocate(List<String> names, Position position) {
            var contextVars = new Expression[names.size()];
            for (int i = 0; i < names.size(); i++) {
                contextVars[i] = declareAndLocate(names.get(i), position);
            }
            return ExpressionArray.of(contextVars);
        }

        public Expression locate(int scopeOffset, String name, Position pos) {
            var addr = vars.locate(name, scopeOffset, !Feature.LOOSE_VAR.isEnabled(features), pos);
            return switch (addr.kind()) {
                case VAR -> new VariableHeapValue(addr.slot(), pos);
                case FRAME_VAR -> new VariableHeapFrameValue(addr.frameOffset(), addr.slot(), pos);
                case CONSTANT -> new ConstantValue(addr.value(), pos);
                case HEAP -> {
                    var key = Objects.requireNonNull(addr.key());
                    var heap = Objects.requireNonNull(addr.heap());
                    yield new HeapValue(
                            heap, key, pos
                    );
                }
            };
        }

        public Expression staticNativeField(ClassNameRope rope, Position position) {
            if (rope.size() <= 1) {
                throw new ScriptParseException("native static need a field name.", position);
            }
            var fieldName = rope.pop();
            var clazz = classes.resolve(rope, position);

            var path = clazz.getName() + '.' + fieldName;
            if (!nativeAccess.security().allowed(path)) {
                throw new ScriptParseException("Inaccessible native path: " + path, position);
            }
            if (ReservedNames.CLASS.equals(fieldName)) {
                return new ConstantValue(clazz, position);
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
            VarHandle handle;
            try {
                handle = MethodHandleUtils.lookupOf(clazz)
                        .unreflectVarHandle(field);
            } catch (IllegalAccessException e) {
                throw new ScriptParseException("Cannot access field: " + path, e, position);
            }
            if (!Modifiers.isFinal(field)) {
                return new StaticNativeFieldValue(handle, position);
            }
            try {
                return new ConstantValue(handle.get(), position);
            } catch (IllegalArgumentException ex) {
                throw new ScriptParseException("Failed to get static field value: " + path, ex, position);
            }
        }

        public Expression newNativeArray(Class<?> componentType, Position pos) {
            Class<?> classForCheck = componentType;
            while (classForCheck.isArray()) {
                classForCheck = classForCheck.getComponentType();
            }
            if (ClassUtils.isVoidType(classForCheck)) {
                throw new ScriptParseException("ComponentType must not void", pos);
            }
            nativeAccess.securityCheck(classForCheck.getName() + ".[]", pos);

            var function = nativeAccess.functions().array(componentType);
            return new ConstantValue(function, pos);
        }

        public Expression nativeMethod(
                Class<?> clazz, String methodName, @Nullable List<Class<?>> paramTypes, Position position) {
            nativeAccess.securityCheck(clazz.getName() + '.' + methodName, position);

            Method method;
            try {
                method = clazz.getMethod(methodName,
                        paramTypes == null ? new Class[0] : paramTypes.toArray(new Class[0])
                );
            } catch (NoSuchMethodException | SecurityException ex) {
                throw new ScriptParseException(ex.getMessage(), ex, position);
            }

            var func = nativeAccess.functions().method(method);
            return new ConstantValue(func, position);
        }

        public Expression nativeMethod(
                Class<?> clazz, String methodName, Position position) {
            nativeAccess.securityCheck(clazz.getName() + '.' + methodName, position);

            var methods = NativeMethods.find(clazz, methodName)
                    .toList();
            if (methods.isEmpty()) {
                throw new ScriptParseException("No such method: " + clazz.getName() + '#' + methodName, position);
            }

            var func = nativeAccess.functions().method(methods);
            return new ConstantValue(func, position);
        }

        public Expression nativeNew(
                Class<?> clazz, @Nullable List<Class<?>> paramTypes, Position position) {
            nativeAccess.securityCheck(clazz.getName() + '.' + ReservedNames.NEW, position);

            Constructor<?> constructor;
            try {
                constructor = clazz.getConstructor(
                        paramTypes == null ? new Class[0] : paramTypes.toArray(new Class[0])
                );
            } catch (NoSuchMethodException | SecurityException ex) {
                throw new ScriptParseException(ex.getMessage(), ex, position);
            }
            var func = nativeAccess.functions().constructor(constructor);
            return new ConstantValue(func, position);
        }

        public Expression nativeNew(Class<?> clazz, Position position) {
            nativeAccess.securityCheck(clazz.getName() + '.' + ReservedNames.NEW, position);

            var constructors = clazz.getConstructors();
            var func = nativeAccess.functions().constructor(List.of(constructors));
            return new ConstantValue(func, position);
        }

        public Expression nativeMethodReference(String pattern, Position position) {
            int split = pattern.indexOf("::");
            var className = pattern.substring(0, split).trim();
            var cls = classes.load(className);

            var method = pattern.substring(split + 2).trim();
            if (!ReservedNames.NEW.equals(method)) {
                return nativeMethod(cls, method, position);
            }
            if (cls.isArray()) {
                return newNativeArray(cls.getComponentType(), position);
            }
            return nativeNew(cls, position);
        }

        public ScriptIR script(List<Statement> list) {
            var batches = StatementBatch.batch(list, jump -> {
                throw new ScriptParseException("Unhandled control flow: " + jump, jump.position());
            });
            if (batches.size() != 1) {
                throw new IllegalStateException("Unexpected batches size: " + batches.size());
            }
            return new ScriptIR(
                    lastSourceVersion,
                    vars.slotSize(),
                    vars.buildScopeTables(),
                    batches.get(0)
            );
        }
    }

}
