// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import java.io.IOException;

/**
 *
 * @author zqq90
 */
public class ResourceNotFoundException extends IOException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
