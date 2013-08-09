package webit.script.core.ast;

/**
 *
 * @author Zqq
 */
public interface ResetableValue {

    Object get();

    boolean set(Object value);
}
