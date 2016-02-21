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

    @Parameter(names = "-q", required = true, description = "Query blob.")
    String q;

    @Parameter(names = "-t", required = true, description = "Target blob.")
    String t;

}
