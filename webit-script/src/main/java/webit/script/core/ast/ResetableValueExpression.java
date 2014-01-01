// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public interface ResetableValueExpression extends Expression {

    Object setValue(Context context, Object value);
}
