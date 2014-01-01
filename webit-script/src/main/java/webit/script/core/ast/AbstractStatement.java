// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;


/**
 *
 * @author Zqq
 */
public abstract class AbstractStatement implements Statement{

    protected final int line;
    protected final int column;

    public AbstractStatement(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public final int getLine() {
        return line;
    }

    public final int getColumn() {
        return column;
    }
}
