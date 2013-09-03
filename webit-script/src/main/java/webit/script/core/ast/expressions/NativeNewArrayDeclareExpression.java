// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.method.NativeNewArrayDeclare;

/**
 *
 * @author Zqq
 */
public class NativeNewArrayDeclareExpression extends AbstractExpression {

    private final Class componentType;

    public NativeNewArrayDeclareExpression(Class componentType, int line, int column) {
        super(line, column);
        this.componentType = componentType;
    }

    @Override
    public Object execute(Context context, boolean needReturn) {
        return new NativeNewArrayDeclare(componentType);
    }
}
