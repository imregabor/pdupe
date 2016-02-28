package pdupe.util;

import com.beust.jcommander.IStringConverter;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Type converter for PrintStream outputs.
 *
 * @author Gabor Imre
 */
public class JCommanderObjectDeserializerSupplier implements IStringConverter<Supplier<Object>> {

    @Override
    public Supplier<Object> convert(final String value) {
        final File f = new File(value);
        if (!f.exists()) {
            throw new IllegalArgumentException("File not found: " + value);
        }
        return new Supplier<Object>() {
            @Override
            public Object get() {
                try (ObjectInputStream ois = Util.ois(value)) {
                    return ois.readObject();
                } catch (IOException | ClassNotFoundException ex) {
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
