// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import webit.script.exceptions.NativeSecurityException;
import webit.script.security.NativeSecurityManager;

/**
 *
 * @author Zqq
 */
public class NativeSecurityManagerUtil {

    public static void checkAccess(NativeSecurityManager securityManager, String path) throws NativeSecurityException {
        if (securityManager.access(path) == false) {
            throw new NativeSecurityException(path);
        }
    }
}
