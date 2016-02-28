package pdupe.util;

import com.beust.jcommander.IStringConverter;
import com.google.common.base.Supplier;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * @author Gabor Imre
 */
public class JCommanderFileBytesSupplierConverter implements IStringConverter<Supplier<byte[]>> {

    @Override
    public Supplier<byte[]> convert(final String value) {
        final File f = new File(value);
        if (!f.exists()) {
            throw new IllegalArgumentException("File not found: " + value);
        }
        return new Supplier<byte[]>() {
            @Override
            public byte[] get() {
                try {
                    return FileUtils.readFileToByteArray(f);
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
