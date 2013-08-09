package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public class IntArrayIterAdapter extends AbstractIter<Integer> {

    private final int [] array;
    private final int max;

    public IntArrayIterAdapter(int[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    protected Integer _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
