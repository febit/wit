// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang;

import webit.script.InternalContext;

/**
 *
 * @author zqq90
 */
public interface MethodDeclare {

    Object invoke(InternalContext context, Object[] args);
}
