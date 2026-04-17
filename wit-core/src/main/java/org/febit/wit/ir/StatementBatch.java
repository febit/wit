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
package org.febit.wit.ir;

import lombok.RequiredArgsConstructor;
import org.febit.wit.ir.flow.Jump;
import org.febit.wit.ir.statement.NoopStatement;
import org.febit.wit.ir.support.Jumps;
import org.febit.wit.ir.support.StatementList;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.runtime.RuntimeContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * A batch of statements, used for internal optimization, not for IR.
 * <p>
 * NOTICE: Except the last one, statements in batch should not have jumps.
 */
@RequiredArgsConstructor(staticName = "of")
public class StatementBatch implements JumpAware {

    private final Statement[] statements;

    public static StatementBatch empty() {
        return new StatementBatch(new Statement[0]);
    }

    public static StatementBatch of(List<Statement> statements) {
        return new StatementBatch(statements.toArray(Statement[]::new));
    }

    /**
     * Batch statements, collect flow control jumps.
     *
     * @return always not empty, if no statement, return a batch with empty statements.
     */
    public static List<StatementBatch> batch(@Nullable List<Statement> list, Consumer<Jump> jumpConsumer) {
        if (list == null || list.isEmpty()) {
            return List.of(StatementBatch.empty());
        }
        var flag = new AtomicBoolean();
        var collecting = (Consumer<Jump>) (jump -> {
            flag.set(true);
            jumpConsumer.accept(jump);
        });

        var batches = new ArrayList<StatementBatch>();
        var current = new ArrayList<Statement>();

        flatAndOptimize(list, stat -> {
            current.add(stat);
            Jumps.collect(collecting, stat);
            if (flag.get()) {
                batches.add(StatementBatch.of(current));
                current.clear();
                flag.set(false);
            }
        });

        if (!current.isEmpty()) {
            batches.add(StatementBatch.of(current));
        }
        if (batches.isEmpty()) {
            return List.of(StatementBatch.empty());
        }
        return List.copyOf(batches);
    }

    private static void flatAndOptimize(@Nullable List<Statement> statements, Consumer<Statement> collector) {
        if (statements == null || statements.isEmpty()) {
            return;
        }
        for (var stat : statements) {
            if (stat instanceof StatementList list) {
                flatAndOptimize(list.statements(), collector);
                continue;
            }
            stat = StatementUtils.optimize(stat);
            if (stat instanceof NoopStatement) {
                continue;
            }
            collector.accept(stat);
        }
    }

    public void execute(RuntimeContext context) {
        for (var stat : statements) {
            stat.execute(context);
        }
    }

    public List<Statement> asList() {
        return List.of(this.statements);
    }

    public boolean isEmpty() {
        return this.statements.length == 0;
    }

    @Override
    public void collectJumps(Consumer<Jump> collector) {
        Jumps.collect(collector, statements);
    }
}
