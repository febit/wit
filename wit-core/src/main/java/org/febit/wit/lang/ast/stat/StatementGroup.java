// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class StatementGroup implements Statement {

    @Getter
    private final Statement[] list;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        context.execute(this.list);
        return null;
    }

    @Override
    public Statement optimize() {
        if (this.list.length == 0) {
            return NoopStatement.INSTANCE;
        }
        return this;
    }
}
