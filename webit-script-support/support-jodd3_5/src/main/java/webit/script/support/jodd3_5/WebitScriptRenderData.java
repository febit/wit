// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.support.jodd3_5;

import jodd.madvoc.meta.RenderWith;

/**
 *
 * @author zqq90
 */
@RenderWith(WebitScriptRender.class)
public class WebitScriptRenderData {

    public final String path;
    public final String contentType;

    public WebitScriptRenderData(String path, String contentType) {
        this.path = path;
        this.contentType = contentType;
    }
}
