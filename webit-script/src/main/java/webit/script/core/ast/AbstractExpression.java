// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author Zqq
 */
public abstract class AbstractExpression extends AbstractStatment implements Expression{

    public AbstractExpression(int line, int column) {
        super(line, column);
    }
}
