// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import java.util.Enumeration;

/**
 *
 * @author Zqq
 */
public class EnumerationIterAdapter extends AbstractIter {

    private final Enumeration enumeration;

    public EnumerationIterAdapter(Enumeration enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    protected Object _next() {
        return enumeration.nextElement();
    }

    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }
}
