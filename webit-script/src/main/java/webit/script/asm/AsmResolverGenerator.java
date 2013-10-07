// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import webit.script.asm3.ClassWriter;
import webit.script.asm3.Label;
import webit.script.asm3.Opcodes;
import webit.script.asm3.Type;
import webit.script.asm3.commons.GeneratorAdapter;
import webit.script.asm3.commons.Method;
import webit.script.asm3.commons.TableSwitchGenerator;
import webit.script.util.ClassUtil;
import webit.script.util.bean.FieldInfo;
import webit.script.util.bean.FieldInfoResolver;
import webit.script.util.collection.IntArrayList;

/**
 *
 * @author Zqq
 */
public class AsmResolverGenerator {

    private static final String RESOLVERS_CLASS_NAME_PRE = "webit.script.asm.AsmResolver_";
    private static final String[] ASM_RESOLVER = new String[]{"webit/script/asm/AsmResolver"};
    private static final int MIN_SIZE_TO_SWITH = 4;

    private static String generateClassName(Class beanClass) {
        return RESOLVERS_CLASS_NAME_PRE + beanClass.getSimpleName() + '_' + ASMUtil.getSn();
    }

    public static Class generateResolver(Class beanClass) throws Exception {

        if (ClassUtil.isPublic(beanClass)) {
            String className = generateClassName(beanClass);
            byte[] code = generateClassBody(className, beanClass);

            return ASMUtil.loadClass(className, code);
        } else {
            throw new Exception("Class [" + beanClass.getName() + "] is not a public class");
        }
    }

    private static byte[] generateClassBody(String className, Class beanClass) {
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, ASMUtil.toAsmClassName(className), null, ASMUtil.ASM_CLASS_OBJECT, ASM_RESOLVER);

        //Default Constructor
        ASMUtil.attachDefaultConstructorMethod(classWriter);

        FieldsDescription fieldsDescription = resolverFieldsDescriptor(beanClass);
        attach_get_Method(classWriter, beanClass, fieldsDescription);
        attach_set_Method(classWriter, beanClass, fieldsDescription);

        attach_getMatchClass_Method(classWriter, beanClass);
        attach_getMatchMode_Method(classWriter);

        //End Class Writer
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private static void attach_get_Method(final ClassWriter classWriter, final Class beanClass, final FieldsDescription fieldsDescription) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_ASM_RESOLVER_GET, null, null, classWriter);
        final Type beanType = Type.getType(beanClass);

        final int fieldInfosLength = fieldsDescription.size;
        if (fieldInfosLength == 0) {
            //Do nothing
        } else if (fieldInfosLength < MIN_SIZE_TO_SWITH) {
            attachGetFieldsCode(mg, fieldsDescription.all, beanType);
        } else {
            final Label end_switch = mg.newLabel();
            final Map<Integer, FieldInfo[]> groupByHashcode = fieldsDescription.groupByHashcode;
            mg.loadArg(1); //property
            mg.invokeVirtual(ASMUtil.TYPE_OBJECT, ASMUtil.METHOD_HASH_CODE);
            //
            mg.tableSwitch(fieldsDescription.hashcodes, new TableSwitchGenerator() {
                public void generateCase(int hash, Label end) {
                    attachGetFieldsCode(mg, groupByHashcode.get(hash), beanType, end_switch);
                }

                public void generateDefault() {
                }
            });
            mg.mark(end_switch);
        }
        //
        //Exception
        attachThrowNoSuchPropertyException(mg, beanClass);
        mg.endMethod();
    }

    private static void attach_set_Method(final ClassWriter classWriter, final Class beanClass, final FieldsDescription fieldsDescription) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_ASM_RESOLVER_SET, null, null, classWriter);
        final Type beanType = Type.getType(beanClass);

        final int fieldInfosLength = fieldsDescription.size;
        if (fieldInfosLength == 0) {
            //Do nothing
        } else if (fieldInfosLength < MIN_SIZE_TO_SWITH) {
            attachSetFieldsCode(mg, fieldsDescription.all, beanType);
        } else {
            final Label end_switch = mg.newLabel();
            final Map<Integer, FieldInfo[]> groupByHashcode = fieldsDescription.groupByHashcode;
            mg.loadArg(1); //property
            mg.invokeVirtual(ASMUtil.TYPE_OBJECT, ASMUtil.METHOD_HASH_CODE);
            //
            mg.tableSwitch(fieldsDescription.hashcodes, new TableSwitchGenerator() {
                public void generateCase(int hash, Label end) {
                    attachSetFieldsCode(mg, groupByHashcode.get(hash), beanType, end_switch);
                }

                public void generateDefault() {
                }
            });

            mg.mark(end_switch);
        }
        //
        //Exception
        attachThrowNoSuchPropertyException(mg, beanClass);
        mg.endMethod();
    }

    private static void attachSetFieldsCode(final GeneratorAdapter mg, FieldInfo[] fieldInfos, Type beanType) {
        Label l_end = mg.newLabel();
        attachSetFieldsCode(mg, fieldInfos, beanType, l_end);
        mg.mark(l_end);
    }

    private static void attachSetFieldsCode(final GeneratorAdapter mg, FieldInfo[] fieldInfos, Type beanType, Label l_failedMatch) {
        int fieldInfosLength = fieldInfos.length;
        Label[] gotoTable = new Label[fieldInfosLength];
        //if ==
        for (int i = 0; i < fieldInfosLength; i++) {
            gotoTable[i] = mg.newLabel();
            attachIfFieldSameGoto(mg, fieldInfos[i], gotoTable[i] = mg.newLabel());
        }
        //if equals
        for (int i = 0; i < fieldInfosLength; i++) {
            attachIfFieldEqualsGoTo(mg, fieldInfos[i], gotoTable[i]);
        }
        //failed, to end
        mg.goTo(l_failedMatch);
        //actions
        for (int i = 0; i < fieldInfosLength; i++) {
            mg.mark(gotoTable[i]);
            attachSetFieldCode(mg, fieldInfos[i], beanType);
        }
    }

    private static void attachSetFieldCode(final GeneratorAdapter mg, FieldInfo fieldInfo, Type beanType) {
        java.lang.reflect.Method setter = fieldInfo.getSetterMethod();
        if (setter != null) {
            //return book.setName((String)name);

            Class fieldClass = setter.getParameterTypes()[0];

            Method setterMethod = Method.getMethod(setter);
            mg.loadArg(0);
            mg.checkCast(beanType);
            mg.loadArg(2);
            mg.checkCast(ASMUtil.getBoxedType(fieldClass));
            ASMUtil.attachUnBoxCodeIfNeed(mg, fieldClass);
            mg.invokeVirtual(beanType, setterMethod);
            attachReturnTrue(mg);
        } else if (fieldInfo.getField() != null && fieldInfo.isIsFinal() == false) {
            //return book.name = (String) name;
            Class fieldClass = fieldInfo.getField().getType();
            Type fieldType = Type.getType(fieldClass);
            Type boxedFieldType = fieldClass.isPrimitive()
                    ? ASMUtil.getBoxedType(fieldClass)
                    : fieldType;

            mg.loadArg(0);
            mg.checkCast(beanType);
            mg.loadArg(2);
            mg.checkCast(boxedFieldType);
            ASMUtil.attachUnBoxCodeIfNeed(mg, fieldClass);
            mg.putField(beanType, fieldInfo.name, fieldType);
            attachReturnTrue(mg);
        } else {
            //UnwriteableException
            attachThrowUnwriteableException(mg, fieldInfo);
        }
    }

    private static void attachGetFieldsCode(final GeneratorAdapter mg, FieldInfo[] fieldInfos, Type beanType) {
        Label l_end = mg.newLabel();
        attachGetFieldsCode(mg, fieldInfos, beanType, l_end);
        mg.mark(l_end);
    }

    private static void attachGetFieldsCode(final GeneratorAdapter mg, FieldInfo[] fieldInfos, Type beanType, Label l_failedMatch) {
        int fieldInfosLength = fieldInfos.length;
        Label[] gotoTable = new Label[fieldInfosLength];
        //if ==
        for (int i = 0; i < fieldInfosLength; i++) {
            gotoTable[i] = mg.newLabel();
            attachIfFieldSameGoto(mg, fieldInfos[i], gotoTable[i] = mg.newLabel());
        }
        //if equals
        for (int i = 0; i < fieldInfosLength; i++) {
            attachIfFieldEqualsGoTo(mg, fieldInfos[i], gotoTable[i]);
        }
        //failed, to end
        mg.goTo(l_failedMatch);
        //actions
        for (int i = 0; i < fieldInfosLength; i++) {
            mg.mark(gotoTable[i]);
            attachGetFieldCode(mg, fieldInfos[i], beanType);
        }
    }

    private static void attachGetFieldCode(final GeneratorAdapter mg, final FieldInfo fieldInfo, final Type beanType) {
        java.lang.reflect.Method getter = fieldInfo.getGetterMethod();
        if (getter != null) {
            //return book.getName();
            Method getterMethod = Method.getMethod(getter);
            mg.loadArg(0);
            mg.checkCast(beanType);
            mg.invokeVirtual(beanType, getterMethod);
            ASMUtil.attachBoxCodeIfNeed(mg, getter.getReturnType());
            mg.returnValue();
        } else if (fieldInfo.getField() != null) {
            //return book.name;
            mg.loadArg(0);
            mg.checkCast(beanType);
            mg.getField(beanType, fieldInfo.name, Type.getType(fieldInfo.getField().getType()));
            ASMUtil.attachBoxCodeIfNeed(mg, fieldInfo.getField().getType());
            mg.returnValue();
        } else {
            //Unreadable Exception
            attachThrowUnreadableException(mg, fieldInfo);
        }
    }

    private static void attach_getMatchClass_Method(ClassWriter classWriter, Class beanClass) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_ASM_RESOLVER_getMatchClass, null, null, classWriter);
        mg.push(Type.getType(beanClass));
        mg.returnValue();
        mg.endMethod();
    }

    private static void attach_getMatchMode_Method(ClassWriter classWriter) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_ASM_RESOLVER_getMatchMode, null, null, classWriter);
        mg.getStatic(ASMUtil.TYPE_MATCH_MODE, "EQUALS", ASMUtil.TYPE_MATCH_MODE);
        mg.returnValue();
        mg.endMethod();
    }

    private static void attachReturnTrue(final GeneratorAdapter mg) {
        mg.push(true);
        mg.returnValue();
    }

//    private void attachIfFieldNotSameGoto(final GeneratorAdapter mg, FieldInfo fieldInfo, Label l_goto) {
//        mg.push(fieldInfo.name);
//        mg.loadArg(1); //property
//        mg.ifCmp(ASMUtil.TYPE_STRING, GeneratorAdapter.NE, l_goto); // if name != property goto
//    }
    private static void attachIfFieldSameGoto(final GeneratorAdapter mg, FieldInfo fieldInfo, Label l_goto) {
        mg.push(fieldInfo.name);
        mg.loadArg(1); //property
        mg.ifCmp(ASMUtil.TYPE_STRING, GeneratorAdapter.EQ, l_goto); // if name == property goto
    }

    private static void attachIfFieldEqualsGoTo(final GeneratorAdapter mg, FieldInfo fieldInfo, Label l_goto) {
        mg.push(fieldInfo.name);
        mg.loadArg(1); //property
        mg.invokeVirtual(ASMUtil.TYPE_STRING, ASMUtil.METHOD_EQUALS);
        mg.ifZCmp(GeneratorAdapter.NE, l_goto); // if true goto
    }

    private static void attachThrowUnreadableException(final GeneratorAdapter mg, final FieldInfo fieldInfo) {
        ASMUtil.attachThrowScriptRuntimeException(mg, "Unreadable property " + fieldInfo.parent.getName() + "#" + fieldInfo.name);
    }

    private static void attachThrowUnwriteableException(final GeneratorAdapter mg, final FieldInfo fieldInfo) {
        ASMUtil.attachThrowScriptRuntimeException(mg, "Unwriteable property " + fieldInfo.parent.getName() + "#" + fieldInfo.name);
    }

    private static void attachThrowNoSuchPropertyException(final GeneratorAdapter mg, final Class beanClass) {
        mg.newInstance(ASMUtil.TYPE_SCRIPT_RUNTIME_EXCEPTION);
        mg.dup();
        mg.push("Invalid property " + beanClass.getName() + "#");
        mg.loadArg(1);
        mg.invokeVirtual(ASMUtil.TYPE_OBJECT, ASMUtil.METHOD_TO_STRING);
        mg.invokeVirtual(ASMUtil.TYPE_STRING, ASMUtil.METHOD_CONCAT);
        mg.invokeConstructor(ASMUtil.TYPE_SCRIPT_RUNTIME_EXCEPTION, ASMUtil.METHOD_CONSTRUCTOR_SCRIPT_RUNTIME_EXCEPTION);
        mg.throwException();
    }

    private static FieldsDescription resolverFieldsDescriptor(Class beanClass) {

        final FieldInfo[] all = FieldInfoResolver.resolver(beanClass);
        Arrays.sort(all);
        final int size = all.length;

        if (size > 0) {
            final Map<Integer, FieldInfo[]> groupByHashcode = new HashMap<Integer, FieldInfo[]>();

            final List<FieldInfo> cacheList = new ArrayList<FieldInfo>(size);
            final IntArrayList hashList = new IntArrayList(size);

            int hash;
            FieldInfo fieldInfo;

            cacheList.add(fieldInfo = all[0]);
            hashList.add(hash = fieldInfo.hashCode);

            int i = 1;
            while (i < size) {
                fieldInfo = all[i++];
                if (hash == fieldInfo.hashCode) {
                    cacheList.add(fieldInfo);
                } else {
                    groupByHashcode.put(hash, cacheList.toArray(new FieldInfo[cacheList.size()]));
                    cacheList.clear();

                    cacheList.add(fieldInfo);
                    hashList.add(hash = fieldInfo.hashCode);
                }
            }
            groupByHashcode.put(hash, cacheList.toArray(new FieldInfo[cacheList.size()]));

            return new FieldsDescription(all, size, hashList.toArray(), groupByHashcode);
        } else {
            return new FieldsDescription(all, size, null, null);
        }
    }

    private final static class FieldsDescription {

        final FieldInfo[] all;
        final int size;
        final int[] hashcodes;
        final Map<Integer, FieldInfo[]> groupByHashcode;

        public FieldsDescription(FieldInfo[] all, int size, int[] hashcodes, Map<Integer, FieldInfo[]> groupByHashcode) {
            this.all = all;
            this.size = size;
            this.hashcodes = hashcodes;
            this.groupByHashcode = groupByHashcode;
        }
    }
}
