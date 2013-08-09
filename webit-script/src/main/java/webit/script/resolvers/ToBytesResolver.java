package webit.script.resolvers;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author Zqq
 */
public interface ToBytesResolver extends Resolver {

    byte[] toBytes(Object bean, String encoding) throws UnsupportedEncodingException;
}
