// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.debug;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public interface BreakPointListener {

    void onBreak(Object label, InternalContext context, Statement statement, Object result);
}
