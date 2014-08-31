// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.security.impl;

import webit.script.security.NativeSecurityManager;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public final class NoneNativeSecurityManager implements NativeSecurityManager {

    public boolean access(String path) {
        return true;
    }
}
