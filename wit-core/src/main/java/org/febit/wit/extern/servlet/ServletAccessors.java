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
package org.febit.wit.extern.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestAccessor;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestAttributesAccessor;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestHeaderAccessor;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestHeadersAccessor;
import org.febit.wit.extern.servlet.accessor.HttpServletRequestParametersAccessor;
import org.febit.wit.extern.servlet.accessor.HttpSessionAccessor;
import org.febit.wit.extern.servlet.facade.HttpServletRequestAttributes;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeader;
import org.febit.wit.extern.servlet.facade.HttpServletRequestHeaders;
import org.febit.wit.extern.servlet.facade.HttpServletRequestParameters;
import org.febit.wit.runtime.accessor.AccessorConsumer;

@UtilityClass
public class ServletAccessors {

    public static void registerAll(AccessorConsumer consumer){
        consumer.accept(HttpServletRequest.class, new HttpServletRequestAccessor());
        consumer.accept(HttpServletRequestAttributes.class, new HttpServletRequestAttributesAccessor());
        consumer.accept(HttpServletRequestHeader.class, new HttpServletRequestHeaderAccessor());
        consumer.accept(HttpServletRequestParameters.class, new HttpServletRequestParametersAccessor());
        consumer.accept(HttpServletRequestHeaders.class, new HttpServletRequestHeadersAccessor());
        consumer.accept(HttpSession.class, new HttpSessionAccessor());
    }
}
