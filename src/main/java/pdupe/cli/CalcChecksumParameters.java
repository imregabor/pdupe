package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Find duplicate candidates in binary files.
 *
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Calculate checksums")
public class CalcChecksumParameters {

    public enum Checksum {
        MD5("MD5"), SHA1("SHA-1"), SHA512("SHA-512");

        final String c;

        private Checksum(String c) {
            this.c = c;
        }

        public String getConst() {
            return this.c;
        }
    }

    @Parameter(names = "-i", required = true, description = "File list to launch checksum calculation")
    String i;

    @Parameter(names = "-m", description = "Method (use MD5, SHA1 or SHA512.")
    Checksum m = Checksum.SHA512;

    @Parameter(names = "-o", required = true, description = "Output file to write")
    String o;

}
