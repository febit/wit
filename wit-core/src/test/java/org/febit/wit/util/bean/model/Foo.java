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

public class Foo extends FooParent {

    public static String publicStatic;
    private String private2;

    public String getProtected1() {
        return protected1;
    }

    protected String getPrivate2() {
        return private2;
    }

    void setPrivate2(String private2) {
        this.private2 = private2;
    }

    public String getMethodField4() {
        return null;
    }
}
