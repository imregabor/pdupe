package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.ArrayList;
import java.util.List;
import pdupe.util.JCommanderObjecSerializeSink;
import pdupe.util.Sink;

/**
 * Add directory/file to the blob.
 *
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Add files/directories to the blob")
public class CommandAddParametes {

    @Parameter(
            names = "-o",
            converter = JCommanderObjecSerializeSink.class,
            required = true,
            description = "Binary blob to write")
    Sink<Object> outfile;

    @Parameter(
            required = true,
            description = "Files/directories to add")
    List<String> locations;

    @Parameter(
            names = "-if",
            description = "Ignore file name pattern, applied to the canonical pathname of the files.")
    List<String> ignoreFilePatterns = new ArrayList<>();

    @Parameter(
            names = "-id",
            description = "Ignore dir name pattern, applied to the canonical pathname of the directories.")
    List<String> ignoreDirPatterns = new ArrayList<>();

    @Parameter(
            names = "-v",
            description = "Verbose ignore pattern matches.")
    boolean v;
}
