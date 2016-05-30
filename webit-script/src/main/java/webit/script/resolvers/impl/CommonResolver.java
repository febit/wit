// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.SetResolver;
import webit.script.util.bean.BeanUtil;

/**
 *
 * @author zqq90
 */
public class CommonResolver implements GetResolver, SetResolver, OutResolver {

    @Override
    public Object get(final Object object, final Object property) {
        try {
            return BeanUtil.get(object, String.valueOf(property));
        } catch (Exception e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
    }

    @Override
    public void set(final Object object, final Object property, final Object value) {
        try {
            BeanUtil.set(object, String.valueOf(property), value);
        } catch (Exception e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
    }

    @Override
    public void render(final Out out, final Object bean) {
        out.write(bean.toString());
    }

    @Override
    public Class getMatchClass() {
        return Object.class;
    }
}
