package pdupe.util;

import com.google.common.collect.BiMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utils for {@link BiMap} handling.
 *
 * @author Gabor Imre
 */
public final class BiMaps {

    public static <K, V> Set<K> keys(BiMap<K, V> map, Collection<V> values) {
        return values(map.inverse(), values);
    }

    public static <K, V> Set<V> values(BiMap<K, V> map, Collection<K> keys) {
        final Set<V> ret = new HashSet<>();
        for(K key : keys) {
            final V value = map.get(key);
            if (value == null) {
                throw new IllegalArgumentException("No value for key " + key);
            }
            ret.add(value);
        }
        return ret;
    }

}
