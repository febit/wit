// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForMapStatment extends AbstractStatment implements Loopable {

    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;
    private final Expression mapExpr;
    private final VariantMap varMap;
    private final Statment[] statments;
    public final LoopInfo[] possibleLoopsInfo;
    private final Statment elseStatment;
    private final String label;

    public ForMapStatment(int iterIndex, int keyIndex, int valueIndex, Expression mapExpr, VariantMap varMap, Statment[] statments, LoopInfo[] possibleLoopsInfo, Statment elseStatment, String label, int line, int column) {
        super(line, column);
        this.iterIndex = iterIndex;
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
        this.mapExpr = mapExpr;
        this.varMap = varMap;
        this.statments = statments;
        this.possibleLoopsInfo = possibleLoopsInfo;
        this.elseStatment = elseStatment;
        this.label = label;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final Object object = StatmentUtil.execute(mapExpr, context);
        final Iter<Map.Entry> iter;
        if (object != null) {
            if (object instanceof Map) {
                iter = CollectionUtil.toIter(((Map) object).entrySet());
            } else {
                throw new ScriptRuntimeException("Not a instance of java.util.Map");
            }
        } else {
            iter = null;
        }
        if (iter != null && iter.hasNext()) {
            final LoopCtrl ctrl = context.loopCtrl;
            final Statment[] statments = this.statments;
            Map.Entry entry;
            final VariantStack vars;
            (vars = context.vars).push(varMap);
            label:
            do {
                entry = iter.next();
                vars.resetCurrentWith(iterIndex, iter, keyIndex, entry.getKey(), valueIndex, entry.getValue());
                StatmentUtil.executeAndCheckLoops(statments, context);
                if (!ctrl.goon()) {
                    if (ctrl.matchLabel(label)) {
                        switch (ctrl.getLoopType()) {
                            case BREAK:
                                ctrl.reset();
                                break label; // while
                            case RETURN:
                                //can't deal
                                break label; //while
                            case CONTINUE:
                                ctrl.reset();
                                break; //switch
                            default:
                                break label; //while
                            }
                    } else {
                        break; //while
                    }
                }
            } while (iter.hasNext());
            vars.pop();

        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return possibleLoopsInfo != null ? new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo)) : null;
    }
}
