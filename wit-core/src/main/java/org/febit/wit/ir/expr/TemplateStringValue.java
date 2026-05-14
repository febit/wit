/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.ir.expr;

import lombok.Builder;
import lombok.Singular;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.ExpressionArray;
import org.febit.wit.ir.Located;
import org.febit.wit.ir.Position;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.Undefined;

import java.util.List;

public record TemplateStringValue(
        List<Expression> segments,
        Position position
) implements Expression {

    public TemplateStringValue {
        segments = List.copyOf(segments);
    }

    @Builder(
            builderClassName = "TemplateStringBuilder"
    )
    private static TemplateStringValue builder0(
            @lombok.NonNull Located located,
            @Singular
            List<Expression> segments
    ) {
        return new TemplateStringValue(ExpressionArray.of(segments).asList(), located.position());
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public Object execute(RuntimeContext context) {
        var buf = new StringBuilder();
        var exprs = this.segments;
        for (int i = 0, size = exprs.size(); i < size; i++) {
            var segment = exprs.get(i);
            var s = segment.execute(context);
            if (s != null && s != Undefined.UNDEFINED) {
                buf.append(s);
            }
        }
        return buf.toString();
    }
}
