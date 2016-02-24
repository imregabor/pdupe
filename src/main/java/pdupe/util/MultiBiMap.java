package pdupe.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * BiMap allowing mutliple keys to map the same value.
 *
 * @param <K> key type
 * @param <V> value type
 *
 * @author Gabor Imre
 */
public interface MultiBiMap<K, V> extends Serializable {
    /**
     * Put key-value pair.
     *
     * @param key Key
     * @param value Value
     * @throws IllegalArgumentException when the exact same key-value pair is already contained by this map
     */
    void put(K key, V value);

    /**
     * Get values for a key.
     *
     * @param key Key to query
     * @return Associated values
     */
    V get(K key);

    /**
     * Get keys for a value.
     *
     * @param value Value to query
     * @return Associated keys
     */
    Collection<K> getKeys(V value);

    /**
     * Get keys for multiple values.
     *
     * @param values Values to query
     * @return Keys for specified values
     */
    Collection<K> getKeys(Collection<V> values);

    /**
     * Check for a presence of a key.
     *
     * @param key Key to check
     * @return {@code true} when key is present
     */
    boolean containsKey(K key);

    /**
     * Unique value set.
     *
     * @return Unique values as a set
     */
    Set<V> uniqueValues();
}
