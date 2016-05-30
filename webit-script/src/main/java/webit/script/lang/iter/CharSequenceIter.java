// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

/**
 *
 * @author zqq90
 */
public final class CharSequenceIter extends AbstractArrayIter {

    private final CharSequence chars;

    public CharSequenceIter(CharSequence chars) {
        super(chars.length() - 1);
        this.chars = chars;
    }

    @Override
    public Object next() {
        return chars.charAt(++_index);
    }
}
