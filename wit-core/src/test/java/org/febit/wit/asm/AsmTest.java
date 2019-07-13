// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.asm;

import org.febit.wit_shaded.asm.ClassWriter;
import org.febit.wit_shaded.asm.Constants;
import org.febit.wit_shaded.asm.Label;
import org.febit.wit_shaded.asm.MethodWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author zqq90
 */
class AsmTest implements Constants {

    @Test
    void test() throws Exception {
        ClassWriter classWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC,
                "x/Example", "java/lang/Object", null);

        AsmUtil.visitConstructor(classWriter);

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
        AsmUtil.visitBoxIfNeed(m, int.class);
        m.visitInsn(Constants.ARETURN);
        m.visitLabel(toException);
        AsmUtil.visitScriptRuntimeException(m, "First argument can't be null.");
        m.visitMaxs();

        Class<?> exampleClass = AsmUtil.loadClass("x.Example", classWriter);

        Object obj = exampleClass.getConstructor().newInstance();
        Object result = exampleClass.getMethods()[0].invoke(obj, new Object[]{new Object[]{""}});
        assertEquals(0, result);
    }
}
