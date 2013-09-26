// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.Statment;

/**
 *
 * @author Zqq
 */
public interface BlockStatment extends Statment {

    public void execute(final Context context, final int[] indexs, final Object[] values);

    public boolean hasLoops();
}
