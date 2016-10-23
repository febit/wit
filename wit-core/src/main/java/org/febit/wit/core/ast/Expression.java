// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

/**
 *
 * @author zqq90
 */
public abstract class Expression extends Statement{

    protected Expression(int line, int column) {
        super(line, column);
    }
}
