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
package org.febit.wit.runtime.accessor;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.util.bean.model.EmptyBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmptyBeanAccessorTest {

    @Test
    void object() {
        var bean = new Object();
        var accessor = EmptyBeanAccessor.get();

        Exception exception;
        exception = assertThrows(ScriptEvaluateException.class,
                () -> accessor.get(bean, "f1"));
        assertEquals("no such property: " + Object.class.getName() + "#f1", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> accessor.set(bean, "f1", "f1"));
        assertEquals("no such property: " + Object.class.getName() + "#f1", exception.getMessage());
    }

    @Test
    void emptyBean() {
        var bean = new EmptyBean();
        var accessor = EmptyBeanAccessor.get();

        Exception exception;
        exception = assertThrows(ScriptEvaluateException.class,
                () -> accessor.get(bean, "f1"));
        assertEquals("no such property: " + EmptyBean.class.getName() + "#f1", exception.getMessage());

        exception = assertThrows(ScriptEvaluateException.class,
                () -> accessor.set(bean, "f1", "f1"));
        assertEquals("no such property: " + EmptyBean.class.getName() + "#f1", exception.getMessage());
    }

}
