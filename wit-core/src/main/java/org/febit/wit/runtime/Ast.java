package org.febit.wit.runtime;

import lombok.Builder;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.expr.NewArrayExpr;
import org.febit.wit.runtime.ast.expr.SuppliedValue;
import org.febit.wit.runtime.ast.expr.TemplateStringValue;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static org.febit.wit.runtime.AstUtils.toExpressionArray;

@UtilityClass
public class Ast {

    public static DirectValue directValue(Position pos, @Nullable Object value) {
        return new DirectValue(value, pos);
    }

    public static SuppliedValue emptyArray(Position pos) {
        return new SuppliedValue(() -> new Object[0], pos);
    }

    public static NewArrayExpr newArray(
            Position pos,
            @Singular List<Expression> exprs
    ) {
        return new NewArrayExpr(toExpressionArray(exprs), pos);
    }

    @Builder(
            builderMethodName = "templateStringBuilder",
            builderClassName = "TemplateStringBuilder"
    )
    public static TemplateStringValue templateString(
            Position pos,
            @Singular List<Expression> exprs
    ) {
        return new TemplateStringValue(toExpressionArray(exprs), pos);
    }

}
