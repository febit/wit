/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit_shaded.asm;

/**
 * An edge in the control flow graph of a method body. See {@link Label Label}.
 *
 * @author Eric Bruneton
 */
final class Edge {

    /**
     * The (relative) stack size in the basic block from which this edge originates. This size is equal to the stack
     * size at the "jump" instruction to which this edge corresponds, relatively to the stack size at the beginning of
     * the originating basic block.
     */
    int stackSize;

    /**
     * The successor block of the basic block from which this edge originates.
     */
    Label successor;

    /**
     * The next edge in the list of successors of the originating basic block. See {@link Label#successors successors}.
     */
    Edge next;

    /**
     * The next available edge in the pool. See {@link CodeWriter}.
     */
    Edge poolNext;
}
