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
package org.febit.wit.runtime.evaluation;

import org.junit.jupiter.api.Test;

import static org.febit.wit.runtime.evaluation.EvalSupport.error;
import static org.febit.wit.runtime.evaluation.EvalSupport.ok;

@SuppressWarnings({
        "java:S2699", // Tests should include assertions
})
class BeanAccessorEvalTest {

    @Test
    void emptyBeanPropertyAccess() {
        error("""
                var newObject = native new java.lang.Object();
                var bean = newObject();
                var value = bean[null];
                """, "property should not be null for bean access.");

        error("""
                var newObject = native new java.lang.Object();
                var bean = newObject();
                var value = bean.name;
                """, "no such property: java.lang.Object#name");

        error("""
                var newObject = native new java.lang.Object();
                var bean = newObject();
                bean[null] = "wit";
                """, "property should not be null for bean access.");

        error("""
                var newObject = native new java.lang.Object();
                var bean = newObject();
                bean.name = "wit";
                """, "no such property: java.lang.Object#name");
    }

    @Test
    void beanPropertyRead() {
        ok("""
                @import org.febit.wit.script.bean.User;
                var newUser = native new User();
                var user = newUser();
                var value = user.name;
                """);

        error("""
                @import org.febit.wit.script.bean.User;
                var newUser = native new User();
                var user = newUser();
                var value = user[null];
                """, "property should not be null for bean access.");

        error("""
                @import org.febit.wit.script.bean.User;
                var newUser = native new User();
                var user = newUser();
                var value = user["missing"];
                """, "no such property: org.febit.wit.script.bean.User#missing");
    }

    @Test
    void beanPropertyWrite() {
        ok("""
                @import org.febit.wit.script.bean.User;
                var newUser = native new User();
                var user = newUser();
                user.name = "wit";
                """);

        error("""
                @import org.febit.wit.script.bean.User;
                var newUser = native new User();
                var user = newUser();
                user[null] = "wit";
                """, "property should not be null for bean access.");

        error("""
                @import org.febit.wit.script.bean.User;
                var newUser = native new User();
                var user = newUser();
                user["missing"] = "wit";
                """, "no such property: org.febit.wit.script.bean.User#missing");
    }
}

