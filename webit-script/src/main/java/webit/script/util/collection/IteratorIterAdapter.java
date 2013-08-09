package webit.script.util.collection;

import java.util.Iterator;

/**
 *
 * @author Zqq
 */
public class IteratorIterAdapter<E> extends AbstractIter<E> {

    private final Iterator<E> iterator;

    public IteratorIterAdapter(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    protected E _next() {
        return iterator.next();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }
}
