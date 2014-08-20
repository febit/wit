// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public interface MethodDeclare{

    Object invoke(Context context, Object[] args);
}
