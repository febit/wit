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

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings({"unused", "FieldMayBeFinal", "SpellCheckingInspection"})
public class Bar {

    public String f1 = "foo:f1";
    @Setter
    @Getter
    private String f2 = "foo:f2";
    public final String f3 = "foo:f3";
    public int f4 = 4;
    @Setter
    @Getter
    private int f5 = 5;
    public String bG = "foo:bG"; // hashcode 3109
    public String af = "foo:af"; // hashcode 3109
    private String unXable = "Unreadable & Unwriteable";
    public final String unwriteable = "Unwriteable";
    @Setter
    private String unreadable = "Unreadable";

    public boolean isBool() {
        return true;
    }

    public Boolean getBoolean() {
        return false;
    }
}
