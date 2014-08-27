// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.

import java.io.PrintStream;
import org.junit.Test;
import webit.script.asm.ASMUtil;
import webit.script.asm3.ClassWriter;
import webit.script.asm3.Opcodes;
import webit.script.asm3.Type;
import webit.script.asm3.commons.GeneratorAdapter;
import webit.script.asm3.commons.Method;

/**
 *
 * @author Zqq
 */
public class AsmTest extends ClassLoader implements Opcodes {

    @Test
    public void main() throws Exception {
        ClassWriter cw;
        byte[] code;
        AsmTest loader;
        Class<?> exampleClass;

        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_5, ACC_PUBLIC, "Example", null, "java/lang/Object", null);

        // creates a GeneratorAdapter for the (implicit) constructor
        Method m = ASMUtil.METHOD_DEFAULT_CONSTRUCTOR;

        final GeneratorAdapter mg_init = new GeneratorAdapter(ACC_PUBLIC, m, null, null,
                cw);
        mg_init.loadThis();
        mg_init.invokeConstructor(Type.getType(Object.class), m);
        mg_init.returnValue();
        mg_init.endMethod();

        // creates a GeneratorAdapter for the 'main' method
        m = new Method("main", "(I)V");
        final GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, m, null, null, cw);

        final int var_str = mg.newLocal(Type.getType(String.class));
        final int var_str2 = mg.newLocal(Type.getType(String.class));
        final int var_class = mg.newLocal(Type.getType(Class.class));

        final int var_out = mg.newLocal(Type.getType(PrintStream.class));

        mg.push(Type.getType(String.class));
        mg.storeLocal(var_class);

        //
        mg.getStatic(Type.getType(System.class), "out",
                Type.getType(PrintStream.class));
        mg.storeLocal(var_out);

        mg.push("Hello 1!");
        mg.storeLocal(var_str);

        //
        mg.push("Hello 2!");
        mg.storeLocal(var_str2);

        mg.loadLocal(var_out);
        mg.loadLocal(var_class);

        mg.invokeVirtual(Type.getType(PrintStream.class),
                new Method("println", "(Ljava/lang/Object;)V"));

        mg.returnValue();
        mg.endMethod();

        cw.visitEnd();

        code = cw.toByteArray();
        loader = new AsmTest();

        exampleClass = loader.defineClass("Example", code, 0, code.length);

        // uses the dynamically generated class to print 'Helloworld'
        exampleClass.getMethods()[0].invoke(null, new Object[]{1});
    }
}
