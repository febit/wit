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
package org.febit.wit.extern.lib.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Wit;
import org.febit.wit.WitModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class GlobalContextRegister implements WitModule {

    public static final String DEFAULT_NAME = "$GLOBAL";

    @Getter
    private final Map<Object, Object> table = new ConcurrentHashMap<>();

    @Getter
    private final String name;

    public static GlobalContextRegister create() {
        return create(DEFAULT_NAME);
    }

    @Override
    public void apply(Wit wit) {
        var heap = wit.globals().constants();
        heap.set(this.name, this.table);
    }
}
