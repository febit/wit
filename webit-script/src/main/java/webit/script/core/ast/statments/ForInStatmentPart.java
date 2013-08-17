// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentPart;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ForInStatmentPart extends StatmentPart {

    protected int itemIndex;
    protected int iterIndex;
    protected Expression collectionExpr;
    private BlockStatment bodyStatment;
    private Statment elseStatment;
    private String label;

    public ForInStatmentPart(int itemIndex, int iterIndex, Expression collectionExpr, int line, int column) {
        super(line, column);
        this.itemIndex = itemIndex;
        this.iterIndex = iterIndex;
        this.collectionExpr = collectionExpr;
    }
    

    public ForInStatmentPart setLabel(String label) {
        this.label = label;
        return this;
    }

    public ForInStatmentPart setBodyStatment(BlockStatment bodyStatment) {
        this.bodyStatment = bodyStatment.optimize();
        return this;
    }

    public ForInStatmentPart setElseStatment(Statment elseStatment) {
        
        elseStatment = StatmentUtil.optimize(elseStatment);
        this.elseStatment = elseStatment;
        return this;
    }
    
    public Statment pop(){
        return StatmentUtil.optimize(new ForInStatment(itemIndex, iterIndex, collectionExpr, bodyStatment, elseStatment, label, line, column));
    }
}
