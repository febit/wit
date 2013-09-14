// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

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
    
    public int size();
    
    public void clear();
}
