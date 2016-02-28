package pdupe.util;

import com.beust.jcommander.IStringConverter;
import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * @author Gabor Imre
 */
public class JCommanderLinesSupplierConverter implements IStringConverter<Supplier<List<String>>> {

    @Override
    public Supplier<List<String>> convert(final String value) {
        final File f = new File(value);
        if (!f.exists()) {
            throw new IllegalArgumentException("File not found: " + value);
        }
        return new Supplier<List<String>>() {
            @Override
            public List<String> get() {
                try {
                    return FileUtils.readLines(f, Charsets.UTF_8);
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
