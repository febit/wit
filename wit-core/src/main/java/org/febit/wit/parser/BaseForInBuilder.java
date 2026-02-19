// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclareExpr;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true, chain = true)
public abstract class BaseForInBuilder {

    protected final VariantManager vars;
    protected final Position position;

    private boolean frameShifted = false;

    protected Statement @Nullable [] body;

    @Getter
    private int frame;

    @Setter
    @Nullable
    protected Statement elseBody;

    @Setter
    @Nullable
    protected Expression collection;

    @Setter
    @Nullable
    protected FunctionDeclareExpr filter;

    protected int iterIndex;

    protected BaseForInBuilder(VariantManager vars, Position position) {
        this.position = position;
        this.vars = vars;
    }

    public abstract Statement build(int label);

    public BaseForInBuilder shiftFrame() {
        if (frameShifted) {
            throw new IllegalStateException("frame already shifted");
        }
        this.frameShifted = true;
        vars.shiftFrame();
        iterIndex = vars.assignVar("for.iter", position);
        return this;
    }

    public BaseForInBuilder body(@Nullable List<Statement> list) {
        if (!frameShifted) {
            throw new IllegalStateException("frame not shifted");
        }
        this.frame = vars.unshiftFrame();
        this.body = Ast.flatStatements(list);
        return this;
    }

}
