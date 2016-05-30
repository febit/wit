// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.debug;

import webit.script.Context;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public interface BreakPointListener {
    
    void onBreak(Object label, Context context, Statement statement, Object result);
}
