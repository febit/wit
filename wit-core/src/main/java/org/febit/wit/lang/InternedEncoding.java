// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zqq
 */
public class InternedEncoding {

    public static final InternedEncoding UTF_8;
    private static final Map<String, InternedEncoding> INTERNED;

    static {
        INTERNED = new HashMap<>();
        UTF_8 = intern("UTF-8");
    }

    public final String value;

    private InternedEncoding(String value) {
        this.value = value;
    }

    public static InternedEncoding intern(String encoding) {
        if (encoding == null) {
            return null;
        }
        encoding = Charset.forName(encoding).name();
        InternedEncoding interned = INTERNED.get(encoding);
        if (interned != null) {
            return interned;
        }
        return doIntern(encoding);
    }

    private static synchronized InternedEncoding doIntern(String encoding) {
        InternedEncoding interned = INTERNED.get(encoding);
        if (interned != null) {
            return interned;
        }
        interned = new InternedEncoding(encoding);
        INTERNED.put(encoding, interned);
        return interned;
    }
}
