/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webit.script.asm;

import java.util.HashMap;
import java.util.Map;
import webit.script.asm4.ClassWriter;
import webit.script.asm4.Label;
import webit.script.asm4.Opcodes;
import webit.script.asm4.Type;
import webit.script.asm4.commons.GeneratorAdapter;
import webit.script.asm4.commons.Method;
import webit.script.asm4.commons.TableSwitchGenerator;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.asm.ASMUtil;
import webit.script.resolvers.AsmResolver;
import webit.script.util.FieldInfo;
import webit.script.util.FieldInfoResolver;
import webit.script.util.ClassUtil;

/**
 *
 * @author Zqq
 */
public class AsmResolverGenerator implements Opcodes {

    private static final String RESOLVERS_CLASS_NAME_PRE = "webit.script.resolvers.impl.Resolver_";
    //
    private static final Type TYPE_ASM_RESOLVER = Type.getType(AsmResolver.class);
    //
    private static final Method METHOD_HASH_CODE = new Method("hashCode", Type.INT_TYPE, new Type[0]);
    private static final Method METHOD_EQUALS = new Method("equals", Type.BOOLEAN_TYPE, new Type[]{ASMUtil.TYPE_OBJECT});
    //
    private static final Method METHOD_GET = new Method("get", ASMUtil.TYPE_OBJECT, new Type[]{ASMUtil.TYPE_OBJECT, ASMUtil.TYPE_OBJECT});
    private static final Method METHOD_SET = new Method("set", Type.BOOLEAN_TYPE, new Type[]{ASMUtil.TYPE_OBJECT, ASMUtil.TYPE_OBJECT, ASMUtil.TYPE_OBJECT});
    private static final Method METHOD_CONSTRUCTOR_ASM_RESOLVER = new Method("<init>", Type.VOID_TYPE, new Type[]{Type.getType(Class.class)});
    private static final Method METHOD_CREATE_NOSUCHPROPERTY_EXCEPTION = new Method("createNoSuchPropertyException", Type.getType(ScriptRuntimeException.class), new Type[]{ASMUtil.TYPE_OBJECT});
    private static final Method METHOD_CREATE_UNWRITE_EXCEPTION = new Method("createUnwriteablePropertyException", Type.getType(ScriptRuntimeException.class), new Type[]{ASMUtil.TYPE_OBJECT});
    private static final Method METHOD_CREATE_UNREAD_EXCEPTION = new Method("createUnreadablePropertyException", Type.getType(ScriptRuntimeException.class), new Type[]{ASMUtil.TYPE_OBJECT});
    //

    //
    private static int sn = 1;

    private static synchronized int getSn() {
        return sn++;
    }

    protected static String generateClassName(Class beanClass) {
        return RESOLVERS_CLASS_NAME_PRE + beanClass.getSimpleName() + '_' + getSn();
    }

    protected byte[] generateClassBody(String className, Class beanClass) {
        String asmClassName = ASMUtil.toAsmClassName(className);
        ClassWriter classWriter;

        classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(V1_5, ACC_PUBLIC, asmClassName, null, "webit/script/resolvers/AsmResolver", null);

        //Default Constructor
        attachDefaultConstructorMethod(classWriter, beanClass);

        attach_get_Method(classWriter, beanClass);
        attach_set_Method(classWriter, beanClass);

        //End Class Writer
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
    //

    private void attach_get_Method(ClassWriter classWriter, Class beanClass) {
        final GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, METHOD_GET, null, null, classWriter);
        final Type beanType = Type.getType(beanClass);

        //Book book = (Book) bean;
        final int var_entity = mg.newLocal(beanType);
        mg.loadArg(0);
        mg.checkCast(beanType);
        mg.storeLocal(var_entity);


        //Prepare FieldInfo
        final FieldInfo[] fieldInfos = new FieldInfoResolver(beanClass).resolver();
        if (fieldInfos.length == 0) {
            //Do nothing
        } else if (fieldInfos.length <= 2) {

            Label if_final_end = mg.newLabel();
            for (int i = 0; i < fieldInfos.length; i++) {
                FieldInfo fieldInfo = fieldInfos[i];
                Label if_end = mg.newLabel();

                attachIfFieldMatchCode(mg, fieldInfo, if_end);
                attachGetFieldCode(mg, fieldInfo, var_entity, beanType);

                mg.goTo(if_final_end);//goto final-end
                mg.mark(if_end);//end if
            }

            mg.mark(if_final_end); //end else if
        } else {
            //Switch(hashcode){}
            int[] hashcodes = new int[fieldInfos.length];

            hashcodes[0] = fieldInfos[0].getHashCode();
            int currindex = 0;
            final Map<Integer, Integer> hashIndexMap = new HashMap<Integer, Integer>();
            hashIndexMap.put(hashcodes[0], 0);


            for (int i = 1; i < fieldInfos.length; i++) {
                int hash = fieldInfos[i].getHashCode();
                if (hash != hashcodes[currindex]) {
                    hashcodes[++currindex] = hash;
                    hashIndexMap.put(hash, i);
                }
            }

            int realLength = currindex + 1;
            if (realLength < fieldInfos.length) {
                int[] old = hashcodes;
                hashcodes = new int[realLength];
                System.arraycopy(old, 0, hashcodes, 0, realLength);
            }


            //int hashcode = property.hashCode();
            //int var_hashcode = mg.newLocal(Type.INT_TYPE);
            mg.loadArg(1); //property
            mg.invokeVirtual(ASMUtil.TYPE_OBJECT, METHOD_HASH_CODE);
            //mg.storeLocal(var_hashcode);

            final Label end_switch = mg.newLabel();
            //
            mg.tableSwitch(hashcodes, new TableSwitchGenerator() {
                public void generateCase(int hash, Label end) {
                    int index = hashIndexMap.get(hash);
                    Label if_final_end = mg.newLabel();
                    for (; index < fieldInfos.length && hash == fieldInfos[index].getHashCode(); index++) {
                        Label if_end = mg.newLabel();

                        FieldInfo fieldInfo = fieldInfos[index];
                        attachIfFieldMatchCode(mg, fieldInfo, if_end);
                        attachGetFieldCode(mg, fieldInfo, var_entity, beanType);

                        mg.goTo(if_final_end);//goto final-end
                        mg.mark(if_end);//end if

                    }
                    mg.mark(if_final_end); //end else if
                    mg.goTo(end_switch);
                }

                public void generateDefault() {
                    //Do nothing
                }
            });

            mg.mark(end_switch);
        }
        //
        //mg.returnValue();
        //Exception
        mg.loadThis(); //this.
        mg.loadArg(1); //property
        mg.invokeVirtual(TYPE_ASM_RESOLVER, METHOD_CREATE_NOSUCHPROPERTY_EXCEPTION);
        mg.throwException();
        mg.endMethod();
    }

    private void attach_set_Method(ClassWriter classWriter, Class beanClass) {
        final GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, METHOD_SET, null, null, classWriter);
        final Type beanType = Type.getType(beanClass);

        //Book book = (Book) bean;
        final int var_entity = mg.newLocal(beanType);
        mg.loadArg(0);
        mg.checkCast(beanType);
        mg.storeLocal(var_entity);


        //Prepare FieldInfo
        final FieldInfo[] fieldInfos = new FieldInfoResolver(beanClass).resolver();
        if (fieldInfos.length == 0) {
            //Do nothing
        } else if (fieldInfos.length <= 2) {

            Label if_final_end = mg.newLabel();
            for (int i = 0; i < fieldInfos.length; i++) {
                FieldInfo fieldInfo = fieldInfos[i];
                Label if_end = mg.newLabel();

                attachIfFieldMatchCode(mg, fieldInfo, if_end);
                attachSetFieldCode(mg, fieldInfo, var_entity, beanType);

                mg.goTo(if_final_end);//goto final-end
                mg.mark(if_end);//end if

            }
            mg.mark(if_final_end); //end else if
        } else {
            //Switch(hashcode){}
            int[] hashcodes = new int[fieldInfos.length];

            hashcodes[0] = fieldInfos[0].getHashCode();
            int currindex = 0;
            final Map<Integer, Integer> hashIndexMap = new HashMap<Integer, Integer>();
            hashIndexMap.put(hashcodes[0], 0);


            for (int i = 1; i < fieldInfos.length; i++) {
                int hash = fieldInfos[i].getHashCode();
                if (hash != hashcodes[currindex]) {
                    hashcodes[++currindex] = hash;
                    hashIndexMap.put(hash, i);
                }
            }

            int realLength = currindex + 1;
            if (realLength < fieldInfos.length) {
                int[] old = hashcodes;
                hashcodes = new int[realLength];
                System.arraycopy(old, 0, hashcodes, 0, realLength);
            }


            //int hashcode = property.hashCode();
            //int var_hashcode = mg.newLocal(Type.INT_TYPE);
            mg.loadArg(1); //property
            mg.invokeVirtual(ASMUtil.TYPE_OBJECT, METHOD_HASH_CODE);
            //mg.storeLocal(var_hashcode);

            final Label end_switch = mg.newLabel();
            //
            mg.tableSwitch(hashcodes, new TableSwitchGenerator() {
                public void generateCase(int hash, Label end) {
                    int index = hashIndexMap.get(hash);
                    Label if_final_end = mg.newLabel();
                    for (; index < fieldInfos.length && hash == fieldInfos[index].getHashCode(); index++) {
                        Label if_end = mg.newLabel();

                        FieldInfo fieldInfo = fieldInfos[index];

                        attachIfFieldMatchCode(mg, fieldInfo, if_end);
                        attachSetFieldCode(mg, fieldInfo, var_entity, beanType);

                        mg.goTo(if_final_end);//goto final-end
                        mg.mark(if_end);//end if

                    }
                    mg.mark(if_final_end); //end else if
                    mg.goTo(end_switch);
                }

                public void generateDefault() {
                    //Do nothing
                }
            });

            mg.mark(end_switch);
        }
        //
        //mg.returnValue();
        //Exception
        mg.loadThis(); //this.
        mg.loadArg(1); //property
        mg.invokeVirtual(TYPE_ASM_RESOLVER, METHOD_CREATE_NOSUCHPROPERTY_EXCEPTION);
        mg.throwException();
        mg.endMethod();
    }

    private void attachReturnTrueCode(GeneratorAdapter mg) {
        mg.push(true);
        mg.returnValue();
    }

    private void attachIfFieldMatchCode(GeneratorAdapter mg, FieldInfo fieldInfo, Label elseJumpTo) {
        mg.loadArg(1); //property
        mg.push(fieldInfo.getName());
        mg.invokeVirtual(ASMUtil.TYPE_OBJECT, METHOD_EQUALS);
        mg.ifZCmp(GeneratorAdapter.EQ, elseJumpTo); // if == 0 jump
    }

    private void attachSetFieldCode(GeneratorAdapter mg, FieldInfo fieldInfo, int var_entity, Type beanType) {
        java.lang.reflect.Method setter = fieldInfo.getSetterMethod();
        if (setter != null) {
            //return book.setName((String)name);

            Class fieldClass = setter.getParameterTypes()[0];
            Class boxedClass = ClassUtil.getBoxedClass(fieldClass);

            Type boxedType = Type.getType(boxedClass);

            Method setterMethod = Method.getMethod(setter);
            mg.loadLocal(var_entity);
            mg.loadArg(2);
            mg.checkCast(boxedType);
            if (boxedClass != fieldClass) {
                mg.invokeStatic(ASMUtil.TYPE_CLASS_UTIL, ASMUtil.getUnBoxMethod(fieldClass));
            }
            mg.invokeVirtual(beanType, setterMethod);
            attachReturnTrueCode(mg);
        } else if (fieldInfo.getField() != null && fieldInfo.isIsFinal() == false) {
            //return book.name = (String) name;

            Class fieldClass = fieldInfo.getField().getType();
            Class boxedClass = ClassUtil.getBoxedClass(fieldClass);
            Type fieldType = Type.getType(fieldClass);
            Type boxedType = (fieldClass == boxedClass) ? fieldType : Type.getType(boxedClass);

            mg.loadLocal(var_entity);
            mg.loadArg(2);
            mg.checkCast(boxedType);
            if (boxedClass != fieldClass) {
                mg.invokeStatic(ASMUtil.TYPE_CLASS_UTIL, ASMUtil.getUnBoxMethod(fieldClass));
            }
            mg.putField(beanType, fieldInfo.getName(), fieldType);
            attachReturnTrueCode(mg);
        } else {
            //Exception
            mg.loadThis(); //this.
            mg.loadArg(1); //property
            mg.invokeVirtual(TYPE_ASM_RESOLVER, METHOD_CREATE_UNWRITE_EXCEPTION);
            mg.throwException();
        }
    }

    private void attachGetFieldCode(GeneratorAdapter mg, FieldInfo fieldInfo, int var_entity, Type beanType) {
        java.lang.reflect.Method getter = fieldInfo.getGetterMethod();
        if (getter != null) {
            //return book.getName();
            Class valueType = getter.getReturnType();
            Method boxMethod = ASMUtil.getBoxMethod(valueType);

            Method getterMethod = Method.getMethod(getter);
            mg.loadLocal(var_entity);
            mg.invokeVirtual(beanType, getterMethod);
            if (boxMethod != null) {
                mg.invokeStatic(ASMUtil.TYPE_CLASS_UTIL, boxMethod);
            }
            mg.returnValue();
        } else if (fieldInfo.getField() != null) {
            //return book.name;

            Class valueType = fieldInfo.getField().getType();
            Method boxMethod = ASMUtil.getBoxMethod(valueType);

            mg.loadLocal(var_entity);
            mg.getField(beanType, fieldInfo.getName(), Type.getType(fieldInfo.getField().getType()));
            if (boxMethod != null) {
                mg.invokeStatic(ASMUtil.TYPE_CLASS_UTIL, boxMethod);
            }
            mg.returnValue();
        } else {
            //Exception
            mg.loadThis(); //this.
            mg.loadArg(1); //property
            mg.invokeVirtual(TYPE_ASM_RESOLVER, METHOD_CREATE_UNREAD_EXCEPTION);
            mg.throwException();
        }
    }

    private void attachDefaultConstructorMethod(ClassWriter classWriter, Class beanClass) {
        GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, ASMUtil.METHOD_DEFAULT_CONSTRUCTOR, null, null, classWriter);

        mg.loadThis();
        mg.push(Type.getType(beanClass));
        mg.invokeConstructor(TYPE_ASM_RESOLVER, METHOD_CONSTRUCTOR_ASM_RESOLVER);
        mg.returnValue();
        mg.endMethod();
    }

    public Class generateResolver(Class beanClass) {
        String className = generateClassName(beanClass);

        byte[] code = generateClassBody(className, beanClass);

        return ASMUtil.loadClass(className, code, 0, code.length);
    }
}
