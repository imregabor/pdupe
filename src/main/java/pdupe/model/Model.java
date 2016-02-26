package pdupe.model;

import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pdupe.util.BiMaps;
import pdupe.util.ChecksumEntry;
import pdupe.util.HashMultiBiMap;
import pdupe.util.MultiBiMap;

/**
 * Data model.
 *
 * @author Gabor Imre
 */
public class Model implements Serializable {

    private static final long serialVersionUID = 0;

    /**
     * Next available ID to be used.
     */
    private long nextAvailableId;

    /**
     * ID to canonical path association.
     *
     * See {@link File#getCanonicalPath()}.
     */
    private final BiMap<Long, String> idToCanonicalPath;

    /**
     * File size.
     */
    private final MultiBiMap<Long, Long> idToSize;

    /**
     * Map file name to ID.
     */
    private final MultiBiMap<Long, String> idToName;

    /**
     * Checksums.
     *
     * Outer map: checksum type to checksums
     * Inner maps: id to checksum
     */
    private final Map<String, MultiBiMap<Long, String>> idToChecksum;

    /**
     * Create an empty store.
     */
    public Model() {
        this.nextAvailableId = 0;
        this.idToCanonicalPath = HashBiMap.create(100000);
        this.idToSize = HashMultiBiMap.create();
        this.idToName = HashMultiBiMap.create();
        this.idToChecksum = new HashMap<>();

    }

    /**
     * Sizes occurring in both sets.
     *
     * @param other Other set
     * @return File sizes occurring in both sets
     */
    public Set<Long> matchingSizes(Model other) {
        System.err.println("matchingSizes(...)");

        System.err.print(  "    collection sizes from this            ");
        final Set<Long> thisSizes = this.idToSize.uniqueValues();
        System.err.println("done.");
        System.err.println("        distinct size values: " + thisSizes.size());

        System.err.print(  "    collection sizes from query           ");
        final Set<Long> otherSizes = other.idToSize.uniqueValues();
        System.err.println("done.");
        System.err.println("        distinct size values: " + otherSizes.size());

        System.err.print(  "    compute intersection                  ");
        thisSizes.retainAll(otherSizes);
        System.err.println("done.");
        System.err.println("    All done. Distinct size values: " + thisSizes.size());

        return thisSizes;
    }

    public void addChecksums(String type, List<String> checksumLines) {
        if (!this.idToChecksum.containsKey(type)) {
            this.idToChecksum.put(type, HashMultiBiMap.<Long, String>create());
        }
        final MultiBiMap<Long, String> sums = this.idToChecksum.get(type);
        for (String s : checksumLines) {
            final ChecksumEntry e = new ChecksumEntry(s);
            if (!this.idToCanonicalPath.containsValue(e.getFile())) {
                throw new IllegalArgumentException("File not found in storage " + e.getFile());
            }
            sums.put(this.idToCanonicalPath.inverse().get(e.getFile()), e.getChecksum());
        }
    }

    public Collection<Long> idsFromChecksums(String type, Collection<String> checksums) {
        System.err.println("idsFromChecksums(" + type + ", ...)");
        System.err.println("    Input size:  " + checksums.size());
        final Collection<Long> ret = this.idToChecksum.get(type).getKeys(checksums);
        System.err.println("    Result size: " + ret.size());
        return ret;
    }


    public Collection<Long> idsFromNames(Collection<String> names) {
        System.err.println("idsFromNames()");
        System.err.println("    Input size:  " + names.size());
        final Collection<Long> ret = this.idToName.getKeys(names);
        System.err.println("    Result size: " + ret.size());
        return ret;
    }

    public Collection<Long> idsFromSizes(Collection<Long> sizes) {
        System.err.println("idsFromSizes()");
        System.err.println("    Input size:  " + sizes.size());
        final Collection<Long> ret = this.idToSize.getKeys(sizes);
        System.err.println("    Result size: " + ret.size());
        return ret;
    }

    public Collection<String> pathesFromIds(Collection<Long> ids) {
        return BiMaps.values(this.idToCanonicalPath, ids);
    }

    public Set<String> pathesFromNames(Collection<String> names) {
        return BiMaps.values(this.idToCanonicalPath, this.idToName.getKeys(names));
    }

    /**
     * Checksums occurring in both sets.
     *
     * @param type Checksum type
     * @param other Other set
     * @return Checksums occurring in both sets
     */
    public Set<String> matchingChecksums(String type, Model other) {
        System.err.println("nameMatchChecksums(...)");

        System.err.print(  "    collection checksums from this (target)   ");
        final Set<String> thisChecksums = this.idToChecksum.get(type).uniqueValues();
        System.err.println("done.");
        System.err.println("        distinct sum values: " + thisChecksums.size());

        System.err.print(  "    collection names from query           ");
        final Set<String> otherChecksums = other.idToChecksum.get(type).uniqueValues();
        System.err.println("done.");
        System.err.println("        distinct sum values: " + otherChecksums.size());

        System.err.print(  "    compute intersection                  ");
        thisChecksums.retainAll(otherChecksums);
        System.err.println("done.");
        System.err.println("    All done. Distinct sum values: " + thisChecksums.size());

        return thisChecksums;
    }


    /**
     * Names occurring in both sets.
     *
     * @param other Other set
     * @return File names occurring in both sets
     */
    public Set<String> matchingNames(Model other) {
        System.err.println("nameMatchIds(...)");

        System.err.print(  "    collection names from this (target)   ");
        final Set<String> thisNames = this.idToName.uniqueValues();
        System.err.println("done.");
        System.err.println("        distinct name values: " + thisNames.size());

        System.err.print(  "    collection names from query           ");
        final Set<String> otherNames = other.idToName.uniqueValues();
        System.err.println("done.");
        System.err.println("        distinct name values: " + otherNames.size());

        System.err.print(  "    compute intersection                  ");
        thisNames.retainAll(otherNames);
        System.err.println("done.");
        System.err.println("    All done. Distinct name values: " + thisNames.size());

        return thisNames;
    }


    /**
     * Store a new file.
     *
     * @param f File to store
     * @param w Watcher or {@code null}.
     * @param ignoreFile Predicate to ignore canonical path of the file to be added or {@code null}.
     */
    public void addFile(File f, TraverseWatcher w, Predicate<String> ignoreFile) {

        final String cp;
        final String name = f.getName();
        final long  siz;
        try {
            if (!f.exists()) {
                throw new IllegalArgumentException("File not exists " + f);
            }
            if (!f.isFile()) {
                throw new IllegalArgumentException("Not a file " + f);
            }
            cp = f.getCanonicalPath();
            siz = f.length();

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        if (this.idToCanonicalPath.containsValue(cp)) {
            throw new IllegalArgumentException("File already added \"" + cp + "\"");
        }

        if (ignoreFile != null && ignoreFile.apply(cp)) {
            if (w != null) {
                w.fileVetoed(cp, siz);
            }
        } else {
            this.idToCanonicalPath.put(this.nextAvailableId, cp);
            this.idToSize.put(this.nextAvailableId, siz);
            this.idToName.put(this.nextAvailableId, name);
            final long ret = this.nextAvailableId;
            this.nextAvailableId++;
            if (w != null) {
                w.fileToBeAdded(cp, siz);
            }
        }
    }

    /**
     * Recursively add files from a directory.
     *
     * @param d Directory to traverse
     * @param w Watcher or {@code null}.
     * @param ignoreFile Predicate to ignore canonical path of the file to be added or {@code null}.
     * @param ignoreDir Predicate to ignore canonical path of the directory to be traversed or {@code null}.
     */
    public void traverseDir(File d, TraverseWatcher w, Predicate<String> ignoreFile, Predicate<String> ignoreDir) {
        if (!d.exists()) {
            throw new IllegalArgumentException("Directory not exists: " + d);
        }

        if (!d.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + d);
        }

        final String cp;
        if (w != null || ignoreDir != null) {
            try {
                cp = d.getCanonicalPath();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            if (ignoreDir != null && ignoreDir.apply(cp)) {
                // veto
                if (w != null) {
                    w.directoryVetoed(cp);
                }
                return;
            } else {
                // no veto
                if (w != null) {
                    w.directoryEntered(cp);
                }
            }
        } else {
            cp = null;
        }

        for (File f : d.listFiles()) {
            if (f.isDirectory()) {
                traverseDir(f, w, ignoreFile, ignoreDir);
            } else {
                addFile(f, w, ignoreFile);
            }
        }

        if (w != null) {
            w.directoryDone(cp);
        }
    }

    /**
     * Auto add file/directory.
     *
     * @param af File/Directory to add
     * @param w Watcher or {@code null}.
     * @param ignoreFile Predicate to ignore canonical path of the file to be added or {@code null}.
     * @param ignoreDir Predicate to ignore canonical path of the directory to be traversed or {@code null}.
     */
    public void addAuto(File af, TraverseWatcher w, Predicate<String> ignoreFile, Predicate<String> ignoreDir) {
        if (af.isFile()) {
            // add file
            addFile(af, w, ignoreFile);
        } else if (af.isDirectory()) {
            // add directory
            traverseDir(af, w, ignoreFile, ignoreDir);
        }
    }

    /**
     * Write human readable listing.
     *
     * @param out Output to write
     * @param count Max items to write
     */
    public void list(StringBuilder out, int count) {
        out.append("Size\tPath\n");
        for(long l = 0; l < this.nextAvailableId && l < count; l++) {
            out.append(this.idToSize.get(l)).append("\t").append(this.idToCanonicalPath.get(l)).append("\n");
        }
        if (this.nextAvailableId > count) {
            out.append("\n")
                    .append("... ").append(this.nextAvailableId - count).append(" more\n");
        }
    }

    public String list(int count) {
        final StringBuilder b = new StringBuilder();
        list(b, count);
        return b.toString();
    }

    public long size() {
        return this.nextAvailableId;
    }
}
