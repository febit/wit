// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.accessor.AccessorFactory;
import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Render;
import org.febit.wit.accessor.Setter;
import org.febit.wit.accessor.impl.BeanReflectAccessor;
import org.febit.wit.exceptions.UncheckedException;
import org.febit.wit.util.ClassMap;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.bean.PropertyInfo;
import org.febit.wit.util.bean.PropertyInfos;
import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.Label;
import org.febit.wit_shaded.asm.MethodWriter;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;

import static org.febit.wit.util.Defaults.nvl;

@Slf4j
public class AsmBeanAccessorFactory implements AccessorFactory {

    private static final BeanReflectAccessor FALLBACK = new BeanReflectAccessor();

    private static final String[] ASM_RESOLVER = {"org/febit/wit/asm/AsmAccessor"};
    private static final ClassMap<AsmAccessor> CACHE = new ClassMap<>();

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
    private AsmAccessor buildIfAbsent(Class<?> type) {
        AsmAccessor resolver = CACHE.get(type);
        if (resolver == null) {
            synchronized (CACHE) {
                resolver = CACHE.get(type);
                if (resolver == null) {
                    try {
                        resolver = (AsmAccessor) constructAccessorClassFor(type)
                                .getConstructor().newInstance();
                        resolver = CACHE.putIfAbsent(type, resolver);
                    } catch (Exception | LinkageError e) {
                        log.error("Failed to create resolver for: {}", type.getName(), e);
                    }
                }
            }
        }
        return resolver;
    }

    static Class<?> constructAccessorClassFor(Class<?> beanClass) {
        //XXX: rewrite
        if (!ClassUtils.isPublic(beanClass)) {
            throw new UncheckedException("Class<?> is not public: " + beanClass);
        }
        var className = "org.febit.wit.asm.Accessor" + AsmUtils.NEXT_SN.getAndIncrement();

        var classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL,
                AsmUtils.getInternalName(className), "java/lang/Object", ASM_RESOLVER);
        AsmUtils.visitConstructor(classWriter);

        var fields = PropertyInfos.resolve(beanClass)
                .sorted()
                .toArray(PropertyInfo[]::new);

        final int fieldCount = fields.length;
        int[] hashes = new int[fieldCount];
        int[] indexer = new int[fieldCount];
        if (fieldCount > 0) {
            int hashCount = 0;
            int hash;
            hashes[hashCount++] = hash = fields[0].name().hashCode();
            int i = 1;
            while (i < fieldCount) {
                PropertyInfo property = fields[i];
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
        final MethodWriter m = classWriter.visitMethod(Constants.ACC_PUBLIC, "getMatchClass",
                "()Ljava/lang/Class;", null);
        m.visitInsn(Constants.ACONST_NULL);
        m.visitInsn(Constants.ARETURN);
        m.visitMaxs();

        return AsmUtils.loadClass(className, classWriter);
    }

    private static void visitXetMethod(final boolean isGetter, final ClassWriter classWriter, final Class<?> beanClass,
                                       final PropertyInfo[] fields, final int[] hashes, final int[] indexer) {
        final String beanName = AsmUtils.getBoxedInternalName(beanClass);
        final MethodWriter m;
        if (isGetter) {
            m = classWriter.visitMethod(Constants.ACC_PUBLIC, "get",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null);
        } else {
            m = classWriter.visitMethod(Constants.ACC_PUBLIC, "set",
                    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", null);
        }
        final int fieldInfosLength = fields.length;
        if (fieldInfosLength != 0) {
            final Label finalEndLabel = new Label();
            if (fieldInfosLength < 4) {
                visitXetFields(isGetter, m, fields, 0, fieldInfosLength, beanName, finalEndLabel);
            } else {
                m.visitVarInsn(Constants.ALOAD, 2);
                m.invokeVirtual("java/lang/Object", "hashCode", "()I");

                final int size = hashes.length;
                Label[] labels = new Label[size];
                for (int i = 0; i < size; i++) {
                    labels[i] = new Label();
                }

                m.visitLookupSwitchInsn(finalEndLabel, hashes, labels);
                int start = 0;
                for (int i = 0; i < size; i++) {
                    int end = indexer[i];
                    m.visitLabel(labels[i]);
                    visitXetFields(isGetter, m, fields, start, end, beanName, finalEndLabel);
                    start = end;
                }
            }
            m.visitLabel(finalEndLabel);
        }
        //Exception
        m.visitTypeInsn(Constants.NEW, "org/febit/wit/exceptions/ScriptRuntimeException");
        m.visitInsn(Constants.DUP);
        m.visitLdcInsn("Invalid property " + beanClass.getName() + '#');
        m.visitVarInsn(Constants.ALOAD, 2);
        m.invokeStatic(AsmUtils.TYPE_STRING_NAME, "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
        m.invokeVirtual(AsmUtils.TYPE_STRING_NAME, "concat", "(Ljava/lang/String;)Ljava/lang/String;");
        m.visitMethodInsn(Constants.INVOKESPECIAL, "org/febit/wit/exceptions/ScriptRuntimeException",
                AsmUtils.METHOD_CTOR, "(Ljava/lang/String;)V");
        m.visitInsn(Constants.ATHROW);
        m.visitMaxs();
    }

    private static void visitXetFields(final boolean isGetter, final MethodWriter m,
                                       final PropertyInfo[] properties, final int start, final int end,
                                       final String beanName, final Label failedMatchLabel) {
        final Label[] gotoTable = new Label[end - start];
        //if ==
        for (int i = start; i < end; i++) {
            Label label = new Label();
            gotoTable[i - start] = label;
            m.visitLdcInsn(properties[i].name());
            m.visitVarInsn(Constants.ALOAD, 2);
            // if == goto
            m.visitJumpInsn(Constants.IF_ACMPEQ, label);
        }
        //if equals
        for (int i = start; i < end; i++) {
            m.visitLdcInsn(properties[i].name());
            m.visitVarInsn(Constants.ALOAD, 2);
            m.invokeVirtual(AsmUtils.TYPE_STRING_NAME, "equals", "(Ljava/lang/Object;)Z");
            // if true goto
            m.visitJumpInsn(Constants.IFNE, gotoTable[i - start]);
        }
        //failed, to end
        m.visitJumpInsn(Constants.GOTO, failedMatchLabel);
        //actions
        for (int i = start; i < end; i++) {
            m.visitLabel(gotoTable[i - start]);
            PropertyInfo info = properties[i];
            if (isGetter) {
                appendGetFieldCode(m, info, beanName);
            } else {
                appendSetFieldCode(m, info, beanName);
            }
        }
    }

    private static void appendGetFieldCode(final MethodWriter m, final PropertyInfo property, final String beanName) {
        var getter = property.getterMethod();
        var field = property.field();
        if (getter == null && field == null) {
            //Unreadable Exception
            AsmUtils.visitScriptRuntimeException(m, "Unreadable property "
                    + property.owner().getName() + "#" + property.name());
            return;
        }
        Class<?> resultType = getter != null ? getter.getReturnType() : field.getType();
        m.visitVarInsn(Constants.ALOAD, 1);
        m.checkCast(beanName);
        if (getter != null) {
            //return book.getName()
            m.invokeVirtual(beanName, getter.getName(), AsmUtils.getDescriptor(getter));
        } else {
            //return book.name
            m.visitFieldInsn(Constants.GETFIELD, beanName, property.name(), AsmUtils.getDescriptor(resultType));
        }
        AsmUtils.visitBoxIfNeed(m, resultType);
        m.visitInsn(Constants.ARETURN);
    }

    private static void appendSetFieldCode(final MethodWriter m, final PropertyInfo property, final String beanName) {
        var setter = property.setterMethod();
        var field = property.field();
        if (setter == null && (field == null || property.isReadonlyField())) {
            // Readonly Exception
            AsmUtils.visitScriptRuntimeException(m, "Readonly property "
                    + property.owner().getName() + "#" + property.name());
            return;
        }
        Class<?> fieldClass = setter != null
                ? setter.getParameterTypes()[0]
                : field.getType();
        m.visitVarInsn(Constants.ALOAD, 1);
        m.checkCast(beanName);
        m.visitVarInsn(Constants.ALOAD, 3);
        m.checkCast(AsmUtils.getBoxedInternalName(fieldClass));
        AsmUtils.visitUnboxIfNeed(m, fieldClass);
        if (setter != null) {
            //book.setName((String)name)
            m.invokeVirtual(beanName, setter.getName(), AsmUtils.getDescriptor(setter));
        } else {
            //book.name = (String) name
            m.visitFieldInsn(Constants.PUTFIELD, beanName, property.name(), AsmUtils.getDescriptor(fieldClass));
        }

        m.visitInsn(Constants.RETURN);
    }
}
