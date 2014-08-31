// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import java.util.NoSuchElementException;
import webit.script.lang.Iter;

/**
 *
 * @author zqq
 */
public abstract class IterFilter extends AbstractIter {

    protected final Iter iter;

    public IterFilter(Iter Iter) {
        this.iter = Iter;
    }

    protected abstract boolean valid(Object item);

    protected boolean gotNext = false;
    protected Object nextItem;

    protected boolean fetchNext() {
        if (gotNext) {
            return true;
        }
        Iter it = this.iter;
        while (it.hasNext()) {
            Object item = it.next();
            if (valid(item)) {
                gotNext = true;
                nextItem = item;
                return true;
            }
        }
        return false;
    }

    @Override
    protected Object _next() {
        if (fetchNext()) {
            gotNext = false;
            return nextItem;
        }
        throw new NoSuchElementException("no more next");
    }

    public boolean hasNext() {
        return fetchNext();
    }
}
