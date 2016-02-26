package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Add directory/file to the blob.
 *
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Add files/directories to the blob")
public class CommandMergeChecksum {

    @Parameter(names = "-b", required = true, description = "Binary blob to read and write")
    String b;

    @Parameter(names = "-i", required = true, description = "Checksum file to read")
    String i;

    @Parameter(names = "-s", description = "Checksum type")
    String s = "SHA512";
}
