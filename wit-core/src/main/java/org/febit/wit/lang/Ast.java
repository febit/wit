package org.febit.wit.lang;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.febit.wit.lang.ast.expr.NewArrayExpr;
import org.febit.wit.lang.ast.expr.NewListExpr;
import org.febit.wit.lang.ast.expr.SupplierValue;
import org.febit.wit.lang.ast.expr.TemplateStringValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.febit.wit.lang.AstUtils.toExpressionArray;

@UtilityClass
public class Ast {

    public static DirectValue directValue(Position pos, @Nullable Object value) {
        return new DirectValue(value, pos);
    }

    public static SupplierValue emptyArray(Position pos) {
        return new SupplierValue(() -> new Object[0], pos);
    }

    public static SupplierValue emptyMap(Position pos) {
        return new SupplierValue(HashMap::new, pos);
    }

    public static SupplierValue emptyList(Position pos) {
        return new SupplierValue(ArrayList::new, pos);
    }

    @Builder(
            builderMethodName = "newArrayBuilder",
            builderClassName = "NewArrayBuilder"
    )
    public static NewArrayExpr newArray(
            Position pos,
            @Singular List<Expression> exprs
    ) {
        return new NewArrayExpr(toExpressionArray(exprs), pos);
    }

    @Builder(
            builderMethodName = "newListBuilder",
            builderClassName = "NewListBuilder"
    )
    public static NewListExpr newList(
            Position pos,
            @Singular List<Expression> exprs
    ) {
        return new NewListExpr(toExpressionArray(exprs), pos);
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

    public static class NewArrayBuilder {
    }

    public static class NewListBuilder {
    }

    public static class TemplateStringBuilder {
    }

}
