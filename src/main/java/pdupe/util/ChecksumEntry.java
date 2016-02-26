package pdupe.util;

import com.google.common.base.Preconditions;

/**
 * @author Gabor Imre
 */
public class ChecksumEntry {
    private final String checksum;
    private final String file;

    public ChecksumEntry(String checksumLine) {
        Preconditions.checkNotNull(checksumLine);
        int i1 = checksumLine.indexOf(' ');
        if (i1 <= 0 || i1 >= checksumLine.length() - 2) {
            throw new IllegalArgumentException(checksumLine);
        }
        if (checksumLine.charAt(i1+1) != '*' ) {
            throw new IllegalArgumentException(checksumLine);
        }
        this.checksum = checksumLine.substring(0, i1);
        this.file = checksumLine.substring(i1 + 2);
    }

    public String getFile() {
        return file;
    }

    public String getChecksum() {
        return checksum;
    }



}
