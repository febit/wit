// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.loop;

/**
 *
 * @author Zqq
 */
public enum LoopType {

    /**
     * no loop, default loop type
     */
    NULL("null"),
    /**
     * break
     */
    BREAK("break"),
    /**
     * continue
     */
    CONTINUE("continue"),
    /**
     * return
     */
    RETURN("return");
    public final String value;

    LoopType(String value) {
        this.value = value;
    }
}