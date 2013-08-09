package webit.script.resolvers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jodd.util.ReflectUtil;
import static webit.script.resolvers.MatchMode.EQUALS;
import static webit.script.resolvers.MatchMode.INSTANCEOF;
import static webit.script.resolvers.MatchMode.REGIST;
import webit.script.resolvers.impl.CommonResolver;

/**
 *
 * @author Zqq
 */
public class ResolverManager {

    private ConcurrentMap<Class, GetResolver> getResolverMap;
    private ConcurrentMap<Class, SetResolver> setResolverMap;
    private ConcurrentMap<Class, ToBytesResolver> toBytesResolverMap;
    //
    private ArrayList<GetResolver> getResolvers;
    private ArrayList<SetResolver> setResolvers;
    private ArrayList<ToBytesResolver> toBytesResolvers;
    private ArrayList<Class> getResolverTypes;
    private ArrayList<Class> setResolverTypes;
    private ArrayList<Class> toBytesResolverTypes;
    //
    private CommonResolver commonResolver;

    public ResolverManager() {
        getResolverMap = new ConcurrentHashMap<Class, GetResolver>();
        setResolverMap = new ConcurrentHashMap<Class, SetResolver>();
        toBytesResolverMap = new ConcurrentHashMap<Class, ToBytesResolver>();


        getResolvers = new ArrayList<GetResolver>();
        setResolvers = new ArrayList<SetResolver>();
        toBytesResolvers = new ArrayList<ToBytesResolver>();
        getResolverTypes = new ArrayList<Class>();
        setResolverTypes = new ArrayList<Class>();
        toBytesResolverTypes = new ArrayList<Class>();

        commonResolver = new CommonResolver();
    }

    private GetResolver getGetResolver(Object bean) {
        Class type = bean.getClass();
        GetResolver resolver = getResolverMap.get(type);
        if (resolver == null) {
            for (int i = 0; i < getResolverTypes.size(); i++) {
                if (ReflectUtil.isSubclass(type, getResolverTypes.get(i))) {
                    resolver = getResolvers.get(i);
                    break;
                }
            }
        }
        if (resolver == null) {
            resolver = commonResolver;
        }

        GetResolver old = getResolverMap.putIfAbsent(type, resolver);
        if (old != null) {
            resolver = old;
        }
        return resolver;
    }

    private SetResolver getSetResolver(Object bean) {
        Class type = bean.getClass();
        SetResolver resolver = setResolverMap.get(type);
        if (resolver == null) {
            for (int i = 0; i < setResolverTypes.size(); i++) {
                if (ReflectUtil.isSubclass(type, setResolverTypes.get(i))) {
                    resolver = setResolvers.get(i);
                    break;
                }
            }
        }
        if (resolver == null) {
            resolver = commonResolver;
        }

        SetResolver old = setResolverMap.putIfAbsent(type, resolver);
        if (old != null) {
            resolver = old;
        }
        return resolver;
    }

    private ToBytesResolver getToBytesResolver(Object bean) {
        Class type = bean.getClass();
        ToBytesResolver resolver = toBytesResolverMap.get(type);
        if (resolver == null) {
            for (int i = 0; i < toBytesResolverTypes.size(); i++) {
                if (ReflectUtil.isSubclass(type, toBytesResolverTypes.get(i))) {
                    resolver = toBytesResolvers.get(i);
                    break;
                }
            }
        }
        if (resolver == null) {
            resolver = commonResolver;
        }

        ToBytesResolver old = toBytesResolverMap.putIfAbsent(type, resolver);
        if (old != null) {
            resolver = old;
        }
        return resolver;
    }

    public void init(Resolver[] resolvers) {
        for (int i = 0; i < resolvers.length; i++) {
            Resolver resolver = resolvers[i];
            if (resolver.getMatchMode() == MatchMode.REGIST) {
                ((RegistModeResolver) resolver).regist(this);
            } else {
                registResolver(resolver.getMatchClass(), resolver, resolver.getMatchMode());
            }
        }

        getResolvers.trimToSize();
        setResolvers.trimToSize();
        toBytesResolvers.trimToSize();
        getResolverTypes.trimToSize();
        setResolverTypes.trimToSize();
        toBytesResolverTypes.trimToSize();
        //
    }

    public final boolean registResolver(Class type, Resolver resolver, MatchMode matchMode) {
        switch (matchMode) {
            case INSTANCEOF:
                if (resolver instanceof GetResolver) {
                    getResolverTypes.add(type);
                    getResolvers.add((GetResolver) resolver);
                }
                if (resolver instanceof SetResolver) {
                    setResolverTypes.add(type);
                    setResolvers.add((SetResolver) resolver);
                }
                if (resolver instanceof ToBytesResolver) {
                    toBytesResolverTypes.add(type);
                    toBytesResolvers.add((ToBytesResolver) resolver);
                }
                break;

            case EQUALS:
                if (resolver instanceof GetResolver) {
                    getResolverMap.putIfAbsent(type, (GetResolver) resolver);
                }
                if (resolver instanceof SetResolver) {
                    setResolverMap.putIfAbsent(type, (SetResolver) resolver);
                }
                if (resolver instanceof ToBytesResolver) {
                    toBytesResolverMap.putIfAbsent(type, (ToBytesResolver) resolver);
                }
                break;
            case REGIST:
            default:
                //EXCEPTION??
                return false;
        }

        return true;
    }

    public final Object get(Object bean, Object property) {
        if (bean != null) {
            return getGetResolver(bean).get(bean, property);
        } else {
            return null;
        }
    }

    public final boolean set(Object bean, Object property, Object value) {
        if (bean != null) {
            return getSetResolver(bean).set(bean, property, value);
        } else {
            return false;
        }
    }

    public final byte[] toBytes(Object bean, String encoding) throws UnsupportedEncodingException {
        if (bean != null) {
            if (bean.getClass() == byte[].class) {
                return (byte[]) bean;
            } else {
                return getToBytesResolver(bean).toBytes(bean, encoding);
            }
        } else {
            return null;
        }
    }
}
