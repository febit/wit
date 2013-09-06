// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import java.io.IOException;

/**
 *
 * @author Zqq
 */
public class ResourceNotFoundException extends IOException{
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

//    public ResourceNotFoundException(String message, Throwable cause) {
//        super(message, cause);
//    }
//
//    public ResourceNotFoundException(Throwable cause) {
//        super(cause);
//    }
}
