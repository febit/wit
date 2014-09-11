// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.List;
import webit.script.core.LoopInfo;

/**
 *
 * @author Zqq
 */
public interface Loopable {

    List<LoopInfo> collectPossibleLoopsInfo();
}
