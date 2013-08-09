package webit.script.resolvers.impl;

import java.util.Map;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author Zqq
 */
public class MapResolver implements GetResolver, SetResolver {

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Map.class;
    }

    public Object get(Object object, Object property) {
        Map map = (Map) object;
        return map.get(property);
    }

    public boolean set(Object object, Object property, Object value) {
        Map map = (Map) object;
        map.put(property, value);
        return true;
    }
}
