// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.Label;
import org.febit.wit_shaded.asm.MethodWriter;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

/**
 * @author zqq90
 */
public class AsmTest implements Constants {

    @Test
    public void test() throws Exception {
        ClassWriter classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC,
                "x/Example", "java/lang/Object", null);

        ASMUtil.visitConstructor(classWriter);

        MethodWriter m = classWriter.visitMethod(ACC_PUBLIC, "test",
                "([Ljava/lang/Object;)Ljava/lang/Object;", null);

        Label toException = new Label();
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitJumpInsn(Constants.IFNULL, toException);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ARRAYLENGTH);
        m.visitJumpInsn(Constants.IFEQ, toException);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ICONST_0);

        m.visitInsn(Constants.AALOAD);
        m.visitJumpInsn(Constants.IFNULL, toException);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ICONST_0);
        m.visitInsn(Constants.AALOAD);
        m.visitTypeInsn(Constants.CHECKCAST, "java/lang/String");
        m.visitMethodInsn(Constants.INVOKEVIRTUAL, "java/lang/String", "length", "()I");
        ASMUtil.visitBoxIfNeed(m, int.class);
        m.visitInsn(Constants.ARETURN);
        m.visitLabel(toException);
        ASMUtil.visitScriptRuntimeException(m, "First argument can't be null.");
        m.visitMaxs();

        Class<?> exampleClass = ASMUtil.loadClass("x.Example", classWriter);

        try {
            Object obj = exampleClass.newInstance();
            Object result = exampleClass.getMethods()[0].invoke(obj, new Object[]{new Object[]{""}});
            assertEquals(0, result);
        } catch (InvocationTargetException exception) {
            exception.getCause().printStackTrace();
            throw exception;
        }
    }
}
