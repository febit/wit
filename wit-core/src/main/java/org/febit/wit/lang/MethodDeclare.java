// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import org.febit.wit.InternalContext;

/**
 *
 * @author zqq90
 */
@FunctionalInterface
public interface MethodDeclare {

    Object invoke(InternalContext context, Object[] args);
}
