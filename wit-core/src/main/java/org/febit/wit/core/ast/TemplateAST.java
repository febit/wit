// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.Vars;
import org.febit.wit.core.VariantIndexer;
import org.febit.wit.io.Out;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
public final class TemplateAST {

    private final Statement[] statements;
    private final VariantIndexer[] indexers;
    private final int varSize;
    private final long createdAt;
    private final long resourceVersion;

    public TemplateAST(VariantIndexer[] indexers, Statement[] statements, int varSize, long resourceVersion) {
        this.indexers = indexers;
        this.statements = statements;
        this.varSize = varSize;
        this.createdAt = System.currentTimeMillis();
        this.resourceVersion = resourceVersion;
    }

    public InternalContext execute(Template template, final Out out, Vars rootParams) {
        final InternalContext context = new InternalContext(template, out, rootParams, indexers, varSize, null);
        rootParams.exportTo(context::set);
        StatementUtil.execute(this.statements, context);
        //assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Template template, final InternalContext context, Vars rootParams) {
        final InternalContext newContext = context.createPeerContext(template, indexers, varSize, rootParams);
        rootParams.exportTo(newContext::set);
        StatementUtil.execute(this.statements, newContext);
        //assert context.indexer = 0
        return newContext;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getResourceVersion() {
        return resourceVersion;
    }
}
