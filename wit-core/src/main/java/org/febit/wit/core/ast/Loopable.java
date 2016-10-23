// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast;

import java.util.List;
import org.febit.wit.core.LoopInfo;

/**
 *
 * @author zqq90
 */
public interface Loopable {

    List<LoopInfo> collectPossibleLoopsInfo();
}
