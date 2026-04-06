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

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReflectBeanAccessorFactoryTest {

    @SuppressWarnings("unused")
    public static class Foo {

        public String public0 = "public0";
        public final String public1 = "public1";
        public String public2 = "public2";
        @Getter
        @Setter
        private String private0 = "private0";
        @Setter
        private Class<?> private1 = String.class;
        @Setter
        @Getter
        private boolean bool = false;

        public void setPublic2(String public2) {
            this.public2 = "set:" + public2;
        }

        public String getPublic2() {
            return "get:" + public2;
        }

    }

    @Test
    void test() {
        Foo foo = new Foo();

        var getter = ReflectBeanAccessorFactory.get().getter(Foo.class);
        var setter = ReflectBeanAccessorFactory.get().setter(Foo.class);

        assertEquals(foo.public0, getter.get(foo, "public0"));
        assertEquals(foo.public1, getter.get(foo, "public1"));
        assertEquals(foo.getPublic2(), getter.get(foo, "public2"));
        assertEquals(foo.getPrivate0(), getter.get(foo, "private0"));

        String newStringValue = "new public";
        setter.set(foo, "public0", newStringValue);
        //setter.set(foo, "public1", "new public"); Exception
        setter.set(foo, "private0", newStringValue);

        assertEquals(newStringValue, foo.public0);
        assertEquals(newStringValue, foo.getPrivate0());

        assertEquals(foo.isBool(), getter.get(foo, "bool"));

        foo.setBool(false);
        setter.set(foo, "bool", true);
        assertTrue(foo.isBool());

    }
}
