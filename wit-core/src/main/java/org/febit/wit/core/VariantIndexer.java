// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import java.util.function.ObjIntConsumer;
import org.febit.wit.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public final class VariantIndexer {

    public static final VariantIndexer EMPTY = new VariantIndexer(null, ArrayUtil.emptyStrings(), null);

    private final VariantIndexer parent;
    private final String[] names;
    private final int[] indexes;

    VariantIndexer(VariantIndexer parent, String[] names, int[] indexes) {
        this.parent = parent;
        this.names = names;
        this.indexes = indexes;
    }

    public int getCurrentIndex(final String name) {
        final String[] myNames = this.names;
        for (int i = 0, len = myNames.length; i < len; i++) {
            if (myNames[i].equals(name)) {
                return indexes[i];
            }
        }
        return -1;
    }

    public void forEach(ObjIntConsumer<String> action) {
        final String[] myNames = this.names;
        final int[] myIndexes = this.indexes;
        for (int i = 0, len = myNames.length; i < len; i++) {
            action.accept(myNames[i], myIndexes[i]);
        }
    }

    public int getIndex(final String name) {
        final int index = getCurrentIndex(name);
        if (index != -1) {
            return index;
        }
        if (this.parent != null) {
            return parent.getIndex(name);
        }
        return -1;
    }

    public String getName(int index) {
        return this.names[index];
    }
}
