package org.febit.wit.exceptions;

import java.lang.reflect.Member;

/**
 *
 * @author zqq90
 */
public class AmbiguousMethodException extends ScriptRuntimeException {

    public AmbiguousMethodException(String message) {
        super(message);
    }

    public <T extends Member> AmbiguousMethodException(T[] methods, Class<?>[] argTypes) {
        this(buildMessage(methods, argTypes));
    }

    protected static <T extends Member> String buildMessage(T[] methods, Class<?>[] argTypes) {
        StringBuilder msg = new StringBuilder();
        msg.append("Ambiguous method for [");
        for (int i = 0; i < argTypes.length; i++) {
            if (i != 0) {
                msg.append(',');
            }
            msg.append(argTypes[i].getName());
        }
        msg.append("] with ");
        for (int i = 0; i < methods.length; i++) {
            if (i != 0) {
                msg.append(',');
            }
            msg.append(methods[i].toString());
        }
        return msg.toString();
    }

}
