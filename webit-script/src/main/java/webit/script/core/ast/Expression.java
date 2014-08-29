// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author Zqq
 */
public abstract class Expression extends Statement{

    protected Expression(int line, int column) {
        super(line, column);
    }
}
