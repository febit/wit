// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.SetResolver;
import webit.script.util.ScriptVoid;

/**
 *
 * @author zqq90
 */
public class ScriptVoidResolver implements GetResolver, SetResolver, OutResolver {

    public Object get(Object object, Object property) {
        throw new ScriptRuntimeException("'Void' type has no propertys.");
    }

    public MatchMode getMatchMode() {
        return MatchMode.EQUALS;
    }

    public Class<?> getMatchClass() {
        return ScriptVoid.class;
    }

    public boolean set(Object object, Object property, Object value) {
        throw new ScriptRuntimeException("'Void' type has no propertys.");
    }

    public void render(Out out, Object bean) {
        //render nothing
    }

}
