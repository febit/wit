// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.support.jodd3;

import jodd.madvoc.meta.RenderWith;

/**
 * @author zqq90
 */
@RenderWith(WitResult.class)
public class WitData {

    public final String path;
    public final String contentType;

    public WitData(String path, String contentType) {
        this.path = path;
        this.contentType = contentType;
    }
}
