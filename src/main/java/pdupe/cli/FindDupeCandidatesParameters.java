package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Supplier;
import java.util.Collection;
import pdupe.util.JCommanderObjectDeserializerSupplier;
import pdupe.util.JCommanderWriteLinesSink;
import pdupe.util.Sink;

/**
 * Find duplicate candidates in binary files.
 *
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Add files/directories to the blob")
public class FindDupeCandidatesParameters {

    @Parameter(
            names = "-q",
            required = true,
            converter = JCommanderObjectDeserializerSupplier.class,
            description = "Query binary blob to read.")
    Supplier<Object> q;

    @Parameter(
            names = "-t",
            required = true,
            converter = JCommanderObjectDeserializerSupplier.class,
            description = "Target binary blob to read.")
    Supplier<Object> t;

    @Parameter(names = "-a", description = "Single attribute to compare. Use \"name\", \"size\", \"MD5\", \"SHA1\" or \"SHA512\"")
    String a;

    @Parameter(
            names = "-oq",
            converter = JCommanderWriteLinesSink.class,
            description = "Text file to write selected query files.")
    Sink<Collection<String>> oq;

    @Parameter(
            names = "-ot",
            converter = JCommanderWriteLinesSink.class,
            description = "Text file to write selected target files.")
    Sink<Collection<String>> ot;

}
