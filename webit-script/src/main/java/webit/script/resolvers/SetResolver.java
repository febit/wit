package webit.script.resolvers;

/**
 *
 * @author Zqq
 */
public interface SetResolver extends Resolver {

    boolean set(Object object, Object property, Object value);
}
