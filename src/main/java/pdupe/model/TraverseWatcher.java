package pdupe.model;

import java.io.File;

/**
 * Callback for traverse events.
 *
 * @author Gabor Imre
 */
public interface TraverseWatcher {

    /**
     * A single file to be added.
     *
     * @param cp Canonical path, see {@link File#getCanonicalPath()}
     * @param length File size, see {@link File#length()}
     *
     */
    void fileToBeAdded(String cp, long length);

    /**
     * A single file add is vetoed.
     *
     * @param cp Canonical path, see {@link File#getCanonicalPath()}
     * @param length File size, see {@link File#length()}
     */
    void fileVetoed(String cp, long length);

    /**
     * Traversal of a directory is vetoed.
     *
     * @param cp Canonical path, see {@link File#getCanonicalPath()}
     */
    void directoryVetoed(String cp);


    /**
     * Starting traversal of a directory.
     *
     * @param cp Canonical path, see {@link File#getCanonicalPath()}
     */
    void directoryEntered(String cp);

    /**
     * Traversal of a directory done.
     *
     * @param cp Canonical path, see {@link File#getCanonicalPath()}
     */
    void directoryDone(String cp);
}
