
package webit.script.io;


/**
 *
 * @author Zqq
 */
public interface Out {
    void write(byte[] bytes);
    void write(String string);
    String getEncoding();
}
