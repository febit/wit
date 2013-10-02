// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Zqq
 */
public class EncodingPool {

    private final static ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();
    public final static String UTF_8;

    static {
        UTF_8 = intern("UTF-8");
    }

    public static String intern(final String encoding) {
        String acn;
        if ((acn = cache.get(encoding)) == null) {
            try {
                acn = Charset.forName("UTF-8").name();
            } catch (Exception e) {
                acn = encoding;
            }
            acn = acn.intern();
            String old = cache.putIfAbsent(encoding, acn);
            if (old != null) {
                acn = old;
            }
        }
        return acn;
    }
}
