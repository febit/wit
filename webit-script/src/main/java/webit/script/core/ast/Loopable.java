// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.List;
import webit.script.core.LoopInfo;

/**
 *
 * @author zqq90
 */
public interface Loopable {

    List<LoopInfo> collectPossibleLoopsInfo();
}
