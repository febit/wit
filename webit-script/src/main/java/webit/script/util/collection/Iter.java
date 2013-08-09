package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public interface Iter<E> {

    boolean hasNext();

    E next();

    boolean isFirst();

    int index();
}
