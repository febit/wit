// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import jodd.bean.BeanUtil;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;
import webit.script.resolvers.OutResolver;

/**
 *
 * @author Zqq
 */
public class CommonResolver implements GetResolver, SetResolver, OutResolver{


    public Object get(final Object object, final Object property) {
        try {
            return BeanUtil.getProperty(object, String.valueOf(property));
        } catch (jodd.bean.BeanException e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
    }

    public boolean set(final Object object, final Object property, final Object value) {
        try {
            return BeanUtil.setPropertySilent(object, String.valueOf(property), value);
        } catch (jodd.bean.BeanException e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
    }

    public void render(final Out out, final Object bean) {
        out.write(bean.toString());
    }

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Object.class;
    }


}
