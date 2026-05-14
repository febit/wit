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
package org.febit.wit.extern.asm;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.engine.accessor.AccessorFactory;
import org.febit.wit.engine.accessor.Getter;
import org.febit.wit.engine.accessor.Renderer;
import org.febit.wit.engine.accessor.Setter;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.runtime.accessor.EmptyBeanAccessor;
import org.febit.wit.runtime.accessor.GenericBeanAccessor;
import org.febit.wit.runtime.accessor.ReflectBeanAccessorFactory;
import org.febit.wit.util.ClassMap;
import org.febit.wit.util.Modifiers;
import org.febit.wit.util.bean.BeanProperties;
import org.febit.wit.util.bean.BeanProperty;
import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.Label;
import org.febit.wit_shaded.asm.MethodWriter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
public class AsmBeanAccessorFactory implements AccessorFactory {

    private static final String[] ACCESSOR_INTERFACES = {"org/febit/wit/extern/asm/AsmBeanAccessor"};
    private static final ClassMap<Class<?>> ACCESSOR_CLASSES = new ClassMap<>();

    private final ClassMap<Optional<GenericBeanAccessor<?>>> cached = new ClassMap<>();
    private final AccessorFactory fallback = ReflectBeanAccessorFactory.get();

    @UtilityClass
    private static class LazyHolder {
        private static final AsmBeanAccessorFactory INSTANCE = new AsmBeanAccessorFactory();
    }

    public static AsmBeanAccessorFactory get() {
        return LazyHolder.INSTANCE;
    }

    @Override
    @SuppressWarnings({"unchecked", "OptionalIsPresent"})
    public <T> Getter<T> getter(Class<T> type) {
        var getter = accessor(type);
        return getter.isPresent()
                ? (Getter<T>) getter.get()
                : fallback.getter(type);
    }

    @Override
    @SuppressWarnings({"unchecked", "OptionalIsPresent"})
    public <T> Setter<T> setter(Class<T> type) {
        var setter = accessor(type);
        return setter.isPresent()
                ? (Setter<T>) setter.get()
                : fallback.setter(type);
    }

    @Override
    public <T> Renderer<T> renderer(Class<T> type) {
        return fallback.renderer(type);
    }

    @SuppressWarnings({
            "ReplaceNullCheck",
            "OptionalAssignedToNull",
            "java:S2789", // "null" should not be used with "Optional"
    })
    private Optional<GenericBeanAccessor<?>> accessor(Class<?> type) {
        var accessor = cached.get(type);
        if (accessor != null) {
            return accessor;
        }
        return cached.computeIfAbsent(type, this::tryCreateAccessor);
    }

    @Override
    public AccessorFactory cached() {
        return this;
    }

    private Optional<GenericBeanAccessor<?>> tryCreateAccessor(Class<?> type) {
        try {
            var cls = ACCESSOR_CLASSES.computeIfAbsent(type, AsmBeanAccessorFactory::constructAccessorClass);
            if (cls == EmptyBeanAccessor.class) {
                return Optional.of(EmptyBeanAccessor.get());
            }
            var instance = (GenericBeanAccessor<?>) cls.getConstructor().newInstance();
            return Optional.of(instance);
        } catch (UncheckedException e) {
            log.warn("Cannot create accessor for type: {}, {}", type.getName(), e.getMessage());
            return Optional.empty();
        } catch (Exception | LinkageError e) {
            log.warn("Cannot create accessor for type: {}", type.getName(), e);
            return Optional.empty();
        }
    }

    static Class<?> constructAccessorClass(Class<?> beanClass) {
        var fields = BeanProperties.introspect(beanClass)
                .sorted(Comparator.comparing(b -> b.name().hashCode()))
                .toArray(BeanProperty[]::new);
        if (fields.length == 0) {
            return EmptyBeanAccessor.class;
        }

        if (!Modifiers.isPublic(beanClass)) {
            throw new UncheckedException("class is not public: " + beanClass.getName());
        }
        var className = "org.febit.wit.extern.asm.AsmBeanAccessor" + AsmUtils.SEQ.getAndIncrement();

        var classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL,
                AsmUtils.internalNameOf(className), AsmUtils.TYPE_OBJ, ACCESSOR_INTERFACES);
        AsmUtils.visitConstructor(classWriter);

        final int fieldCount = fields.length;
        int[] hashes = new int[fieldCount];
        int[] indexer = new int[fieldCount];
        int hashCount = 0;
        int hash;
        hashes[hashCount++] = hash = fields[0].name().hashCode();
        int i = 1;
        while (i < fieldCount) {
            var property = fields[i];
            if (hash != property.name().hashCode()) {
                indexer[hashCount - 1] = i;
                hashes[hashCount++] = hash = property.name().hashCode();
            }
            i++;
        }
        indexer[hashCount - 1] = fieldCount;
        hashes = Arrays.copyOf(hashes, hashCount);
        indexer = Arrays.copyOf(indexer, hashCount);

        visitXetMethod(true, classWriter, beanClass, fields, hashes, indexer);
        visitXetMethod(false, classWriter, beanClass, fields, hashes, indexer);

        //getMatchClass
        var m = classWriter.visitMethod(Constants.ACC_PUBLIC, "getMatchClass",
                "()Ljava/lang/Class;", null);
        m.visitInsn(Constants.ACONST_NULL);
        m.visitInsn(Constants.ARETURN);
        m.visitMaxs();

        return AsmUtils.loadClass(className, classWriter);
    }

    private static void visitXetMethod(
            final boolean isGetter, final ClassWriter classWriter, final Class<?> beanClass,
            final BeanProperty[] props, final int[] hashes, final int[] indexer) {
        var beanName = AsmUtils.boxedInternalNameOf(beanClass);
        final MethodWriter m;
        if (isGetter) {
            m = classWriter.visitMethod(Constants.ACC_PUBLIC, "get",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null);
        } else {
            m = classWriter.visitMethod(Constants.ACC_PUBLIC, "set",
                    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", null);
        }
        var propsSize = props.length;

        assert propsSize > 0 : "propsSize should be greater than 0";
        var finalEndLabel = new Label();
        if (propsSize < 4) {
            visitXetFields(isGetter, m, props, 0, propsSize, beanName, finalEndLabel);
        } else {
            m.visitVarInsn(Constants.ALOAD, 2);
            m.invokeVirtual(AsmUtils.TYPE_OBJ, "hashCode", "()I");

            var size = hashes.length;
            var labels = new Label[size];
            for (int i = 0; i < size; i++) {
                labels[i] = new Label();
            }

            m.visitLookupSwitchInsn(finalEndLabel, hashes, labels);
            int start = 0;
            for (int i = 0; i < size; i++) {
                int end = indexer[i];
                m.visitLabel(labels[i]);
                visitXetFields(isGetter, m, props, start, end, beanName, finalEndLabel);
                start = end;
            }
        }
        m.visitLabel(finalEndLabel);
        //Exception
        m.visitTypeInsn(Constants.NEW, AsmUtils.TYPE_EVAL_EX);
        m.visitInsn(Constants.DUP);
        m.visitLdcInsn("no such property: " + beanClass.getName() + '#');
        m.visitVarInsn(Constants.ALOAD, 2);
        m.invokeStatic(AsmUtils.TYPE_STRING, "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
        m.invokeVirtual(AsmUtils.TYPE_STRING, "concat", "(Ljava/lang/String;)Ljava/lang/String;");
        m.visitMethodInsn(Constants.INVOKESPECIAL, AsmUtils.TYPE_EVAL_EX,
                AsmUtils.METHOD_CTOR, "(Ljava/lang/String;)V");
        m.visitInsn(Constants.ATHROW);
        m.visitMaxs();
    }

    private static void visitXetFields(
            final boolean isGetter, final MethodWriter m,
            final BeanProperty[] props, final int start, final int end,
            final String beanName, final Label failedMatchLabel) {
        var gotoTable = new Label[end - start];
        //if ==
        for (int i = start; i < end; i++) {
            var label = new Label();
            gotoTable[i - start] = label;
            m.visitLdcInsn(props[i].name());
            m.visitVarInsn(Constants.ALOAD, 2);
            // if == goto
            m.visitJumpInsn(Constants.IF_ACMPEQ, label);
        }
        //if equals
        for (int i = start; i < end; i++) {
            m.visitLdcInsn(props[i].name());
            m.visitVarInsn(Constants.ALOAD, 2);
            m.invokeVirtual(AsmUtils.TYPE_STRING, "equals", "(Ljava/lang/Object;)Z");
            // if true goto
            m.visitJumpInsn(Constants.IFNE, gotoTable[i - start]);
        }
        //failed, to end
        m.visitJumpInsn(Constants.GOTO, failedMatchLabel);
        //actions
        for (int i = start; i < end; i++) {
            m.visitLabel(gotoTable[i - start]);
            var info = props[i];
            if (isGetter) {
                appendGetFieldCode(m, info, beanName);
            } else {
                appendSetFieldCode(m, info, beanName);
            }
        }
    }

    private static void appendGetFieldCode(
            final MethodWriter m, final BeanProperty property, final String beanName) {
        var getter = property.getterMethod();
        var field = property.field();
        if (getter == null && field == null) {
            // Unreadable Exception
            AsmUtils.visitScriptEvaluateException(m, "property is not readable: "
                    + property.beanType().getName() + "#" + property.name());
            return;
        }
        var resultType = getter != null ? getter.getReturnType() : field.getType();
        m.visitVarInsn(Constants.ALOAD, 1);
        m.checkCast(beanName);
        if (getter != null) {
            // bean.getName()
            m.invokeVirtual(beanName, getter.getName(), AsmUtils.descriptorOf(getter));
        } else {
            // bean.name
            m.visitFieldInsn(Constants.GETFIELD, beanName, property.name(), AsmUtils.descriptorOf(resultType));
        }
        AsmUtils.visitBoxIfNeed(m, resultType);
        m.visitInsn(Constants.ARETURN);
    }

    private static void appendSetFieldCode(
            final MethodWriter m, final BeanProperty property, final String beanName) {
        var setter = property.setterMethod();
        var field = property.field();
        if (setter == null && (field == null || property.isReadonlyField())) {
            // Readonly Exception
            AsmUtils.visitScriptEvaluateException(m, "property is not writable: "
                    + property.beanType().getName() + "#" + property.name());
            return;
        }
        var fieldClass = setter != null
                ? setter.getParameterTypes()[0]
                : field.getType();
        m.visitVarInsn(Constants.ALOAD, 1);
        m.checkCast(beanName);
        m.visitVarInsn(Constants.ALOAD, 3);
        m.checkCast(AsmUtils.boxedInternalNameOf(fieldClass));
        AsmUtils.visitUnboxIfNeed(m, fieldClass);
        if (setter != null) {
            // bean.setName((String) name)
            m.invokeVirtual(beanName, setter.getName(), AsmUtils.descriptorOf(setter));
        } else {
            // bean.name = (String) name
            m.visitFieldInsn(Constants.PUTFIELD, beanName, property.name(), AsmUtils.descriptorOf(fieldClass));
        }

        m.visitInsn(Constants.RETURN);
    }
}
