// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.security;

public class NoneNativeSecurity implements NativeSecurity {

    @Override
    public boolean allowed(String path) {
        return true;
    }
}
