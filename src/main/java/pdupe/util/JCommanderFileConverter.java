package pdupe.util;

import com.beust.jcommander.IStringConverter;
import java.io.File;

/**
 * @author Gabor Imre
 */
public class JCommanderFileConverter implements IStringConverter<File> {

    @Override
    public File convert(final String value) {
        return new File(value);
    }

}
