package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public class IterGetResolver implements GetResolver {

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Iter.class;
    }

    public Object get(Object object, Object property) {
        Iter iter = (Iter) object;
        //TODO:可优化
        if (property.equals("hasNext")) {
            return iter.hasNext();
        }
        
        else if (property.equals("index")) {
            return iter.index();
        }
        
        else if (property.equals("isFirst")) {
            return iter.isFirst();
        }
        
        else if (property.equals("next")) {
            return iter.next();
        }
        
        throw new ScriptRuntimeException("Invalid property or can't read: webit.tl.util.collection.Iter#"+property);
    }
}
