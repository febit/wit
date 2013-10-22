// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public interface MethodDeclare{

    Object invoke(Context context, Object[] args);
}
