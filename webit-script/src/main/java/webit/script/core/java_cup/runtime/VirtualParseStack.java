// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.java_cup.runtime;

/**
 * This class implements a temporary or "virtual" parse stack that replaces the
 * top portion of the actual parse stack (the part that has been changed by some
 * set of operations) while maintaining its original contents. This data
 * structure is used when the parse needs to "parse ahead" to determine if a
 * given error recovery attempt will allow the parse to continue far enough to
 * consider it successful. Once success or failure of parse ahead is determined
 * the system then reverts to the original parse stack (which has not actually
 * been modified). Since parse ahead does not execute actions, only parse state
 * is maintained on the virtual stack, not full Symbol objects.
 *
 * @version last updated: 7/3/96
 * @author Frank Flannery
 */
final class VirtualParseStack {

    VirtualParseStack(Stack<Symbol> shadowing_stack) {

        real_stack = shadowing_stack;
        vstack = new Stack<Integer>();
        real_next = 0;

        get_from_real();
    }

    /*-----------------------------------------------------------*/
    /*--- (Access to) Instance Variables ------------------------*/
    /*-----------------------------------------------------------*/
    /**
     * The real stack that we shadow. This is accessed when we move off the
     * bottom of the virtual portion of the stack, but is always line
     * unmodified.
     */
    private Stack<Symbol> real_stack;

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * Top of stack indicator for where we leave off in the real stack. This is
     * measured from top of stack, so 0 would indicate that no elements have
     * been "moved" from the real to virtual stack.
     */
    private int real_next;

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * The virtual top portion of the stack. This stack contains Integer objects
     * with state numbers. This stack shadows the top portion of the real stack
     * within the area that has been modified (via operations on the virtual
     * stack). When this portion of the stack becomes empty we transfer elements
     * from the underlying stack onto this stack.
     */
    private Stack<Integer> vstack;

    /*-----------------------------------------------------------*/
    /*--- General Methods ---------------------------------------*/
    /*-----------------------------------------------------------*/
    /**
     * Transfer an element from the real to the virtual stack. This assumes that
     * the virtual stack is currently empty.
     */
    private void get_from_real() {
        /* don't transfer if the real stack is empty */
        if (real_next >= real_stack.size()) {
            return;
        }

        /* get a copy of the first Symbol we have not transfered */
        Symbol stack_sym = real_stack.peek(real_next);

        /* record the transfer */
        real_next++;

        /* put the state number from the Symbol onto the virtual stack */
        vstack.push(stack_sym.parse_state);
    }

    boolean empty() {
        return vstack.empty();
    }

    int top() {
        return vstack.peek();
    }

    void pop() {
        /* pop it */
        vstack.pop();

        /* if we are now empty transfer an element (if there is one) */
        if (vstack.empty()) {
            get_from_real();
        }
    }

    void push(int state_num) {
        vstack.push(state_num);
    }
}
