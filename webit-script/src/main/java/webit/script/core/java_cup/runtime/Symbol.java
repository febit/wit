// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.java_cup.runtime;

public class Symbol {

    public final int id;
    public final int line;
    public final int column;
    public final Object value;
    
    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * The parse state to be recorded on the parse stack with this symbol. This
     * field is for the convenience of the parser and shouldn't be modified
     * except by the parser.
     */
    int parse_state;

    public Symbol(int id, int line, int column, Object value) {
        this.id = id;
        this.line = line;
        this.column = column;
        this.value = value;
    }

    public Symbol(int id, int line, int column, Object value, int parse_state) {
        this(id, line, column, value);
        this.parse_state = parse_state;
    }
    
    

    @Override
    public String toString() {
        return "Symbol{" + "id=" + id + ", line=" + line + ", column=" + column + '}';
    }
}
