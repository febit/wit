// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
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
import webit.script.util.StringUtil;
import webit.script.util.bean.FieldInfo;
import webit.script.util.bean.FieldInfoResolver;

/**
 *
 * @author Zqq
 */
public class AsmResolverFactory {

    private static final String RESOLVERS_CLASS_NAME_PRE = "webit.script.asm.AsmResolver_";
    private static final String[] ASM_RESOLVER = new String[]{"webit/script/asm/AsmResolver"};

    private static String resolveClassName(Class beanClass) {
        return StringUtil.concat(RESOLVERS_CLASS_NAME_PRE, beanClass.getSimpleName(), "_", Integer.toString(ASMUtil.getSn()));
    }

    public static Class createResolverClass(Class beanClass) throws Exception {

        if (ClassUtil.isPublic(beanClass)) {
            final String className;
            return ASMUtil.loadClass(
                    className = resolveClassName(beanClass),
                    generateClassBody(className, beanClass));
        } else {
            throw new Exception(StringUtil.format("Class [{}] is not public", beanClass));
        }
    }

    private static byte[] generateClassBody(String className, Class beanClass) {
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL, ASMUtil.toAsmClassName(className), null, ASMUtil.ASM_CLASS_OBJECT, ASM_RESOLVER);

        //Default Constructor
        ASMUtil.appendDefaultConstructor(classWriter);

        FieldsDescription fieldsDescription = resolveFieldsDescription(beanClass);
        appendGetMethod(classWriter, beanClass, fieldsDescription);
        appendSetMethod(classWriter, beanClass, fieldsDescription);

        appendGetMatchClassMethod(classWriter, beanClass);

        //End Class Writer
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private static void appendGetMethod(final ClassWriter classWriter, final Class beanClass, final FieldsDescription fieldsDescription) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_ASM_RESOLVER_GET, null, null, classWriter);
        final Type beanType = Type.getType(beanClass);

        final int fieldInfosLength = fieldsDescription.size;
        if (fieldInfosLength == 0) {
            //Do nothing
        } else if (fieldInfosLength < 4) {
            appendGetFieldsCode(mg, fieldsDescription.all, beanType);
        } else {
            final Label end_switch = mg.newLabel();
            final Map<Integer, FieldInfo[]> groupByHashcode = fieldsDescription.groupByHashcode;
            mg.loadArg(1);
            mg.invokeVirtual(ASMUtil.TYPE_OBJECT, ASMUtil.METHOD_HASH_CODE);

            mg.tableSwitch(fieldsDescription.hashcodes, new TableSwitchGenerator() {
                public void generateCase(int hash, Label end) {
                    appendGetFieldsCode(mg, groupByHashcode.get(hash), beanType, end_switch);
                }

                public void generateDefault() {
                }
            }, false);
            mg.mark(end_switch);
        }
        //Exception
        appendThrowNoSuchPropertyException(mg, beanClass);
        mg.endMethod();
    }

    private static void appendSetMethod(final ClassWriter classWriter, final Class beanClass, final FieldsDescription fieldsDescription) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_ASM_RESOLVER_SET, null, null, classWriter);
        final Type beanType = Type.getType(beanClass);

        final int fieldInfosLength = fieldsDescription.size;
        if (fieldInfosLength == 0) {
            //Do nothing
        } else if (fieldInfosLength < 4) {
            appendSetFieldsCode(mg, fieldsDescription.all, beanType);
        } else {
            final Label end_switch = mg.newLabel();
            final Map<Integer, FieldInfo[]> groupByHashcode = fieldsDescription.groupByHashcode;
            mg.loadArg(1);
            mg.invokeVirtual(ASMUtil.TYPE_OBJECT, ASMUtil.METHOD_HASH_CODE);
            //
            mg.tableSwitch(fieldsDescription.hashcodes, new TableSwitchGenerator() {
                public void generateCase(int hash, Label end) {
                    appendSetFieldsCode(mg, groupByHashcode.get(hash), beanType, end_switch);
                }

                public void generateDefault() {
                }
            }, false);

            mg.mark(end_switch);
        }
        //Exception
        appendThrowNoSuchPropertyException(mg, beanClass);
        mg.endMethod();
    }

    private static void appendSetFieldsCode(final GeneratorAdapter mg, FieldInfo[] fieldInfos, Type beanType) {
        Label l_end = mg.newLabel();
        appendSetFieldsCode(mg, fieldInfos, beanType, l_end);
        mg.mark(l_end);
    }

    private static void appendSetFieldsCode(final GeneratorAdapter mg, FieldInfo[] fieldInfos, Type beanType, Label l_failedMatch) {
        int fieldInfosLength = fieldInfos.length;
        Label[] gotoTable = new Label[fieldInfosLength];
        //if ==
        for (int i = 0; i < fieldInfosLength; i++) {
            gotoTable[i] = mg.newLabel();
            appendIfFieldSameGoto(mg, fieldInfos[i], gotoTable[i] = mg.newLabel());
        }
        //if equals
        for (int i = 0; i < fieldInfosLength; i++) {
            appendIfFieldEqualsGoTo(mg, fieldInfos[i], gotoTable[i]);
        }
        //failed, to end
        mg.goTo(l_failedMatch);
        //actions
        for (int i = 0; i < fieldInfosLength; i++) {
            mg.mark(gotoTable[i]);
            appendSetFieldCode(mg, fieldInfos[i], beanType);
        }
    }

    private static void appendSetFieldCode(final GeneratorAdapter mg, FieldInfo fieldInfo, Type beanType) {
        java.lang.reflect.Method setter = fieldInfo.getSetterMethod();
        if (setter != null) {
            //return book.setName((String)name);

            Class fieldClass = setter.getParameterTypes()[0];

            Method setterMethod = Method.getMethod(setter);
            mg.loadArg(0);
            mg.checkCast(beanType);
            mg.loadArg(2);
            mg.checkCast(ASMUtil.getBoxedType(fieldClass));
            ASMUtil.appendUnBoxCodeIfNeed(mg, fieldClass);
            mg.invokeVirtual(beanType, setterMethod);
            appendReturnTrue(mg);
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
            ASMUtil.appendUnBoxCodeIfNeed(mg, fieldClass);
            mg.putField(beanType, fieldInfo.name, fieldType);
            appendReturnTrue(mg);
        } else {
            //UnwriteableException
            appendThrowUnwriteableException(mg, fieldInfo);
        }
    }

    private static void appendGetFieldsCode(final GeneratorAdapter mg, FieldInfo[] fieldInfos, Type beanType) {
        Label l_end = mg.newLabel();
        appendGetFieldsCode(mg, fieldInfos, beanType, l_end);
        mg.mark(l_end);
    }

    private static void appendGetFieldsCode(final GeneratorAdapter mg, FieldInfo[] fieldInfos, Type beanType, Label l_failedMatch) {
        int fieldInfosLength = fieldInfos.length;
        Label[] gotoTable = new Label[fieldInfosLength];
        //if ==
        for (int i = 0; i < fieldInfosLength; i++) {
            gotoTable[i] = mg.newLabel();
            appendIfFieldSameGoto(mg, fieldInfos[i], gotoTable[i] = mg.newLabel());
        }
        //if equals
        for (int i = 0; i < fieldInfosLength; i++) {
            appendIfFieldEqualsGoTo(mg, fieldInfos[i], gotoTable[i]);
        }
        //failed, to end
        mg.goTo(l_failedMatch);
        //actions
        for (int i = 0; i < fieldInfosLength; i++) {
            mg.mark(gotoTable[i]);
            appendGetFieldCode(mg, fieldInfos[i], beanType);
        }
    }

    private static void appendGetFieldCode(final GeneratorAdapter mg, final FieldInfo fieldInfo, final Type beanType) {
        java.lang.reflect.Method getter = fieldInfo.getGetterMethod();
        if (getter != null) {
            //return book.getName();
            Method getterMethod = Method.getMethod(getter);
            mg.loadArg(0);
            mg.checkCast(beanType);
            mg.invokeVirtual(beanType, getterMethod);
            ASMUtil.appendBoxCodeIfNeed(mg, getter.getReturnType());
            mg.returnValue();
        } else if (fieldInfo.getField() != null) {
            //return book.name;
            mg.loadArg(0);
            mg.checkCast(beanType);
            mg.getField(beanType, fieldInfo.name, Type.getType(fieldInfo.getField().getType()));
            ASMUtil.appendBoxCodeIfNeed(mg, fieldInfo.getField().getType());
            mg.returnValue();
        } else {
            //Unreadable Exception
            appendThrowUnreadableException(mg, fieldInfo);
        }
    }

    private static void appendGetMatchClassMethod(ClassWriter classWriter, Class beanClass) {
        final GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, ASMUtil.METHOD_ASM_RESOLVER_getMatchClass, null, null, classWriter);
        mg.push(Type.getType(beanClass));
        mg.returnValue();
        mg.endMethod();
    }

    private static void appendReturnTrue(final GeneratorAdapter mg) {
        mg.push(true);
        mg.returnValue();
    }

    private static void appendIfFieldSameGoto(final GeneratorAdapter mg, FieldInfo fieldInfo, Label l_goto) {
        mg.push(fieldInfo.name);
        mg.loadArg(1);
        // if name == property goto
        mg.ifCmp(ASMUtil.TYPE_STRING, GeneratorAdapter.EQ, l_goto);
    }

    private static void appendIfFieldEqualsGoTo(final GeneratorAdapter mg, FieldInfo fieldInfo, Label l_goto) {
        mg.push(fieldInfo.name);
        mg.loadArg(1);
        mg.invokeVirtual(ASMUtil.TYPE_STRING, ASMUtil.METHOD_EQUALS);
        // if true goto
        mg.ifZCmp(GeneratorAdapter.NE, l_goto);
    }

    private static void appendThrowUnreadableException(final GeneratorAdapter mg, final FieldInfo fieldInfo) {
        ASMUtil.appendThrowScriptRuntimeException(mg, StringUtil.concat("Unreadable property ", fieldInfo.parent.getName(), "#", fieldInfo.name));
    }

    private static void appendThrowUnwriteableException(final GeneratorAdapter mg, final FieldInfo fieldInfo) {
        ASMUtil.appendThrowScriptRuntimeException(mg, StringUtil.concat("Unwriteable property ", fieldInfo.parent.getName(), "#", fieldInfo.name));
    }

    private static void appendThrowNoSuchPropertyException(final GeneratorAdapter mg, final Class beanClass) {
        mg.newInstance(ASMUtil.TYPE_SCRIPT_RUNTIME_EXCEPTION);
        mg.dup();
        mg.push(StringUtil.concat("Invalid property ", beanClass.getName(), "#"));
        mg.loadArg(1);
        mg.invokeVirtual(ASMUtil.TYPE_OBJECT, ASMUtil.METHOD_TO_STRING);
        mg.invokeVirtual(ASMUtil.TYPE_STRING, ASMUtil.METHOD_CONCAT);
        mg.invokeConstructor(ASMUtil.TYPE_SCRIPT_RUNTIME_EXCEPTION, ASMUtil.METHOD_CONSTRUCTOR_SCRIPT_RUNTIME_EXCEPTION);
        mg.throwException();
    }

    private static FieldsDescription resolveFieldsDescription(Class beanClass) {

        final FieldInfo[] all = FieldInfoResolver.resolve(beanClass);
        Arrays.sort(all);
        final int size = all.length;

        if (size > 0) {
            final Map<Integer, FieldInfo[]> groupByHashcode = new HashMap<Integer, FieldInfo[]>();

            final List<FieldInfo> cacheList = new ArrayList<FieldInfo>(size);
            final int[] hashs = new int[size];

            int hashsCount = 0;

            int hash;
            FieldInfo fieldInfo;

            cacheList.add(fieldInfo = all[0]);
            hashs[hashsCount++] = hash = fieldInfo.hashCode;

            int i = 1;
            while (i < size) {
                fieldInfo = all[i++];
                if (hash == fieldInfo.hashCode) {
                    cacheList.add(fieldInfo);
                } else {
                    groupByHashcode.put(hash, cacheList.toArray(new FieldInfo[cacheList.size()]));
                    cacheList.clear();
                    cacheList.add(fieldInfo);
                    hashs[hashsCount++] = hash = fieldInfo.hashCode;
                }
            }
            groupByHashcode.put(hash, cacheList.toArray(new FieldInfo[cacheList.size()]));

            return new FieldsDescription(all, size, Arrays.copyOf(hashs, hashsCount), groupByHashcode);
        } else {
            return new FieldsDescription(all, size, null, null);
        }
    }

    private static class FieldsDescription {

        final FieldInfo[] all;
        final int size;
        final int[] hashcodes;
        final Map<Integer, FieldInfo[]> groupByHashcode;

        FieldsDescription(FieldInfo[] all, int size, int[] hashcodes, Map<Integer, FieldInfo[]> groupByHashcode) {
            this.all = all;
            this.size = size;
            this.hashcodes = hashcodes;
            this.groupByHashcode = groupByHashcode;
        }
    }
}
