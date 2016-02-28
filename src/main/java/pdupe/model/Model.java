package pdupe.model;

import com.google.common.base.Preconditions;
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
import java.util.NoSuchElementException;
import pdupe.cli.Checksum;
import pdupe.util.BiMaps;
import pdupe.util.ChecksumEntry;

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
    private final FileSizeAttribute fileSize;

    /**
     * Map file name to ID.
     */
    private final FileNameAttribute fileName;

    /**
     * Checksums.
     *
     * Outer map: checksum type to checksums
     */
    private final Map<String, FileChecksumAttribute> checksums;

    /**
     * Create an empty store.
     */
    public Model() {
        this.nextAvailableId = 0;
        this.idToCanonicalPath = HashBiMap.create(100000);

        this.fileName = new FileNameAttribute();
        this.fileSize = new FileSizeAttribute();
        this.checksums = new HashMap<>();

    }

    /**
     * Retrieve an attribute.
     *
     * @param attributeName Attribute name
     * @return Attribute
     * @throws NoSuchElementException when specified attribute not fount
     */
    public Attribute getAttribute(String attributeName) {
        if ("name".equals(attributeName)) {
            return this.fileName;
        } else if ("size".equals(attributeName)) {
            return this.fileSize;
        } else if (this.checksums.containsKey(attributeName)) {
            return this.checksums.get(attributeName);
        } else {
            throw new NoSuchElementException("Attribute not found \"" + attributeName + "\"");
        }
    }

    public Collection<String> pathesFromIds(Collection<Long> ids) {
        return BiMaps.values(this.idToCanonicalPath, ids);
    }



    FileChecksumAttribute ensureChecksum(Checksum type) {
        Preconditions.checkNotNull(type);
        if (!this.checksums.containsKey(type.name())) {
            this.checksums.put(type.name(), new FileChecksumAttribute(type.getAlgorithm()));
        }
        return this.checksums.get(type.name());
    }


    public void addChecksums(List<String> checksumLines) {

        for (String s : checksumLines) {
            final ChecksumEntry e = new ChecksumEntry(s);
            if (!this.idToCanonicalPath.containsValue(e.getFile())) {
                throw new IllegalArgumentException("File not found in storage " + e.getFile());
            }
            final FileChecksumAttribute checksum = ensureChecksum(e.getMethod());
            checksum.put(this.idToCanonicalPath.inverse().get(e.getFile()), e.getChecksum());
        }
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
            this.fileSize.putFile(this.nextAvailableId, f);
            this.fileName.putFile(this.nextAvailableId, f);
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
            out.append(this.fileSize.get(l)).append("\t").append(this.idToCanonicalPath.get(l)).append("\n");
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
