package pdupe.model;

import java.io.File;
import pdupe.util.HashMultiBiMap;

/**
 * @author Gabor Imre
 */
public abstract class AbstractAttribute<T> extends HashMultiBiMap<Long, T> implements Attribute<T> {

    @Override
    public void putFile(long id, File file) {
        final T value = this.extractValue(file);
        this.put(id, value);
    }


}
