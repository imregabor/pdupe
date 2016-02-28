package pdupe.model;

import java.io.File;

/**
 * @author Gabor Imre
 */
public class FileSizeAttribute  extends AbstractAttribute<Long> {

    @Override
    public Long extractValue(File file) {
        return file.length();
    }

}
