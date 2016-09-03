// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.util;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zqq
 */
public class InternedEncoding {

    private static final Map<String, InternedEncoding> INTERNED = new HashMap<>();
    public static final InternedEncoding UTF_8 = intern("UTF-8");

    public static InternedEncoding intern(String encoding) {
        if (encoding == null) {
            return null;
        }
        encoding = resolveInternEncodingName(encoding);
        InternedEncoding interned = INTERNED.get(encoding);
        if (interned != null) {
            return interned;
        }
        return _intern(encoding);
    }

    private static String resolveInternEncodingName(final String encoding) {
        try {
            return Charset.forName(encoding).name();
        } catch (Exception e) {
            return encoding.toUpperCase();
        }
    }

    private static synchronized InternedEncoding _intern(String encoding) {
        InternedEncoding interned = INTERNED.get(encoding);
        if (interned != null) {
            return interned;
        }
        interned = new InternedEncoding(encoding);
        INTERNED.put(encoding, interned);
        return interned;
    }

    public final String value;

    private InternedEncoding(String value) {
        this.value = value;
    }
}
