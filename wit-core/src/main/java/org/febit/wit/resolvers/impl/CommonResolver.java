// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.Out;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.OutResolver;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.util.bean.BeanUtil;

/**
 *
 * @author zqq90
 */
public class CommonResolver implements GetResolver<Object>, SetResolver<Object>, OutResolver<Object> {

    @Override
    public Object get(final Object object, final Object property) {
        try {
            return BeanUtil.get(object, String.valueOf(property));
        } catch (Exception e) {
            throw new ScriptRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void set(final Object object, final Object property, final Object value) {
        try {
            BeanUtil.set(object, String.valueOf(property), value);
        } catch (Exception e) {
            throw new ScriptRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void render(final Out out, final Object bean) {
        out.write(bean.toString());
    }

    @Override
    public Class<Object> getMatchClass() {
        return Object.class;
    }
}
