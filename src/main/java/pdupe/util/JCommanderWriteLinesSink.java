package pdupe.util;

import com.beust.jcommander.IStringConverter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.apache.commons.io.FileUtils;

/**
 * Type converter for PrintStream outputs.
 *
 * @author Gabor Imre
 */
public class JCommanderWriteLinesSink implements IStringConverter<Sink<Collection<String>>> {

    @Override
    public Sink<Collection<String>> convert(final String value) {
        final File f = new File(value);
        if (f.exists()) {
            throw new IllegalArgumentException("File already exists: " + value);
        }
        return new Sink<Collection<String>>() {
            private boolean written = false;

            @Override
            public void put(Collection<String> t) {
                if (this.written) {
                    throw new IllegalStateException("Already written");
                } else {
                    this.written = true;
                }
                try {
                    FileUtils.writeLines(f, "UTF-8", t);
                } catch (IOException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }

            @Override
            public String toString() {
                return "file \"" + value + "\"";
            }
        };
    }

}
