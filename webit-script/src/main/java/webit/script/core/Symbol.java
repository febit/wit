// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core;

class Symbol {

    final int id;
    final int line;
    final int column;
    final Object value;
    /**
     * The parse state to be recorded on the parse stack with this symbol. This
     * field is for the convenience of the parser and shouldn't be modified
     * except by the parser.
     */
    int state;

    Symbol(int id, Object value) {
        this(id, -1, -1, value);
    }

    Symbol(int id, Object value, Symbol sym) {
        this(id, sym.line, sym.column, value);
    }

    Symbol(int id, int line, int column, Object value) {
        this.id = id;
        this.line = line;
        this.column = column;
        this.value = value;
    }
}
