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

@SuppressWarnings({"unused", "FieldMayBeFinal"})
class FooParent {

    public static String publicStatic0 = "public + static";
    private static String privateStatic0 = "private + static";
    public final String public0 = "public + final";
    private String private0 = "private";
    protected String protected0 = "protected";
    protected String protected1 = "protected";

    @Setter
    @Getter
    public String publicWithGetterSetter0 = "public + getter/setter";
    @Getter
    private String privateWithGetter0 = "private + getter";
    @Setter
    private String privateWithSetter0 = "private + setter";
    @Setter
    @Getter
    private String privateWithGetterSetter0 = "private + getter/setter";

    public String getMethodField0() {
        return "method field - 0";
    }

    private String getMethodField1() {
        return "method field - 1";
    }

    protected String getMethodField2() {
        return "method field - 2";
    }

    protected String getMethodField3() {
        return "method field - 3";
    }

    public void setMethodField3(Object obj) {
        // do nothing
    }
}
