package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.List;

/**
 * Add directory/file to the blob.
 *
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Add files/directories to the blob")
public class CommandAddParametes {

    @Parameter(required = true, description = "Files/directories to add")
    List<String> locations;
}
