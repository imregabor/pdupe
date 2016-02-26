package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Find duplicate candidates in binary files.
 *
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Add files/directories to the blob")
public class FindDupeCandidatesParameters {

    enum MatchMode {
        SIZE, NAME, CHECKSUM
    }

    @Parameter(names = "-q", required = true, description = "Query binary blob to read.")
    String q;

    @Parameter(names = "-t", required = true, description = "Target binary blob to read.")
    String t;

    @Parameter(names = "-m", description = "Match mode (use SIZE, NAME or CHECKSUM).")
    MatchMode mm = MatchMode.SIZE;

    @Parameter(names = "-s", description = "Checksum type for CHECKSUM match.")
    String s = "SHA512";

    @Parameter(names = "-oq", description = "Text file to write selected query files.")
    String oq;

    @Parameter(names = "-ot", description = "Text file to write selected target files.")
    String ot;

}
