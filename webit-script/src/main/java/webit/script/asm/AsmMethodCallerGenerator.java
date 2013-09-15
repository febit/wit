// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.lang.reflect.Constructor;
import webit.script.asm4.ClassWriter;
import webit.script.asm4.Label;
import webit.script.asm4.Opcodes;
import webit.script.asm4.Type;
import webit.script.asm4.commons.GeneratorAdapter;
import webit.script.asm4.commons.Method;
import webit.script.util.ClassUtil;

/**
 *
 * @author Zqq
 */
public class AsmMethodCallerGenerator {

    //
    private static final String CALLER_CLASS_NAME_PRE = AsmMethodCallerGenerator.class.getName() + "_";
    private static final String ASM_METHOD_CALLER = ASMUtil.toAsmClassName(AsmMethodCaller.class.getName());


    protected static String generateClassName(java.lang.reflect.Method method) {
        return CALLER_CLASS_NAME_PRE + method.getName() + '_' + ASMUtil.getSn();
    }

    protected static String generateClassName(Constructor constructor) {
        return CALLER_CLASS_NAME_PRE + constructor.getDeclaringClass().getSimpleName() + '_' + ASMUtil.getSn();
    }

    protected byte[] generateClassBody(String className, Constructor constructor) {

        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, ASMUtil.toAsmClassName(className), null, ASM_METHOD_CALLER, null);

        //Default Constructor
        attachDefaultConstructorMethod(classWriter);

        attach_execute_Method(classWriter, constructor);

        //End Class Writer
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    protected byte[] generateClassBody(String className, java.lang.reflect.Method method) {

        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, ASMUtil.toAsmClassName(className), null, ASM_METHOD_CALLER, null);

        //Default Constructor
        attachDefaultConstructorMethod(classWriter);

        attach_execute_Method(classWriter, method);

        //End Class Writer
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private void attachDefaultConstructorMethod(ClassWriter classWriter) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_DEFAULT_CONSTRUCTOR, null, null, classWriter);

        mg.loadThis();
        mg.invokeConstructor(ASMUtil.TYPE_ASM_CALLER, ASMUtil.METHOD_DEFAULT_CONSTRUCTOR);
        mg.returnValue();
        mg.endMethod();
    }

    public Class generateCaller(java.lang.reflect.Method method) {
        String className = generateClassName(method);

        byte[] code = generateClassBody(className, method);
        /*
         try {
         FileUtil.writeBytes("F:/temp_" + method.getName() + ".class", code);
         } catch (IOException ex) {
         //ignore
         }*/
        return ASMUtil.loadClass(className, code, 0, code.length);
    }

    public Class generateCaller(Constructor constructor) {
        String className = generateClassName(constructor);

        byte[] code = generateClassBody(className, constructor);
        /*
         try {
         FileUtil.writeBytes("F:/temp_" + method.getName() + ".class", code);
         } catch (IOException ex) {
         //ignore
         }*/
        return ASMUtil.loadClass(className, code, 0, code.length);
    }

    private void attach_execute_Method(ClassWriter classWriter, Constructor constructor) {

        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_EXECUTE, null, null, classWriter);

        final Class[] paramTypes = constructor.getParameterTypes();
        final Class declaringClass = constructor.getDeclaringClass();
        final Type declaringClassType = Type.getType(declaringClass);
        final Method asmMethod = Method.getMethod(constructor);

        // new
        mg.newInstance(declaringClassType);
        mg.dup();

        final int argsNeed = paramTypes.length;
        if (argsNeed > 0) {

            final int var_args = mg.newLocal(ASMUtil.TYPE_OBJECT_ARR);
            Label pushArgs = mg.newLabel();
            Label createNewArray = mg.newLabel();
            //Object[] args = ;
            mg.loadArg(0);
            mg.dup();
            mg.storeLocal(var_args);
            //check args count

            mg.ifNull(createNewArray);
            mg.loadArg(0);
            mg.arrayLength();
            mg.push(argsNeed);
            mg.ifICmp(GeneratorAdapter.GE, pushArgs);
            //copyArray
            //System.arraycopy(src, srcPos, dest, destPos, length);
            //src
            mg.loadArg(0);
            //srcPos
            mg.push(0);
            //dest
            mg.push(argsNeed);
            mg.newArray(ASMUtil.TYPE_OBJECT);
            mg.dup();
            mg.storeLocal(var_args);
            //destPos
            mg.push(0);
            //length
            mg.loadArg(0);
            mg.arrayLength();

            mg.invokeStatic(ASMUtil.TYPE_SYSTEM, ASMUtil.METHOD_ARRAY_COPY);

            mg.goTo(pushArgs);

            //createNewArray
            mg.mark(createNewArray);
            mg.push(argsNeed);
            mg.newArray(ASMUtil.TYPE_OBJECT);
            mg.storeLocal(var_args);

            //
            mg.mark(pushArgs);

            for (int i = 0; i < argsNeed; i++) {

                Class paramType = paramTypes[i];
                Class boxedParamType = ClassUtil.getBoxedClass(paramType);

                mg.loadLocal(var_args);
                mg.push(i);
                mg.arrayLoad(ASMUtil.TYPE_OBJECT);
                mg.checkCast(Type.getType(boxedParamType));
                if (paramType != boxedParamType) {
                    mg.invokeStatic(ASMUtil.TYPE_CLASS_UTIL, ASMUtil.getUnBoxMethod(paramType));
                }
            }
        }

        //Call Constructor
        mg.invokeConstructor(declaringClassType, asmMethod);

        mg.returnValue();
        mg.endMethod();
    }

    private void attach_execute_Method(ClassWriter classWriter, java.lang.reflect.Method method) {

        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_EXECUTE, null, null, classWriter);

        final Class declaringClass = method.getDeclaringClass();
        final Method asmMethod = Method.getMethod(method);
        final Type declaringClassType = Type.getType(declaringClass);
        final boolean isStatic = ClassUtil.isStatic(method);
        final Class[] paramTypes = method.getParameterTypes();

        if (isStatic && paramTypes.length == 0) {
            mg.invokeStatic(declaringClassType, asmMethod);
        } else {
            final int var_args = mg.newLocal(ASMUtil.TYPE_OBJECT_ARR);

            final int argsNeed = isStatic ? paramTypes.length : paramTypes.length + 1;

            Label pushArgs = mg.newLabel();
            Label createNewArray = mg.newLabel();
            Label firstIsNullException = mg.newLabel();

            mg.loadArg(0);
            mg.dup();
            mg.storeLocal(var_args);
            //check args count
            if (isStatic) {
                mg.ifNull(createNewArray);
            } else {
                mg.ifNull(firstIsNullException);
                mg.loadLocal(var_args);
                mg.arrayLength();
                mg.ifZCmp(GeneratorAdapter.EQ, firstIsNullException);
                mg.loadLocal(var_args);
                mg.push(0);
                mg.arrayLoad(ASMUtil.TYPE_OBJECT);
                mg.ifNull(firstIsNullException);
            }
            mg.loadArg(0);
            mg.arrayLength();
            mg.push(argsNeed);
            mg.ifICmp(GeneratorAdapter.GE, pushArgs);
            //copyArray
            //System.arraycopy(src, srcPos, dest, destPos, length);
            //src
            mg.loadArg(0);
            //srcPos
            mg.push(0);
            //dest
            mg.push(argsNeed);
            mg.newArray(ASMUtil.TYPE_OBJECT);
            mg.dup();
            mg.storeLocal(var_args);
            //destPos
            mg.push(0);
            //length
            mg.loadArg(0);
            mg.arrayLength();

            mg.invokeStatic(ASMUtil.TYPE_SYSTEM, ASMUtil.METHOD_ARRAY_COPY);

            mg.goTo(pushArgs);

            if (isStatic) {
                //createNewArray
                mg.mark(createNewArray);
                mg.push(argsNeed);
                mg.newArray(ASMUtil.TYPE_OBJECT);
                mg.storeLocal(var_args);
            } else {
                mg.mark(firstIsNullException);
                mg.push("The first argument of this method can't be null");
                mg.invokeStatic(ASMUtil.TYPE_ASM_CALLER, ASMUtil.METHOD_CREATE_EXCEPTION);
                mg.throwException();
            }

            //
            mg.mark(pushArgs);

            int i_args = 0;
            if (isStatic == false) {
                mg.loadLocal(var_args);
                mg.push(0);
                mg.arrayLoad(ASMUtil.TYPE_OBJECT);
                mg.checkCast(declaringClassType);
                i_args = 1;
            }

            for (int i_paramType = 0; i_paramType < paramTypes.length; i_args++, i_paramType++) {

                Class paramType = paramTypes[i_paramType];
                Class boxedParamType = ClassUtil.getBoxedClass(paramType);

                mg.loadLocal(var_args);
                mg.push(i_args);
                mg.arrayLoad(ASMUtil.TYPE_OBJECT);
                mg.checkCast(Type.getType(boxedParamType));
                if (paramType != boxedParamType) {
                    mg.invokeStatic(ASMUtil.TYPE_CLASS_UTIL, ASMUtil.getUnBoxMethod(paramType));
                }
            }

            //Call Method
            if (isStatic) {
                mg.invokeStatic(declaringClassType, asmMethod);
            } else {
                if (method.getDeclaringClass().isInterface()) {
                    mg.invokeInterface(declaringClassType, asmMethod);
                } else {
                    mg.invokeVirtual(declaringClassType, asmMethod);
                }
            }
        }
        // return result; //  void, Boxed Object
        Class methodReturnType = method.getReturnType();
        if (methodReturnType == void.class) {
            // return Context.VOID;
            mg.getStatic(ASMUtil.TYPE_CONTEXT, "VOID", ASMUtil.TYPE_OBJECT);
        } else {
            Method boxMethod = ASMUtil.getBoxMethod(methodReturnType);
            if (boxMethod != null) {
                mg.invokeStatic(ASMUtil.TYPE_CLASS_UTIL, boxMethod);
            }
        }
        mg.returnValue();
        mg.endMethod();
    }
}
