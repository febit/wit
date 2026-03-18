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
import org.febit.wit.extern.servlet.facade.HttpServletRequestParameters;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

class HttpServletRequestParametersAccessorTest {

    @Test
    void test() {
        var req = mock(HttpServletRequest.class);
        var reqAccessor = new HttpServletRequestAccessor();
        var accessor = new HttpServletRequestParametersAccessor();

        var params = (HttpServletRequestParameters) reqAccessor.get(req, "parameters");
        assertNotNull(params);
        assertEquals(params, reqAccessor.get(req, "params"));

        reset(req);
        assertNull(accessor.get(params, null));
        verify(req, never()).getParameter(any());

        reset(req);
        accessor.get(params, "test");
        verify(req).getParameter("test");
    }
}
