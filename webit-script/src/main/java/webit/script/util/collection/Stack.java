// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public interface Stack<E> {

    public void push(E item);

    public E peek();

    public E peek(int offset);

    public boolean empty();

    public E pop();

    public void pops(int len);

    public int size();

    public void clear();
}
