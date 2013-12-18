// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public interface Statement {

    int getLine();

    int getColumn();

    Object execute(Context context);
}
