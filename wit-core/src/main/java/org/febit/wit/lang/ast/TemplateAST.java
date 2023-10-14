// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast;

import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.Vars;
import org.febit.wit.io.Out;
import org.febit.wit.lang.VariantIndexer;

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
        var context = new InternalContext(template, out, rootParams, indexers, varSize, null);
        rootParams.exportTo(context::set);
        context.execute(this.statements);
        //assert context.indexer = 0
        return context;
    }

    public InternalContext execute(Template template, final InternalContext context, Vars rootParams) {
        var newContext = context.createPeerContext(template, indexers, varSize, rootParams);
        rootParams.exportTo(newContext::set);
        newContext.execute(this.statements);
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
