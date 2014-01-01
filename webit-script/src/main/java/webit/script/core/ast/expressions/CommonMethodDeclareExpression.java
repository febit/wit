// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.method.MethodDeclare;

/**
 *
 * @author Zqq
 */
public class CommonMethodDeclareExpression extends AbstractExpression {

    private final MethodDeclare declare;

    public CommonMethodDeclareExpression(MethodDeclare declare, int line, int column) {
        super(line, column);
        this.declare = declare;
    }

    public Object execute(Context context) {
        return this.declare;
    }
}
