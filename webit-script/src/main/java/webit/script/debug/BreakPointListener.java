// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.debug;

import webit.script.InternalContext;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public interface BreakPointListener {
    
    void onBreak(Object label, InternalContext context, Statement statement, Object result);
}
