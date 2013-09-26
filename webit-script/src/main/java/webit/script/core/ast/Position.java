// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author Zqq
 */
public abstract class Position {
    public int line;
    public int column;
    
    public Position() {
    }
    
    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public Position setPosition(int line,int column) {
        this.line = line;
        this.column = column;
        return this;
    }
}
