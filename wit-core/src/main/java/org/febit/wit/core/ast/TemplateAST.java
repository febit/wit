// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.core.VariantIndexer;
import org.febit.wit.io.Out;
import org.febit.wit.lang.KeyValues;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class TemplateAST {

    private final Statement[] statements;
    private final VariantIndexer[] indexers;
    private final int varSize;

    public TemplateAST(VariantIndexer[] indexers, Statement[] statements, int varSize) {
        this.indexers = indexers;
        this.statements = statements;
        this.varSize = varSize;
    }

    public InternalContext execute(Template template, final Out out, KeyValues rootParams) {

        final InternalContext context = new InternalContext(template, out, rootParams, indexers, varSize, null);
        rootParams.exportTo(context);
        StatementUtil.executeInverted(this.statements, context);
        //assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Template template, final InternalContext context, KeyValues rootParams) {

        final InternalContext newContext = context.createPeerContext(template, indexers, varSize);
        rootParams.exportTo(newContext);
        StatementUtil.executeInverted(this.statements, newContext);
        //assert context.indexer = 0
        return newContext;
    }
}
