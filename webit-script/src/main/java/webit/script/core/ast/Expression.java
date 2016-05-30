// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author zqq90
 */
public abstract class Expression extends Statement{

    protected Expression(int line, int column) {
        super(line, column);
    }
}
