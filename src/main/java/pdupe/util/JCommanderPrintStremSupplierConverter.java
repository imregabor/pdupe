package pdupe.util;

import com.beust.jcommander.IStringConverter;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.PrintStream;

/**
 * Type converter for PrintStream outputs.
 *
 * @author Gabor Imre
 */
public class JCommanderPrintStremSupplierConverter implements IStringConverter<Supplier<PrintStream>> {

    @Override
    public Supplier<PrintStream> convert(final String value) {
        final File f = new File(value);
        if (f.exists()) {
            throw new IllegalArgumentException("File already exists: " + value);
        }
        return new Supplier<PrintStream>() {
            @Override
            public PrintStream get() {
                return Util.ps(value);
            }

            @Override
            public String toString() {
                return "file \"" + value + "\"";
            }
        };
    }

}
