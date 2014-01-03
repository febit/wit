// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author zqq90
 */
public class ClassEntry {

    private final String profile;
    private final Class value;

    public ClassEntry(Class value, String profile) {
        this.profile = profile;
        this.value = value;
    }

    public String getProfile() {
        return profile;
    }

    public Class getValue() {
        return value;
    }

    public static ClassEntry wrap(final Class cls) {
        return new ClassEntry(cls, cls.getName());
    }

    public static ClassEntry wrap(final String className) throws ClassNotFoundException {
        final int index;
        return new ClassEntry(
                ClassUtil.getClass((index = className.indexOf('-')) >= 0
                        ? className.substring(0, index)
                        : className),
                className);
    }
}
