package pdupe.cli;

import com.beust.jcommander.Parameter;

/**
 * CLI arguments.
 *
 * @author Gabor Imre
 */
public class MainParameters {

    @Parameter(names = "-h", help = true, description = "Print help and exit.")
    boolean help;

}
