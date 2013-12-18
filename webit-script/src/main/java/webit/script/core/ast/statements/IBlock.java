// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantIndexer;
import webit.script.core.ast.Statement;

/**
 *
 * @author Zqq
 */
public interface IBlock extends Statement{

    public VariantIndexer getVarMap();

    public Statement[] getStatements();

    public boolean hasLoops();
}
