
package webit.script.core.ast;

/**
 *
 * @author Zqq
 */
public interface Optimizable {
    Statment optimize() throws Exception;
}