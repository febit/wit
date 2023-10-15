package org.febit.wit.lang;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.expr.ArrayValue;
import org.febit.wit.lang.ast.expr.DirectValue;

import java.util.List;

import static org.febit.wit.lang.AstUtils.emptyExpressions;
import static org.febit.wit.lang.AstUtils.toExpressionArray;

@UtilityClass
public class AstFactory {

    public ArrayValue arrayValue(List<Expression> exprs, Position position) {
        return new ArrayValue(toExpressionArray(exprs), position);
    }

    public ArrayValue emptyArrayValue(Position position) {
        return new ArrayValue(emptyExpressions(), position);
    }

    public DirectValue directValue(@Nullable Object value, Position position) {
        return new DirectValue(value, position);
    }


}
