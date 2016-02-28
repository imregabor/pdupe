package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.io.File;
import pdupe.util.JCommanderFileConverter;

/**
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Read a single image file and show its extracted metadata.")
public class CommandShowMetadataParameters {

    @Parameter(
            names = "-i",
            required = true,
            converter = JCommanderFileConverter.class,
            description = "Image file to read"
    )
    File i;

}
