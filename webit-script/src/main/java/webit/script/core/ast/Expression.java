package webit.script.core.ast;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public interface Expression extends Statment {

    Object execute(Context context, boolean needReturn);
}
