// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.loop;

import java.util.List;

/**
 *
 * @author Zqq
 */
public interface Loopable {

    List<LoopInfo> collectPossibleLoopsInfo();
}
