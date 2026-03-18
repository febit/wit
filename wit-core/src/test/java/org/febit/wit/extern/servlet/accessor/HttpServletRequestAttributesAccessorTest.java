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
package org.febit.wit.extern.servlet.accessor;

import jakarta.servlet.http.HttpServletRequest;
import org.febit.wit.extern.servlet.facade.HttpServletRequestAttributes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

class HttpServletRequestAttributesAccessorTest {

    @Test
    void test() {
        var req = mock(HttpServletRequest.class);
        var reqAccessor = new HttpServletRequestAccessor();
        var accessor = new HttpServletRequestAttributesAccessor();

        var attrs = (HttpServletRequestAttributes) new HttpServletRequestAccessor().get(req, "attributes");

        assertNotNull(attrs);
        assertEquals(attrs, reqAccessor.get(req, "attrs"));

        reset(req);
        assertNull(accessor.get(attrs, null));
        verify(req, never()).getAttribute(any());

        reset(req);
        accessor.set(attrs, null, "value");
        verify(req, never()).setAttribute(any(), any());

        reset(req);
        accessor.get(attrs, "test");
        verify(req).getAttribute("test");

        reset(req);
        accessor.set(attrs, "test", "value");
        verify(req).setAttribute("test", "value");
    }
}
