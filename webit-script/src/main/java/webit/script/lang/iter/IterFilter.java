// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import java.util.NoSuchElementException;
import webit.script.lang.Iter;

/**
 *
 * @author zqq
 */
public abstract class IterFilter implements Iter {

    protected final Iter iter;
    protected boolean gotNext;
    protected Object nextItem;
    protected int _index;

    protected IterFilter(Iter iter) {
        this.iter = iter;
        this._index = -1;
    }

    protected abstract boolean valid(Object item);

    public final Object next() {
        ++ this._index;
        if (hasNext()) {
            this.gotNext = false;
            return this.nextItem;
        }
        throw new NoSuchElementException("no more next");
    }

    public final boolean hasNext() {
        if (this.gotNext) {
            return true;
        }
        Iter it;
        while ((it = this.iter).hasNext()) {
            Object item;
            if (valid(item = it.next())) {
                this.gotNext = true;
                this.nextItem = item;
                return true;
            }
        }
        return false;
    }

    public final boolean isFirst() {
        return this._index == 0;
    }

    public final int index() {
        return this._index;
    }
}
