package pdupe.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hash based implementation of {@link MultiBiMap}.
 *
 * @param <K> Key type
 * @param <V> Value type
 *
 * @author Gabor Imre
 */
public final class HashMultiBiMap<K, V> implements MultiBiMap<K, V> {

    private static final long serialVersionUID = 0;

    /**
     * Forward mapping.
     */
    private final Map<K, V> forward;

    /**
     * Reverse mapping.
     */
    private final Multimap<V, K> reverse;

    private HashMultiBiMap() {
        this.forward = new HashMap<>();
        this.reverse = HashMultimap.create();
    }

    /**
     * Create an instance.
     *
     * @param <K> Key type
     * @param <V> Value type
     *
     * @return Created instance
     */
    public static <K, V> MultiBiMap<K, V> create() {
        return new HashMultiBiMap<>();
    }

    @Override
    public void put(K key, V value) {
        if (this.reverse.containsEntry(value, key)) {
            throw new IllegalArgumentException("Already contains key: " + key + " value: " + value);
        }
        final boolean b1 = this.forward.put(key, value) == null;
        final boolean b2 = this.reverse.put(value, key);
        if (!b1) {
            throw new IllegalStateException("Key: " + key + " value: " + value);
        }
        if (!b2) {
            throw new IllegalStateException("Key: " + key + " value: " + value);
        }
    }

    @Override
    public V get(K key) {
        return this.forward.get(key);
    }

    @Override
    public Collection<K> getKeys(V value) {
        return this.reverse.get(value);
    }

    @Override
    public boolean containsKey(K key) {
        return this.forward.containsKey(key);
    }

    @Override
    public Set<V> uniqueValues() {
        final Set<V> ret = new HashSet<>();
        ret.addAll(this.forward.values());
        return ret;
    }

    @Override
    public Collection<K> getKeys(Collection<V> values) {
        final List<K> ret = new ArrayList<>();
        for (V v : values) {
            final Collection<K> keys = this.reverse.get(v);
            if (keys == null || keys.isEmpty()) {
                throw new IllegalArgumentException("No value for " + v);
            }
            ret.addAll(keys);
        }
        return ret;
    }


}
