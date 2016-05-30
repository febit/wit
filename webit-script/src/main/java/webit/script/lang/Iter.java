// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang;

/**
 *
 * @author zqq90
 */
public interface Iter {

    boolean hasNext();

    Object next();

    boolean isFirst();

    int index();
}
