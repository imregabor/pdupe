package pdupe.util;

import com.beust.jcommander.IStringConverter;
import com.google.common.base.Supplier;
import java.io.File;
import org.apache.commons.io.LineIterator;

/**
 * Type converter for PrintStream outputs.
 *
 * @author Gabor Imre
 */
public class JCommanderCloseableLineIteratorSupplierConverter implements IStringConverter<Supplier<CloseableLineIterator>> {

    @Override
    public Supplier<CloseableLineIterator> convert(final String value) {
        final File f = new File(value);
        if (!f.exists()) {
            throw new IllegalArgumentException("File not found: " + value);
        }
        return new Supplier<CloseableLineIterator>() {
            @Override
            public CloseableLineIterator get() {
                final LineIterator ret = Util.lineIterator(value);
                return new CloseableLineIterator() {

                    @Override
                    public boolean hasNext() {
                        return ret.hasNext();
                    }

                    @Override
                    public String next() {
                        return ret.next();
                    }

                    @Override
                    public void close() {
                        ret.close();
                    }
                };

            }

            @Override
            public String toString() {
                return "file \"" + value + "\"";
            }
        };
    }

}
