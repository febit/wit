package webit.script.resolvers.impl;

import java.io.UnsupportedEncodingException;
import jodd.bean.BeanUtil;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;
import webit.script.resolvers.ToBytesResolver;

/**
 *
 * @author Zqq
 */
public class CommonResolver implements GetResolver, SetResolver, ToBytesResolver {

    public Object get(Object object, Object property) {
        try {
            return BeanUtil.getProperty(object, String.valueOf(property));
        } catch (jodd.bean.BeanException e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
    }

    public boolean set(Object object, Object property, Object value) {
        try {
            return BeanUtil.setPropertySilent(object, String.valueOf(property), value);
        } catch (jodd.bean.BeanException e) {
            throw new ScriptRuntimeException(e.getMessage());
        }
    }

    public byte[] toBytes(Object bean, String encoding) throws UnsupportedEncodingException {
        return bean.toString().getBytes(encoding);
    }

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Object.class;
    }
}
