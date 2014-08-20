// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang;

/**
 *
 * @author Zqq
 */
public interface Iter {

    boolean hasNext();

    Object next();

    boolean isFirst();

    int index();
}
