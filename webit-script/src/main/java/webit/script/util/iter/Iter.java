// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.iter;

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
