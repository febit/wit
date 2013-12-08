// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.core.ast.Statment;
import webit.script.core.VariantIndexer;

/**
 *
 * @author Zqq
 */
public interface IBlockStatment extends Statment{

    public VariantIndexer getVarMap();

    public Statment[] getStatments();

    public boolean hasLoops();
}
