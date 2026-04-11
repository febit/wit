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
package org.febit.wit.parser.support;

import org.febit.wit.runtime.heap.Heap;
import org.jspecify.annotations.Nullable;

public record VarAddress(
        Kind kind,
        int frameOffset,
        int slot,
        @Nullable Heap heap,
        @Nullable String key,
        @Nullable Object value
) {

    public enum Kind {
        VAR,
        FRAME_VAR,
        DIRECT,
        HEAP,
        ;
    }

    static VarAddress ofVariable(int slot) {
        return new VarAddress(Kind.VAR, 0, slot, null, null, null);
    }

    static VarAddress ofUpper(int frameOffset, int slot) {
        return new VarAddress(Kind.FRAME_VAR, frameOffset, slot, null, null, null);
    }

    static VarAddress ofHeap(Heap heap, String name) {
        return new VarAddress(Kind.HEAP, -1, -1, heap, name, null);
    }

    static VarAddress ofDirect(@Nullable Object value) {
        return new VarAddress(Kind.DIRECT, -1, -1, null, null, value);
    }

}
