/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit_shaded.asm;

/**
 * A constant pool item. Constant pool items can be created with the 'newXXX' methods in the {@link ClassWriter} class.
 *
 * @author Eric Bruneton
 */
final class Item {

    /**
     * Index of this item in the constant pool.
     */
    short index;

    /**
     * Type of this constant pool item. A single class is used to represent all constant pool item types, in order to
     * minimize the bytecode size of this package. The value of this field is one of the constants defined in the
     * {@link ClassWriter ClassWriter} class.
     */
    int type;

    /**
     * Value of this item.
     */
    Number number;

    int argSize;
    /**
     * First part of the value of this item, for items that do not hold a primitive value.
     */
    String strVal1;

    /**
     * Second part of the value of this item, for items that do not hold a primitive value.
     */
    String strVal2;

    /**
     * Third part of the value of this item, for items that do not hold a primitive value.
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
