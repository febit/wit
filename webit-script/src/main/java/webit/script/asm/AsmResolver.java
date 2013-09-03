// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author Zqq
 */
public abstract class AsmResolver implements GetResolver,SetResolver {

    private final Class matchClass;

    public AsmResolver(Class matchClass) {
        this.matchClass = matchClass;
    }

    public MatchMode getMatchMode() {
        return MatchMode.EQUALS;
    }

    public Class getMatchClass() {
        return matchClass;
    }

    protected ScriptRuntimeException createUnwriteablePropertyException(Object property) {
        return new ScriptRuntimeException("Unwriteable property " + matchClass.getName() + "#" + property);
    }

    protected ScriptRuntimeException createUnreadablePropertyException(Object property) {
        return new ScriptRuntimeException("Unreadable property " + matchClass.getName() + "#" + property);
    }

    protected ScriptRuntimeException createNoSuchPropertyException(Object property) {
        return new ScriptRuntimeException("Invalid property " + matchClass.getName() + "#" + property);
    }
}
