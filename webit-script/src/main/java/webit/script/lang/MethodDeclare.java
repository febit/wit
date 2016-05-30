// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang;

import webit.script.Context;

/**
 *
 * @author zqq90
 */
public interface MethodDeclare {

    Object invoke(Context context, Object[] args);
}
