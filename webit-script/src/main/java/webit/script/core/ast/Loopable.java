// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import webit.script.core.LoopInfo;
import java.util.List;

/**
 *
 * @author Zqq
 */
public interface Loopable {

    List<LoopInfo> collectPossibleLoopsInfo();
}
