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
package org.febit.wit_shaded.asm;

/**
 * A constant pool item. Constant pool items can be created with the 'newXXX'
 * methods in the {@link ClassWriter} class.
 *
 * @author Eric Bruneton
 */
final class Item {

    /**
     * Index of this item in the constant pool.
     */
    short index;

    /**
     * Type of this constant pool item. A single class is used to represent all
     * constant pool item types, in order to minimize the bytecode size of this
     * package. The value of this field is one of the constants defined in the
     * {@link ClassWriter ClassWriter} class.
     */
    int type;

    /**
     * Value of this item.
     */
    Number number;

    int argSize;
    /**
     * First part of the value of this item, for items that do not hold a
     * primitive value.
     */
    String strVal1;

    /**
     * Second part of the value of this item, for items that do not hold a
     * primitive value.
     */
    String strVal2;

    /**
     * Third part of the value of this item, for items that do not hold a
     * primitive value.
     */
    String strVal3;

    /**
     * The hash code value of this constant pool item.
     */
    int hashCode;

    /**
     * Constructs an uninitialized {@link Item Item} object.
     */
    Item() {
    }

    /**
     * Constructs a copy of the given item.
     *
     * @param index index of the item to be constructed.
     * @param i the item that must be copied into the item to be constructed.
     */
    Item(final short index, final Item i) {
        this.index = index;
        type = i.type;
        number = i.number;
        strVal1 = i.strVal1;
        strVal2 = i.strVal2;
        strVal3 = i.strVal3;
        hashCode = i.hashCode;
    }

    /**
     * Sets this item to a {@link ClassWriter#LONG LONG} item.
     *
     * @param longVal the value of this item.
     */
    Item set(final int type, final Number number) {
        this.type = type;
        this.number = number;
        this.hashCode = 0x7FFFFFFF & (type + number.intValue());
        return this;
    }

    /**
     * Sets this item to an item that do not hold a primitive value.
     *
     * @param type the type of this item.
     * @param strVal1 first part of the value of this item.
     * @param strVal2 second part of the value of this item.
     * @param strVal3 third part of the value of this item.
     */
    Item set(final int type,
            final String strVal1,
            final String strVal2,
            final String strVal3) {
        this.type = type;
        this.strVal1 = strVal1;
        this.strVal2 = strVal2;
        this.strVal3 = strVal3;
        switch (type) {
            case ClassWriter.UTF8:
            case ClassWriter.STR:
            case ClassWriter.CLASS:
                hashCode = 0x7FFFFFFF & (type + strVal1.hashCode());
                break;
            case ClassWriter.NAME_TYPE:
                hashCode = 0x7FFFFFFF & (type + strVal1.hashCode() * strVal2.hashCode());
                break;
            //case ClassWriter.FIELD:
            //case ClassWriter.METH:
            //case ClassWriter.IMETH:
            default:
                hashCode = 0x7FFFFFFF & (type
                        + strVal1.hashCode() * strVal2.hashCode() * strVal3.hashCode());
        }
        return this;
    }

    /**
     * Indicates if the given item is equal to this one.
     *
     * @param i the item to be compared to this one.
     * @return <tt>true</tt> if the given item if equal to this one,
     * <tt>false</tt> otherwise.
     */
    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != Item.class) {
            return false;
        }
        final Item other = (Item) obj;
        if (other.type != type
                || other.hashCode != this.hashCode) {
            return false;
        }
        switch (type) {
            case ClassWriter.INT:
            case ClassWriter.LONG:
            case ClassWriter.FLOAT:
            case ClassWriter.DOUBLE:
                return other.number.equals(this.number);
            case ClassWriter.UTF8:
            case ClassWriter.STR:
            case ClassWriter.CLASS:
                return other.strVal1.equals(strVal1);
            case ClassWriter.NAME_TYPE:
                return other.strVal1.equals(strVal1)
                        && other.strVal2.equals(strVal2);
            //case ClassWriter.FIELD:
            //case ClassWriter.METH:
            //case ClassWriter.IMETH:
            default:
                return other.strVal1.equals(strVal1)
                        && other.strVal2.equals(strVal2)
                        && other.strVal3.equals(strVal3);
        }
    }
}
