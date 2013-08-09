package webit.script.resolvers;

/**
 *
 * @author Zqq
 */
public abstract interface Resolver {

    MatchMode getMatchMode();

    Class<?> getMatchClass();
}
