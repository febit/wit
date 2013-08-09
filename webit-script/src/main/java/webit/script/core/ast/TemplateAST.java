package webit.script.core.ast;

import java.util.Map;
import webit.script.Context;
import webit.script.core.runtime.LoopCtrl;
import webit.script.core.runtime.VariantStack;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class TemplateAST {

    private final VariantMap varMap;
    private final Statment[] statments;

    public TemplateAST(VariantMap varMap, Statment[] statments) {
        this.varMap = varMap;
        this.statments = statments;
    }

    public Context execute(Context context, Map root) {
        VariantStack vars = context.vars;
        vars.push(varMap);
        vars.setToCurrentContext(root);
        LoopCtrl ctrl = context.loopCtrl;
        int len = statments.length;
        for (int i = 0; i < len && ctrl.goon(); i++) {
            StatmentUtil.execute(statments[i], context);
        }
        if (!ctrl.goon()) {
            throw new ScriptRuntimeException("loop overflow", ctrl.getStatment());
        }
        return context;
    }
}
