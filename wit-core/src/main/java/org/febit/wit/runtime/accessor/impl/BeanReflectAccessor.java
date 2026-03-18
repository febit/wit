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
package org.febit.wit.runtime.accessor.impl;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.io.Out;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Render;
import org.febit.wit.runtime.accessor.Setter;
import org.febit.wit.util.bean.BeanUtils;
import org.jspecify.annotations.Nullable;

public class BeanReflectAccessor implements Getter<Object>, Setter<Object>, Render<Object> {

    @Nullable
    @Override
    public Object get(Object obj, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("Property should not be null for bean access.");
        }
        try {
            return BeanUtils.get(obj, property.toString());
        } catch (Exception e) {
            throw new ScriptEvaluateException(e.getMessage(), e);
        }
    }

    @Override
    public void set(Object obj, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            throw new ScriptEvaluateException("Property should not be null for bean access.");
        }
        try {
            BeanUtils.set(obj, property.toString(), value);
        } catch (Exception e) {
            throw new ScriptEvaluateException(e.getMessage(), e);
        }
    }

    @Override
    public void render(Out out, Object obj) {
        out.write(obj.toString());
    }
}
