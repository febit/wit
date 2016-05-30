// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.support.jodd3;

import jodd.madvoc.meta.RenderWith;

/**
 *
 * @author zqq90
 */
@RenderWith(WebitResult.class)
public class WebitData {

    public final String path;
    public final String contentType;

    public WebitData(String path, String contentType) {
        this.path = path;
        this.contentType = contentType;
    }
}
