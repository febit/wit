// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.io.PrintStream;
import org.junit.Test;
import webit.script.asm3.ClassWriter;
import webit.script.asm3.Opcodes;
import webit.script.asm3.Type;
import webit.script.asm3.commons.GeneratorAdapter;
import webit.script.asm3.commons.Method;
import webit.script.resolvers.MatchMode;

/**
 *
 * @author Zqq
 */
public class AsmTest extends ClassLoader implements Opcodes {


    //@Test
    public void main() throws Exception {
        ClassWriter cw;
        byte[] code;
        AsmTest loader;
        Class<?> exampleClass;

        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_5, ACC_PUBLIC, "Example", null, "java/lang/Object", null);

        // creates a GeneratorAdapter for the (implicit) constructor
        Method m = Method.getMethod("void <init> ()");

        final GeneratorAdapter mg_init = new GeneratorAdapter(ACC_PUBLIC, m, null, null,
                cw);
        mg_init.loadThis();
        mg_init.invokeConstructor(Type.getType(Object.class), m);
        mg_init.returnValue();
        mg_init.endMethod();

        // creates a GeneratorAdapter for the 'main' method
        m = Method.getMethod("void main (int)");
        final GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, m, null, null, cw);

        final int var_str = mg.newLocal(Type.getType(String.class));
        final int var_str2 = mg.newLocal(Type.getType(String.class));
        final int var_enum = mg.newLocal(Type.getType(MatchMode.class));
        final int var_class = mg.newLocal(Type.getType(Class.class));


        final int var_out = mg.newLocal(Type.getType(PrintStream.class));


        mg.push(Type.getType(String.class));
        //mg.visitLdcInsn(Type.getType(String.class));
        mg.storeLocal(var_class);
        
        //
        mg.getStatic(Type.getType(MatchMode.class), "INSTANCEOF",
                Type.getType(MatchMode.class));
        mg.storeLocal(var_enum);


        
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
                Method.getMethod("void println (Object)"));
        
//        mg.loadLocal(var_out);
//        mg.loadLocal(var_enum);
//
//        mg.invokeVirtual(Type.getType(PrintStream.class),
//                Method.getMethod("void println (Object)"));


//        mg.loadArg(0);
//
//        int[] keys = new int[]{1, 100, 3};
//        Arrays.sort(keys);
//
//        mg.tableSwitch(keys, new TableSwitchGenerator() {
//            public void generateCase(int i, Label label) {
//                switch (i) {
//                    case 1:
//
//                        mg.loadLocal(var_out);
//                        mg.loadLocal(var_str);
//                        mg.invokeVirtual(Type.getType(PrintStream.class),
//                                Method.getMethod("void println (String)"));
//                        mg.goTo(label);
//                        break;
//                    case 2:
//                        mg.loadLocal(var_out);
//                        mg.loadLocal(var_str2);
//                        mg.invokeVirtual(Type.getType(PrintStream.class),
//                                Method.getMethod("void println (String)"));
//                        mg.goTo(label);
//                        break;
//                    case 100:
//                        mg.loadLocal(var_out);
//                        mg.push(100);
//                        mg.invokeVirtual(Type.getType(PrintStream.class),
//                                Method.getMethod("void println (int)"));
//                        mg.goTo(label);
//                        break;
//                    default:
//                        mg.goTo(label);
//                }
//            }
//
//            public void generateDefault() {
////                mg.loadLocal(var_out);
////                mg.loadArg(0);
////                mg.invokeVirtual(Type.getType(PrintStream.class),
////                Method.getMethod("void println (int)"));
////                mg.goTo(switch_end);
//            }
//        });

        //
//        mg.push(Type.LONG_TYPE);
//        //mg.checkCast(Type.getType(Class.class));
//        mg.storeLocal(var_class);
//        






//        
//        //out len
//        mg.loadLocal(var_out);
//        mg.loadLocal(var_arg1_length);
//        
//        mg.invokeVirtual(Type.getType(PrintStream.class),
//                Method.getMethod("void println (int)"));




//        mg.loadLocal(var_out);
//        mg.loadLocal(var_str);
//        mg.invokeVirtual(Type.getType(String.class),
//                Method.getMethod("int hashCode ()"));
//        
//        mg.invokeVirtual(Type.getType(PrintStream.class),
//                Method.getMethod("void println (int)"));





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
