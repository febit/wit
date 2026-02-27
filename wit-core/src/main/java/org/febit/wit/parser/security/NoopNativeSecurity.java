// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser.security;

public class NoopNativeSecurity implements NativeSecurity {

    public static final NoopNativeSecurity INSTANCE = new NoopNativeSecurity();

    @Override
    public boolean allowed(String path) {
        return true;
    }
}
