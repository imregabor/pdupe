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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Hash based implementation of {@link MultiBiMap}.
 *
 * @param <K> Key type
 * @param <V> Value type
 *
 * @author Gabor Imre
 */
public class HashMultiBiMap<K, V> implements MultiBiMap<K, V> {

    private final Log log = LogFactory.getLog(HashMultiBiMap.class);

    private static final long serialVersionUID = 0;

    /**
     * Forward mapping.
     */
    private final Map<K, V> forward;

    /**
     * Reverse mapping.
     */
    private final Multimap<V, K> reverse;

    protected HashMultiBiMap() {
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
        if (this.log.isInfoEnabled()) { this.log.info("getKeys(); value count: " + values.size()); }
        final List<K> ret = new ArrayList<>();
        for (V v : values) {
            final Collection<K> keys = this.reverse.get(v);
            if (keys == null || keys.isEmpty()) {
                throw new IllegalArgumentException("No value for " + v);
            }
            ret.addAll(keys);
        }
        if (this.log.isInfoEnabled()) { this.log.info("    done, return size: " + ret.size()); }
        return ret;
    }

    @Override
    public Set<V> commonValues(MultiBiMap<K, V> other) {
        if (this.log.isInfoEnabled()) { this.log.info("commonValues()"); }
        if (this.log.isInfoEnabled()) { this.log.info("  collect values from this (target)"); }
        final Set<V> thisValues = this.uniqueValues();
        if (this.log.isInfoEnabled()) { this.log.info("  done. distinct values: " + thisValues.size()); }
        if (this.log.isInfoEnabled()) { this.log.info("  collect values from other (query)"); }
        final Set<V> otherValues = other.uniqueValues();
        if (this.log.isInfoEnabled()) { this.log.info("  done. distinct values: " + otherValues.size()); }
        if (this.log.isInfoEnabled()) { this.log.info("  compute intersection"); }
        thisValues.retainAll(otherValues);
        if (this.log.isInfoEnabled()) { this.log.info("  done. common values:   " + thisValues.size()); }
        return thisValues;

    }


}
