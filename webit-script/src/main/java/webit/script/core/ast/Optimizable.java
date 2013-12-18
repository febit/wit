// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

/**
 *
 * @author Zqq
 */
public interface Optimizable {

    Statement optimize() throws Throwable;
}