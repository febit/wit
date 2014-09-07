// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author zqq
 */
public class Identifer {
    
    public final String name;
    public final int line;
    public final int column;

    public Identifer(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }
}
