package webit.script.asm;

// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
import java.lang.reflect.InvocationTargetException;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.asm.lib.ClassWriter;
import webit.script.asm.lib.Constants;
import webit.script.asm.lib.Label;
import webit.script.asm.lib.MethodWriter;

/**
 *
 * @author Zqq
 */
public class AsmTest implements Constants {

    @Test
    public void test() throws Exception {
        ClassWriter clssWriter = new ClassWriter(Constants.V1_5, Constants.ACC_PUBLIC, "x/Example", "java/lang/Object", null);

        ASMUtil.visitConstructor(clssWriter);

        MethodWriter m = clssWriter.visitMethod(ACC_PUBLIC, "test", "([Ljava/lang/Object;)Ljava/lang/Object;", null);

        Label toExcaption = new Label();
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitJumpInsn(Constants.IFNULL, toExcaption);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ARRAYLENGTH);
        m.visitJumpInsn(Constants.IFEQ, toExcaption);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ICONST_0); //m.visitIntInsn(Constants.SIPUSH, 17);
        
        m.visitInsn(Constants.AALOAD);
        m.visitJumpInsn(Constants.IFNULL, toExcaption);
        m.visitVarInsn(Constants.ALOAD, 1);
        m.visitInsn(Constants.ICONST_0);
        m.visitInsn(Constants.AALOAD);
        m.visitTypeInsn(Constants.CHECKCAST, "java/lang/String");
        m.visitMethodInsn(Constants.INVOKEVIRTUAL, "java/lang/String", "length", "()I");
        ASMUtil.visitBoxIfNeed(m, int.class);
        m.visitInsn(Constants.ARETURN);
        m.visitLabel(toExcaption);
        ASMUtil.visitScriptRuntimeException(m, "First argument can't be null.");
        m.visitMaxs();

        Class exampleClass = ASMUtil.loadClass("x.Example", clssWriter);

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
