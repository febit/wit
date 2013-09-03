// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.java_cup.runtime;

/**
 * Defines the Symbol class, which is used to represent all terminals and
 * nonterminals while parsing. The lexer should pass CUP Symbols and CUP returns
 * a Symbol.
 *
 * @version last updated: 7/3/96
 * @author Frank Flannery
 */

/* ****************************************************************
 Class Symbol
 what the parser expects to receive from the lexer. 
 the token is identified as follows:
 sym:    the symbol type
 parse_state: the parse state.
 value:  is the lexical value of type Object
 line :  is the line position in the original input file
 column:  is the column position in the original input file
 xleft:  is the line position Object in the original input file
 xright:  is the line position Object in the original input file
 ******************************************************************/
public class Symbol {

//  TUM 20060327: Added new Constructor to provide more flexible way
//   for location handling
    /**
     * *****************************
     ******************************
     */
    public Symbol(int id, Symbol left, Symbol right, Object o) {
        this(id, left.line, right.column, o);
    }

    public Symbol(int id, Symbol left, Symbol right) {
        this(id, left.line, right.column);
    }


    public Symbol(int id, int l, int r, Object o) {
        this(id);
        line = l;
        column = r;
        value = o;
    }


    public Symbol(int id, Object o) {
        this(id, -1, -1, o);
    }


    public Symbol(int id, int l, int r) {
        this(id, l, r, null);
    }

    /**
     * *********************************
     * Constructor for no value or l,r 
     * ********************************
     */
    public Symbol(int sym_num) {
        this(sym_num, -1);
        line = -1;
        column = -1;
    }

    /**
     * *********************************
     * Constructor to give a start state 
     * ********************************
     */
    Symbol(int sym_num, int state) {
        sym = sym_num;
        parse_state = state;
    }

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * The symbol number of the terminal or non terminal being represented
     */
    public int sym;

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * The parse state to be recorded on the parse stack with this symbol. This
     * field is for the convenience of the parser and shouldn't be modified
     * except by the parser.
     */
    public int parse_state;
    /**
     * This allows us to catch some errors caused by scanners recycling symbols.
     * For the use of the parser only. [CSA, 23-Jul-1999]
     */
    boolean used_by_parser = false;
    public int line, column;
    public Object value;

    @Override
    public String toString() {
        return "#" + sym;
    }
}
