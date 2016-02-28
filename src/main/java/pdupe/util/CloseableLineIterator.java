package pdupe.util;

import java.io.Closeable;
import java.util.Iterator;
import org.apache.commons.io.LineIterator;

/**
 * Because {@link LineIterator} is not {@link Closeable}.
 * @author Gabor Imre
 */
public interface CloseableLineIterator extends Iterator<String>, Closeable {

    @Override
    public void close();

}
