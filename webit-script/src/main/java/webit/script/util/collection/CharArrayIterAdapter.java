package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public class CharArrayIterAdapter extends AbstractIter<Character> {

    private final char [] array;
    private final int max;

    public CharArrayIterAdapter(char[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    protected Character _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
