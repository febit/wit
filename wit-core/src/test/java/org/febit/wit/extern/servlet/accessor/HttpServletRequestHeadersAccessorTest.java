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
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeaders;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

class HttpServletRequestHeadersAccessorTest {

    @Test
    void test() {
        var req = mock(HttpServletRequest.class);
        var reqAccessor = new HttpServletRequestAccessor();
        var accessor = new HttpServletRequestHeadersAccessor();

        var headers = (HttpServletRequestHeaders) reqAccessor.get(req, "headers");
        assertNotNull(headers);

        reset(req);
        assertNull(accessor.get(headers, null));
        verify(req, never()).getHeaders(any());

        reset(req);
        assertNull(accessor.get(headers, "test"));
        verify(req).getHeaders("test");
    }
}
