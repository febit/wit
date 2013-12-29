// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.security.impl;

import webit.script.security.NativeSecurityManager;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class NoneNativeSecurityManager implements NativeSecurityManager {

    public boolean access(String path) {
        return true;
    }
}
