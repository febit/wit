// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core;

public class Symbol {

    public final int id;
    public final int line;
    public final int column;
    public final Object value;
    /**
     * The parse state to be recorded on the parse stack with this symbol. This
     * field is for the convenience of the parser and shouldn't be modified
     * except by the parser.
     */
    int state;

    public Symbol(int id, Object value) {
        this(id, -1, -1, value);
    }

    public Symbol(int id, Object value, Symbol sym) {
        this(id, sym.line, sym.column, value);
    }

    public Symbol(int id, int line, int column, Object value) {
        this.id = id;
        this.line = line;
        this.column = column;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Symbol{" + "id=" + id + ", line=" + line + ", column=" + column + '}';
    }
}
