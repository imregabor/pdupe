package pdupe.util;

import com.google.common.base.Preconditions;
import pdupe.cli.Checksum;

/**
 * @author Gabor Imre
 */
public class ChecksumEntry {
    private final Checksum method;
    private final String checksum;
    private final String file;

    public ChecksumEntry(String checksumLine) {
        Preconditions.checkNotNull(checksumLine);
        int i0 = checksumLine.indexOf(' ');
        if (i0 <= 1 || i0 >= checksumLine.length() - 4) {
            throw new IllegalArgumentException(checksumLine);
        }
        this.method = Checksum.valueOf(checksumLine.substring(0, i0));
        int i1 = checksumLine.indexOf(' ', i0 + 1);
        if (i1 <= i0 + 1 || i1 >= checksumLine.length() - 2) {
            throw new IllegalArgumentException(checksumLine);
        }
        if (checksumLine.charAt(i1+1) != '*' ) {
            throw new IllegalArgumentException(checksumLine);
        }
        this.checksum = checksumLine.substring(i0 + 1, i1);
        this.file = checksumLine.substring(i1 + 2);
    }

    public Checksum getMethod() {
        return this.method;
    }

    public String getFile() {
        return this.file;
    }

    public String getChecksum() {
        return this.checksum;
    }



}
