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

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.runtime.accessor.AccessorFactory;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Render;
import org.febit.wit.runtime.accessor.Setter;
import org.febit.wit.runtime.accessor.impl.ReflectBeanAccessor;
import org.febit.wit.util.ClassMap;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.bean.BeanProperties;
import org.febit.wit.util.bean.BeanProperty;
import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.Label;
import org.febit.wit_shaded.asm.MethodWriter;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

import static org.febit.wit.util.Defaults.nvl;

@Slf4j
public class AsmBeanAccessorFactory implements AccessorFactory {

    private static final String[] ASM_ACCESSOR = {"org/febit/wit/extern/asm/AsmBeanAccessor"};

    private static final ReflectBeanAccessor FALLBACK = ReflectBeanAccessor.INSTANCE;
    private static final ClassMap<AsmBeanAccessor> CACHE = new ClassMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Getter<T> getter(Class<T> type) {
        var getter = buildIfAbsent(type);
        return (Getter<T>) nvl(getter, FALLBACK);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Setter<T> setter(Class<T> type) {
        var setter = buildIfAbsent(type);
        return (Setter<T>) nvl(setter, FALLBACK);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Render<T> render(Class<T> type) {
        return (Render<T>) FALLBACK;
    }

    @Nullable
    private AsmBeanAccessor buildIfAbsent(Class<?> type) {
        var accessor = CACHE.get(type);
        if (accessor != null) {
            return accessor;
        }
        synchronized (CACHE) {
            accessor = CACHE.get(type);
            if (accessor != null) {
                return accessor;
            }
            try {
                accessor = (AsmBeanAccessor) constructAccessorClassFor(type)
                        .getConstructor().newInstance();
                accessor = CACHE.putIfAbsent(type, accessor);
            } catch (Exception | LinkageError e) {
                log.error("Cannot create accessor for type: {}", type.getName(), e);
            }
            return accessor;
        }
    }

    static Class<?> constructAccessorClassFor(Class<?> beanClass) {
        //XXX: rewrite
        if (!ClassUtils.isPublic(beanClass)) {
            throw new UncheckedException("Class<?> is not public: " + beanClass);
        }
        var className = "org.febit.wit.extern.asm.AsmBeanAccessor" + AsmUtils.SEQ.getAndIncrement();

        var classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL,
                AsmUtils.internalNameOf(className), AsmUtils.TYPE_OBJ, ASM_ACCESSOR);
        AsmUtils.visitConstructor(classWriter);

        var fields = BeanProperties.introspect(beanClass)
                .sorted()
                .toArray(BeanProperty[]::new);

        final int fieldCount = fields.length;
        int[] hashes = new int[fieldCount];
        int[] indexer = new int[fieldCount];
        if (fieldCount > 0) {
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
        }

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
        if (propsSize != 0) {
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
        }
        //Exception
        m.visitTypeInsn(Constants.NEW, AsmUtils.TYPE_EVAL_EX);
        m.visitInsn(Constants.DUP);
        m.visitLdcInsn("Invalid property " + beanClass.getName() + '#');
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
            //Unreadable Exception
            AsmUtils.visitScriptEvaluateException(m, "Unreadable property "
                    + property.beanType().getName() + "#" + property.name());
            return;
        }
        var resultType = getter != null ? getter.getReturnType() : field.getType();
        m.visitVarInsn(Constants.ALOAD, 1);
        m.checkCast(beanName);
        if (getter != null) {
            //return book.getName()
            m.invokeVirtual(beanName, getter.getName(), AsmUtils.descriptorOf(getter));
        } else {
            //return book.name
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
            AsmUtils.visitScriptEvaluateException(m, "Readonly property "
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
            //book.setName((String)name)
            m.invokeVirtual(beanName, setter.getName(), AsmUtils.descriptorOf(setter));
        } else {
            //book.name = (String) name
            m.visitFieldInsn(Constants.PUTFIELD, beanName, property.name(), AsmUtils.descriptorOf(fieldClass));
        }

        m.visitInsn(Constants.RETURN);
    }
}
