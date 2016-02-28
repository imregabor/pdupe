package pdupe.model;

import java.io.File;
import pdupe.util.MultiBiMap;

/**
 * A generic non-unique attribute of files.
 *
 * Typical examples: name, size, extension, checksum
 *
 * @param <T> Attribute type
 *
 * @author Gabor Imre
 */
public interface Attribute<T> extends MultiBiMap<Long, T>{

    /**
     * Extract attribute value from a file.
     * @param file File to use
     * @return Attribute value
     */
    T extractValue(File file);

    /**
     * Put a file into the attribute storage.
     *
     * Implementations typically use {@link #extractValue(java.io.File)} followed by
     * {@link #put(java.lang.Object, java.lang.Object)}.
     *
     * @param id ID to use
     * @param file File to use for attribute value creation
     */
    void putFile(long id, File file);
}
