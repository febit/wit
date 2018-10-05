// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.security.impl;

import org.febit.wit.security.NativeSecurityManager;

/**
 * @author zqq90
 * @since 1.4.0
 */
public class NoneNativeSecurityManager implements NativeSecurityManager {

    @Override
    public boolean access(String path) {
        return true;
    }
}
