// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.febit.wit.util.ArrayUtil;

import java.util.function.ObjIntConsumer;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class VariantIndexer {

    public static final VariantIndexer EMPTY = new VariantIndexer(null, ArrayUtil.emptyStrings(), new int[0]);

    @Nullable
    private final VariantIndexer parent;
    private final String[] names;
    private final int[] indexes;

    public int getCurrentIndex(final String name) {
        var myNames = this.names;
        for (int i = 0, len = myNames.length; i < len; i++) {
            if (myNames[i].equals(name)) {
                return indexes[i];
            }
        }
        return -1;
    }

    public void forEach(ObjIntConsumer<String> action) {
        var myNames = this.names;
        var myIndexes = this.indexes;
        for (int i = 0, len = myNames.length; i < len; i++) {
            action.accept(myNames[i], myIndexes[i]);
        }
    }

    public int getIndex(final String name) {
        var index = getCurrentIndex(name);
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
