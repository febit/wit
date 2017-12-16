// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.security.impl;

import org.febit.wit.security.NativeSecurityManager;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class NoneNativeSecurityManager implements NativeSecurityManager {

    @Override
    public boolean access(String path) {
        return true;
    }
}
