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
package org.febit.wit.util.bean.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
@lombok.Builder(
        builderClassName = "Builder"
)
public record User(
        String name,
        int age,
        AtomicBoolean enabledRef,
        AtomicReference<String> statusRef
) {

    public String code() {
        return name.toUpperCase();
    }

    public boolean isEnabled() {
        return enabledRef.get();
    }

    public void setEnabled(boolean enabled) {
        enabledRef.set(enabled);
    }

    public String status() {
        return statusRef.get();
    }

    public void status(String status) {
        statusRef.set(status);
    }
}
