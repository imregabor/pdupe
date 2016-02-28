package pdupe.model;

import java.io.File;

/**
 * @author Gabor Imre
 */
public class FileNameAttribute extends AbstractAttribute<String> {

    @Override
    public String extractValue(File file) {
        return file.getName();
    }

}
