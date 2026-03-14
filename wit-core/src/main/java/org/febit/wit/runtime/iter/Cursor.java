package org.febit.wit.runtime.iter;

import java.io.Serializable;

class Cursor implements Serializable {

    private int index = -1;

    public int get() {
        return index;
    }

    public void next() {
        ++index;
    }
}
