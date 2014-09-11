/**
 * *
 * ASM: a very small and fast Java bytecode manipulation framework Copyright (c)
 * 2000,2002,2003 INRIA, France Telecom All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package webit.script.asm.lib;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A {@link ClassVisitor ClassVisitor} that generates Java class files. More
 * precisely this visitor generates a byte array conforming to the Java class
 * file format. It can be used alone, to generate a Java class "from scratch",
 * or with one or more {@link ClassReader ClassReader} and adapter class visitor
 * to generate a modified class from one or more existing Java classes.
 *
 * @author Eric Bruneton
 */
public final class ClassWriter {

    static final int[] EMPATY_INTS = new int[0];
    /**
     * The type of CONSTANT_Class constant pool items.
     */
    static final int CLASS = 7;

    /**
     * The type of CONSTANT_Fieldref constant pool items.
     */
    static final int FIELD = 9;

    /**
     * The type of CONSTANT_Methodref constant pool items.
     */
    static final int METH = 10;

    /**
     * The type of CONSTANT_InterfaceMethodref constant pool items.
     */
    static final int IMETH = 11;

    /**
     * The type of CONSTANT_String constant pool items.
     */
    static final int STR = 8;

    /**
     * The type of CONSTANT_Integer constant pool items.
     */
    static final int INT = 3;

    /**
     * The type of CONSTANT_Float constant pool items.
     */
    static final int FLOAT = 4;

    /**
     * The type of CONSTANT_Long constant pool items.
     */
    static final int LONG = 5;

    /**
     * The type of CONSTANT_Double constant pool items.
     */
    static final int DOUBLE = 6;

    /**
     * The type of CONSTANT_NameAndType constant pool items.
     */
    static final int NAME_TYPE = 12;

    /**
     * The type of CONSTANT_Utf8 constant pool items.
     */
    static final int UTF8 = 1;

    /**
     * Minor and major version numbers of the class to be generated.
     */
    private final int version;

    /**
     * The access flags of this class.
     */
    private final int access;

    /**
     * The constant pool item that contains the internal name of this class.
     */
    private final int name;

    /**
     * The constant pool item that contains the internal name of the super class
     * of this class.
     */
    private final int superName;

    /**
     * The interfaces implemented or extended by this class or interface. More
     * precisely, this array contains the indexes of the constant pool items
     * that contain the internal names of these interfaces.
     */
    private final int[] interfaces;

//    /**
//     * Number of fields of this class.
//     */
//    private int fieldCount;
//
//    /**
//     * The fields of this class.
//     */
//    private ByteBuffer fields;
    /**
     * The methods of this class. These methods are stored in a linked list of
     * {@link MethodWriter MethodWriter} objects, linked to each other by their {@link
     * CodeWriter#next} field. This field stores the first element of this list.
     */
    private final ArrayList<MethodWriter> methods;

    /**
     * The constant pool of this class.
     */
    private final ByteBuffer pool;

    /**
     * The constant pool's hash table data.
     */
    private final HashMap<Item, Item> items;

    /**
     * Index of the next item to be added in the constant pool.
     */
    private short poolIndex;

    /**
     * A reusable key used to look for items in the hash {@link #items items}.
     */
    private final Item key;
    private final Item key2;
    private final Item key3;

//    /**
//     * The type of instructions without any label.
//     */
//    static final int NOARG_INSN = 0;
//
//    /**
//     * The type of instructions with an signed byte label.
//     */
//    static final int SBYTE_INSN = 1;
//
//    /**
//     * The type of instructions with an signed short label.
//     */
//    static final int SHORT_INSN = 2;
//
//    /**
//     * The type of instructions with a local variable index label.
//     */
//    static final int VAR_INSN = 3;
//
//    /**
//     * The type of instructions with an implicit local variable index label.
//     */
//    static final int IMPLVAR_INSN = 4;
//
//    /**
//     * The type of instructions with a type descriptor argument.
//     */
//    static final int TYPE_INSN = 5;
//
//    /**
//     * The type of field and method invocations instructions.
//     */
//    static final int FIELDORMETH_INSN = 6;
//
//    /**
//     * The type of the INVOKEINTERFACE instruction.
//     */
//    static final int ITFMETH_INSN = 7;
//
//    /**
//     * The type of instructions with a 2 bytes bytecode offset label.
//     */
//    static final int LABEL_INSN = 8;
//
//    /**
//     * The type of instructions with a 4 bytes bytecode offset label.
//     */
//    static final int LABELW_INSN = 9;
//
//    /**
//     * The type of the LDC instruction.
//     */
//    static final int LDC_INSN = 10;
//
//    /**
//     * The type of the LDC_W and LDC2_W instructions.
//     */
//    static final int LDCW_INSN = 11;
//
//    /**
//     * The type of the IINC instruction.
//     */
//    static final int IINC_INSN = 12;
//
//    /**
//     * The type of the TABLESWITCH instruction.
//     */
//    static final int TABL_INSN = 13;
//
//    /**
//     * The type of the LOOKUPSWITCH instruction.
//     */
//    static final int LOOK_INSN = 14;
//
////    /**
////     * The type of the MULTIANEWARRAY instruction.
////     */
////    static final int MANA_INSN = 15;
//
//    /**
//     * The type of the WIDE instruction.
//     */
//    static final int WIDE_INSN = 16;
//
//    /**
//     * The instruction types of all JVM opcodes.
//     */
//    static final byte[] TYPE;
//
//    // --------------------------------------------------------------------------
//    // Static initializer
//    // --------------------------------------------------------------------------
//    /**
//     * Computes the instruction types of JVM opcodes.
//     */
//    static {
//        int i, len;
//        byte[] b = new byte[220];
//        String s
//                = "AAAAAAAAAAAAAAAABCKLLDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEE"
//                + "EEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAA"
//                + "AAAAAAAAAAAAAAAAAIIIIIIIIIIIIIIIIDNOAAAAAAGGGGGGGHAFBFAAFFAAQPIIJJII"
//                + "IIIIIIIIIIIIIIII";
//        for (i = 0, len = b.length; i < len; ++i) {
//            b[i] = (byte) (s.charAt(i) - 'A');
//        }
//        TYPE = b;
//
//        /* code to generate the above string
//
//         // SBYTE_INSN instructions
//         b[Constants.NEWARRAY] = SBYTE_INSN;
//         b[Constants.BIPUSH] = SBYTE_INSN;
//
//         // SHORT_INSN instructions
//         b[Constants.SIPUSH] = SHORT_INSN;
//
//         // (IMPL)VAR_INSN instructions
//         b[Constants.RET] = VAR_INSN;
//         for (i = Constants.ILOAD; i <= Constants.ALOAD; ++i) {
//         b[i] = VAR_INSN;
//         }
//         for (i = Constants.ISTORE; i <= Constants.ASTORE; ++i) {
//         b[i] = VAR_INSN;
//         }
//         for (i = 26; i <= 45; ++i) { // ILOAD_0 to ALOAD_3
//         b[i] = IMPLVAR_INSN;
//         }
//         for (i = 59; i <= 78; ++i) { // ISTORE_0 to ASTORE_3
//         b[i] = IMPLVAR_INSN;
//         }
//
//         // TYPE_INSN instructions
//         b[Constants.NEW] = TYPE_INSN;
//         b[Constants.ANEWARRAY] = TYPE_INSN;
//         b[Constants.CHECKCAST] = TYPE_INSN;
//         b[Constants.INSTANCEOF] = TYPE_INSN;
//
//         // (Set)FIELDORMETH_INSN instructions
//         for (i = Constants.GETSTATIC; i <= Constants.INVOKESTATIC; ++i) {
//         b[i] = FIELDORMETH_INSN;
//         }
//         b[Constants.INVOKEINTERFACE] = ITFMETH_INSN;
//
//         // LABEL(W)_INSN instructions
//         for (i = Constants.IFEQ; i <= Constants.JSR; ++i) {
//         b[i] = LABEL_INSN;
//         }
//         b[Constants.IFNULL] = LABEL_INSN;
//         b[Constants.IFNONNULL] = LABEL_INSN;
//         b[200] = LABELW_INSN; // GOTO_W
//         b[201] = LABELW_INSN; // JSR_W
//         // temporary opcodes used internally by ASM - see Label and MethodWriter
//         for (i = 202; i < 220; ++i) {
//         b[i] = LABEL_INSN;
//         }
//
//         // LDC(_W) instructions
//         b[Constants.LDC] = LDC_INSN;
//         b[19] = LDCW_INSN; // LDC_W
//         b[20] = LDCW_INSN; // LDC2_W
//
//         // special instructions
//         b[Constants.IINC] = IINC_INSN;
//         b[Constants.TABLESWITCH] = TABL_INSN;
//         b[Constants.LOOKUPSWITCH] = LOOK_INSN;
//         b[Constants.MULTIANEWARRAY] = MANA_INSN;
//         b[196] = WIDE_INSN; // WIDE
//
//         for (i = 0; i < b.length; ++i) {
//         System.err.print((char)('A' + b[i]));
//         }
//         System.err.println();
//         */
//    }
    public ClassWriter(
            final int version,
            final int access,
            final String name,
            final String superName,
            final String[] interfaces) {
        poolIndex = 1;
        pool = new ByteBuffer();
        items = new HashMap<Item, Item>(64);
        methods = new ArrayList<MethodWriter>();
        key = new Item();
        key2 = new Item();
        key3 = new Item();

        this.version = version;
        this.access = access;
        this.name = newClass(name);
        this.superName = superName == null ? 0 : newClass(superName);
        if (interfaces != null) {
            int interfaceCount = interfaces.length;
            this.interfaces = new int[interfaceCount];
            for (int i = 0; i < interfaceCount; ++i) {
                this.interfaces[i] = newClass(interfaces[i]);
            }
        } else {
            this.interfaces = EMPATY_INTS;
        }
//        if ((access & Constants.ACC_DEPRECATED) != 0) {
//            newUTF8("Deprecated");
//        }
//        if ((access & Constants.ACC_SYNTHETIC) != 0) {
//            newUTF8("Synthetic");
//        }
    }

//    public void visitField(
//            final int access,
//            final String name,
//            final String desc,
//            final Object value) {
//        ++fieldCount;
//        if (fields == null) {
//            fields = new ByteBuffer();
//        }
//        fields.putShort(access).putShort(newUTF8(name)).putShort(newUTF8(desc));
//        int attributeCount = 0;
//        if (value != null) {
//            ++attributeCount;
//        }
//        if ((access & Constants.ACC_SYNTHETIC) != 0) {
//            ++attributeCount;
//        }
//        if ((access & Constants.ACC_DEPRECATED) != 0) {
//            ++attributeCount;
//        }
//        fields.putShort(attributeCount);
//        if (value != null) {
//            fields.putShort(newUTF8("ConstantValue"));
//            fields.putInt(2).putShort(newConstItem(value).index);
//        }
//        if ((access & Constants.ACC_SYNTHETIC) != 0) {
//            fields.putShort(newUTF8("Synthetic")).putInt(0);
//        }
//        if ((access & Constants.ACC_DEPRECATED) != 0) {
//            fields.putShort(newUTF8("Deprecated")).putInt(0);
//        }
//    }
    public MethodWriter visitMethod(
            final int access,
            final String name,
            final String desc,
            final String[] exceptions) {
        MethodWriter cw = new MethodWriter(this, access, name, desc, exceptions);
        this.methods.add(cw);
        return cw;
    }

    /**
     * Returns the bytecode of the class that was build with this class writer.
     *
     * @return the bytecode of the class that was build with this class writer.
     */
    public byte[] toByteArray() {
        // computes the real size of the bytecode of this class
        final int interfaceCount = interfaces.length;
        int size = 24 + 2 * interfaceCount;
//        if (fields != null) {
//            size += fields.length;
//        }
        for (MethodWriter cb : this.methods) {
            size += cb.getSize();
        }
//        int attributeCount = 0;
//        if ((access & Constants.ACC_DEPRECATED) != 0) {
//            ++attributeCount;
//            size += 6;
//        }
//        if ((access & Constants.ACC_SYNTHETIC) != 0) {
//            ++attributeCount;
//            size += 6;
//        }
        size += pool.length;
        // allocates a byte vector of this size, in order to avoid unnecessary
        // arraycopy operations in the ByteBuffer.enlarge() method
        final ByteBuffer out = new ByteBuffer(size);
        out.putInt(0xCAFEBABE).putInt(version);
        out.putShort(poolIndex).put(pool);
        out.putShort(access).putShort(name).putShort(superName);
        out.putShort(interfaceCount);
        for (int i = 0; i < interfaceCount; ++i) {
            out.putShort(interfaces[i]);
        }
        out.putShort(0); //out.putShort(fieldCount);
//        if (fields != null) {
//            out.put(fields);
//        }
        out.putShort(this.methods.size());
        for (MethodWriter cb : this.methods) {
            cb.renderTo(out);
        }
        out.putShort(0 /* attributeCount */);
//        if ((access & Constants.ACC_DEPRECATED) != 0) {
//            out.putShort(newUTF8("Deprecated")).putInt(0);
//        }
//        if ((access & Constants.ACC_SYNTHETIC) != 0) {
//            out.putShort(newUTF8("Synthetic")).putInt(0);
//        }
        return out.data;
    }

    // --------------------------------------------------------------------------
    // Utility methods: constant pool management
    // --------------------------------------------------------------------------
    /**
     * Adds a number or string constant to the constant pool of the class being
     * build. Does nothing if the constant pool already contains a similar item.
     *
     * @param cst the value of the constant to be added to the constant pool.
     * This parameter must be an {@link java.lang.Integer Integer}, a {@link
     *      java.lang.Float Float}, a {@link java.lang.Long Long}, a {@link
     *      java.lang.Double Double}, a {@link String String} or a {@link Type}.
     * @return a new or already existing constant item with the given value.
     */
    Item newConstItem(final Object cst) {
        if (cst instanceof Integer) {
            return newInteger((Integer) cst);
        } else if (cst instanceof Byte) {
            return newInteger((Byte) cst);
        } else if (cst instanceof Character) {
            return newInteger((Character) cst);
        } else if (cst instanceof Short) {
            return newInteger((Short) cst);
        } else if (cst instanceof Boolean) {
            return newInteger(((Boolean) cst) ? 1 : 0);
        } else if (cst instanceof Float) {
            return newFloat((Float) cst);
        } else if (cst instanceof Long) {
            return newLong((Long) cst);
        } else if (cst instanceof Double) {
            return newDouble((Double) cst);
        } else if (cst instanceof String) {
            return newString((String) cst);
        } else if (cst instanceof Item) {
            return (Item) cst;
        } else {
            throw new IllegalArgumentException("value " + cst);
        }
    }

    /**
     * Adds an UTF8 string to the constant pool of the class being build. Does
     * nothing if the constant pool already contains a similar item. <i>This
     * method is intended for {@link Attribute} sub classes, and is normally not
     * needed by class generators or adapters.</i>
     *
     * @param value the String value.
     * @return the index of a new or already existing UTF8 item.
     */
    int newUTF8(final String value) {
        Item result = get(key.set(UTF8, value, null, null));
        if (result == null) {
            pool.putByte(UTF8).putUTF8(value);
            result = new Item(poolIndex++, key);
            put(result);
        }
        return result.index;
    }

    /**
     * Adds a class reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     *
     * @param value the internal name of the class.
     * @return the index of a new or already existing class reference item.
     */
    int newClass(final String value) {
        return newClassItem(value).index;
    }

    /**
     * Adds a class reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     *
     * @param value the internal name of the class.
     * @return a new or already existing class reference item.
     */
    Item newClassItem(final String value) {
        Item result = get(key2.set(CLASS, value, null, null));
        if (result == null) {
            pool.putBS(CLASS, newUTF8(value));
            result = new Item(poolIndex++, key2);
            put(result);
        }
        return result;
    }

    /**
     * Adds a field reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     * <i>This method is intended for {@link Attribute} sub classes, and is
     * normally not needed by class generators or adapters.</i>
     *
     * @param owner the internal name of the field's owner class.
     * @param name the field's name.
     * @param desc the field's descriptor.
     * @return the index of a new or already existing field reference item.
     */
    public int newField(
            final String owner,
            final String name,
            final String desc) {
        Item result = get(key3.set(FIELD, owner, name, desc));
        if (result == null) {
            put122(FIELD, newClass(owner), newNameType(name, desc));
            result = new Item(poolIndex++, key3);
            put(result);
        }
        return result.index;
    }

    /**
     * Adds a method reference to the constant pool of the class being build.
     * Does nothing if the constant pool already contains a similar item.
     *
     * @param owner the internal name of the method's owner class.
     * @param name the method's name.
     * @param desc the method's descriptor.
     * @param itf <tt>true</tt> if <tt>owner</tt> is an interface.
     * @return a new or already existing method reference item.
     */
    Item newMethodItem(
            final String owner,
            final String name,
            final String desc,
            final boolean itf) {
        Item result = get(key3.set(itf ? IMETH : METH, owner, name, desc));
        if (result == null) {
            put122(itf ? IMETH : METH, newClass(owner), newNameType(name, desc));
            result = new Item(poolIndex++, key3);
            put(result);
        }
        return result;
    }

    /**
     * Adds an integer to the constant pool of the class being build. Does
     * nothing if the constant pool already contains a similar item.
     *
     * @param value the int value.
     * @return a new or already existing int item.
     */
    private Item newInteger(final int value) {
        Item result = get(key.set(INT, value));
        if (result == null) {
            pool.putByte(INT).putInt(value);
            result = new Item(poolIndex++, key);
            put(result);
        }
        return result;
    }

    /**
     * Adds a float to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item.
     *
     * @param value the float value.
     * @return a new or already existing float item.
     */
    private Item newFloat(final float value) {
        Item result = get(key.set(FLOAT, value));
        if (result == null) {
            pool.putByte(FLOAT).putInt(Float.floatToIntBits(value));
            result = new Item(poolIndex++, key);
            put(result);
        }
        return result;
    }

    /**
     * Adds a long to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item.
     *
     * @param value the long value.
     * @return a new or already existing long item.
     */
    private Item newLong(final long value) {
        Item result = get(key.set(LONG, value));
        if (result == null) {
            pool.putByte(LONG).putLong(value);
            result = new Item(poolIndex, key);
            put(result);
            poolIndex += 2;
        }
        return result;
    }

    /**
     * Adds a double to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item.
     *
     * @param value the double value.
     * @return a new or already existing double item.
     */
    private Item newDouble(final double value) {
        Item result = get(key.set(DOUBLE, value));
        if (result == null) {
            pool.putByte(DOUBLE).putLong(Double.doubleToLongBits(value));
            result = new Item(poolIndex, key);
            put(result);
            poolIndex += 2;
        }
        return result;
    }

    /**
     * Adds a string to the constant pool of the class being build. Does nothing
     * if the constant pool already contains a similar item.
     *
     * @param value the String value.
     * @return a new or already existing string item.
     */
    private Item newString(final String value) {
        Item result = get(key2.set(STR, value, null, null));
        if (result == null) {
            pool.putBS(STR, newUTF8(value));
            result = new Item(poolIndex++, key2);
            put(result);
        }
        return result;
    }

    /**
     * Adds a name and type to the constant pool of the class being build. Does
     * nothing if the constant pool already contains a similar item. <i>This
     * method is intended for {@link Attribute} sub classes, and is normally not
     * needed by class generators or adapters.</i>
     *
     * @param name a name.
     * @param desc a type descriptor.
     * @return the index of a new or already existing name and type item.
     */
    public int newNameType(final String name, final String desc) {
        Item result = get(key2.set(NAME_TYPE, name, desc, null));
        if (result == null) {
            put122(NAME_TYPE, newUTF8(name), newUTF8(desc));
            result = new Item(poolIndex++, key2);
            put(result);
        }
        return result.index;
    }

    /**
     * Returns the constant pool's hash table item which is equal to the given
     * item.
     *
     * @param key a constant pool item.
     * @return the constant pool's hash table item which is equal to the given
     * item, or <tt>null</tt> if there is no such item.
     */
    private Item get(final Item key) {
        return items.get(key);
    }

    /**
     * Puts the given item in the constant pool's hash table. The hash table
     * <i>must</i> not already contains this item.
     *
     * @param i the item to be added to the constant pool's hash table.
     */
    private void put(final Item i) {
        this.items.put(i, i);
    }

    /**
     * Puts one byte and two shorts into the constant pool.
     *
     * @param b a byte.
     * @param s1 a short.
     * @param s2 another short.
     */
    private void put122(final int b, final int s1, final int s2) {
        pool.putBS(b, s1).putShort(s2);
    }
}
