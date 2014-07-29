// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author zqq
 */
public class Identifer {
    
    protected final String name;
    protected final int line;
    protected final int column;

    public Identifer(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
