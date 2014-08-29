// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import webit.script.asm.lib.ClassWriter;
import webit.script.asm.lib.Constants;
import webit.script.asm.lib.Label;
import webit.script.asm.lib.MethodWriter;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.resolvers.SetResolver;
import webit.script.util.ClassMap;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;
import webit.script.util.bean.FieldInfo;
import webit.script.util.bean.FieldInfoResolver;

/**
 *
 * @author Zqq
 */
public class AsmResolverManager extends ResolverManager {

    private static final String[] ASM_RESOLVER = new String[]{"webit/script/asm/AsmResolver"};
    private static final ClassMap<AsmResolver> CACHE = new ClassMap<AsmResolver>();

    @Override
    protected SetResolver resolveSetResolver(Class type) {
        SetResolver resolver = getAsmResolver(type);
        if (resolver != null) {
            return resolver;
        }
        return super.resolveSetResolver(type);
    }

    @Override
    protected GetResolver resolveGetResolver(Class type) {
        GetResolver resolver = getAsmResolver(type);
        if (resolver != null) {
            return resolver;
        }
        return super.resolveGetResolver(type);
    }

    private AsmResolver getAsmResolver(Class type) {
        AsmResolver resolver = CACHE.get(type);
        if (resolver == null) {
            synchronized (CACHE) {
                resolver = CACHE.get(type);
                if (resolver == null) {
                    try {
                        resolver = (AsmResolver) createResolverClass(type).newInstance();
                        resolver = CACHE.putIfAbsent(type, resolver);
                    } catch (Throwable e) {
                        logger.error("Failed to create resolver for:".concat(type.getName()), e);
                    }
                }
            }
        }
        return resolver;
    }

    static Class createResolverClass(Class beanClass) throws Exception {

        if (ClassUtil.isPublic(beanClass)) {
            final String className = "webit.script.asm.Resolver_".concat(ASMUtil.getSn());

            final ClassWriter classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC + Constants.ACC_FINAL, ASMUtil.getInternalName(className), "java/lang/Object", ASM_RESOLVER);
            ASMUtil.visitConstructor(classWriter);

            final String beanName = ASMUtil.getBoxedInternalName(beanClass);

            //getMatchClass
            final MethodWriter m = classWriter.visitMethod(Constants.ACC_PUBLIC, "getMatchClass", "()Ljava/lang/Class;", null);
            m.pushClass(beanName);
            m.visitInsn(Constants.ARETURN);
            m.visitMaxs();

            final FieldInfo[] all = FieldInfoResolver.resolve(beanClass);
            Arrays.sort(all);
            final int size = all.length;
            int[] hashs;
            int[] indexer;
            if (size > 0) {
                hashs = new int[size];
                indexer = new int[size];
                int hashsCount = 0;
                int hash;
                hashs[hashsCount++] = hash = all[0].hashCode;
                int i = 1;
                while (i < size) {
                    FieldInfo fieldInfo = all[i];
                    if (hash != fieldInfo.hashCode) {
                        indexer[hashsCount - 1] = i;
                        hashs[hashsCount++] = hash = fieldInfo.hashCode;
                    }
                    i++;
                }
                indexer[hashsCount - 1] = size;
                hashs = Arrays.copyOf(hashs, hashsCount);
                indexer = Arrays.copyOf(indexer, hashsCount);
            } else {
                hashs = null;
                indexer = null;
            }

            visitXetMethod(true, classWriter, beanClass, all, hashs, indexer);
            visitXetMethod(false, classWriter, beanClass, all, hashs, indexer);

            return ASMUtil.loadClass(className, classWriter);
        } else {
            throw new Exception(StringUtil.format("Class [{}] is not public", beanClass));
        }
    }

    private static void visitXetMethod(boolean isGetter, final ClassWriter classWriter, final Class beanClass, final FieldInfo[] all, int[] hashs, int[] indexer) {
        final String beanName = ASMUtil.getBoxedInternalName(beanClass);
        final MethodWriter m;
        if (isGetter) {
            m = classWriter.visitMethod(Constants.ACC_PUBLIC, "get", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null);
        } else {
            m = classWriter.visitMethod(Constants.ACC_PUBLIC, "set", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", null);
        }
        final int fieldInfosLength = all.length;
        if (fieldInfosLength != 0) {
            final Label end_switch = new Label();
            if (fieldInfosLength < 4) {
                visitXetFields(isGetter, m, all, 0, fieldInfosLength, beanName, end_switch);
            } else {
                m.visitVarInsn(Constants.ALOAD, 2);
                m.invokeVirtual("java/lang/Object", "hashCode", "()I");

                final int size = hashs.length;
                Label[] labels = new Label[size];
                for (int i = 0; i < size; i++) {
                    labels[i] = new Label();
                }

                m.visitLookupSwitchInsn(end_switch, hashs, labels);
                int start = 0;
                for (int i = 0; i < size; i++) {
                    int end = indexer[i];
                    m.visitLabel(labels[i]);
                    visitXetFields(isGetter, m, all, start, end, beanName, end_switch);
                    start = end;
                }
            }
            m.visitLabel(end_switch);
        }
        //Exception
        m.visitTypeInsn(Constants.NEW, "webit/script/exceptions/ScriptRuntimeException");
        m.visitInsn(Constants.DUP);
        m.visitLdcInsn(StringUtil.concat("Invalid property ", beanClass.getName(), "#"));
        m.visitVarInsn(Constants.ALOAD, 2);
        m.invokeStatic("java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
        m.invokeVirtual("java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
        m.visitMethodInsn(Constants.INVOKESPECIAL, "webit/script/exceptions/ScriptRuntimeException", "<init>", "(Ljava/lang/String;)V");
        m.visitInsn(Constants.ATHROW);
        m.visitMaxs();
    }

    private static void visitXetFields(boolean isGetter, final MethodWriter m, FieldInfo[] fieldInfos, final int start, final int end, String beanName, Label l_failedMatch) {
        Label[] gotoTable = new Label[end - start];
        //if equals
        for (int i = start; i < end; i++) {
            Label label = gotoTable[i - start] = new Label();
            m.visitLdcInsn(fieldInfos[i].name);
            m.visitVarInsn(Constants.ALOAD, 2);
            m.invokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z");
            // if true goto
            m.visitJumpInsn(Constants.IFNE, label);
        }
        //failed, to end
        m.visitJumpInsn(Constants.GOTO, l_failedMatch);
        //actions
        for (int i = start; i < end; i++) {
            m.visitLabel(gotoTable[i - start]);
            FieldInfo info = fieldInfos[i];
            if (isGetter) {
                appendGetFieldCode(m, info, beanName);
            } else {
                appendSetFieldCode(m, info, beanName);
            }
        }
    }

    private static void appendGetFieldCode(final MethodWriter m, final FieldInfo fieldInfo, final String beanName) {
        Method getter = fieldInfo.getGetterMethod();
        Field field = fieldInfo.getField();
        if (getter != null || field != null) {
            Class resultType = getter != null ? getter.getReturnType() : field.getType();
            m.visitVarInsn(Constants.ALOAD, 1);
            m.checkCast(beanName);
            if (getter != null) {
                //return book.getName()
                m.invokeVirtual(beanName, getter.getName(), ASMUtil.getDescriptor(getter));
            } else {
                //return book.name
                m.visitFieldInsn(Constants.GETFIELD, beanName, fieldInfo.name, ASMUtil.getDescriptor(resultType));
            }
            ASMUtil.visitBoxIfNeed(m, resultType);
            m.visitInsn(Constants.ARETURN);
        } else {
            //Unreadable Exception
            ASMUtil.visitScriptRuntimeException(m, StringUtil.concat("Unreadable property ", fieldInfo.parent.getName(), "#", fieldInfo.name));
        }
    }

    private static void appendSetFieldCode(final MethodWriter m, FieldInfo fieldInfo, String beanName) {
        Method setter = fieldInfo.getSetterMethod();
        Field field = fieldInfo.getField();
        if (setter != null || (field != null && !fieldInfo.isIsFinal())) {
            Class fieldClass = setter != null ? setter.getParameterTypes()[0] : field.getType();
            m.visitVarInsn(Constants.ALOAD, 1);
            m.checkCast(beanName);
            m.visitVarInsn(Constants.ALOAD, 3);
            m.checkCast(ASMUtil.getBoxedInternalName(fieldClass));
            ASMUtil.visitUnboxIfNeed(m, fieldClass);
            if (setter != null) {
                //book.setName((String)name)
                m.invokeVirtual(beanName, setter.getName(), ASMUtil.getDescriptor(setter));
            } else {
                //book.name = (String) name
                m.visitFieldInsn(Constants.PUTFIELD, beanName, fieldInfo.name, ASMUtil.getDescriptor(fieldClass));
            }

            m.visitInsn(Constants.RETURN);
        } else {
            //UnwriteableException
            ASMUtil.visitScriptRuntimeException(m, StringUtil.concat("Unwriteable property ", fieldInfo.parent.getName(), "#", fieldInfo.name));
        }
    }
}
