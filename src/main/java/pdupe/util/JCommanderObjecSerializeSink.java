package pdupe.util;

import com.beust.jcommander.IStringConverter;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author Gabor Imre
 */
public class JCommanderObjecSerializeSink implements IStringConverter<Sink<Object>> {

    @Override
    public Sink<Object> convert(final String value) {
        final File f = new File(value);
        if (f.exists()) {
            throw new IllegalArgumentException("File already exists: " + value);
        }
        return new Sink<Object>() {
            private boolean written = false;

            @Override
            public void put(Object t) {
                if (written) {
                    throw new IllegalStateException("Already written");
                } else {
                    written = true;
                }
                try (ObjectOutputStream oos = Util.oos(value)) {
                    oos.writeObject(t);
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
