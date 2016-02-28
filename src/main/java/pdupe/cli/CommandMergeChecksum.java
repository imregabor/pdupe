package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Supplier;
import java.util.List;
import pdupe.util.JCommanderLinesSupplierConverter;
import pdupe.util.JCommanderObjecSerializeSink;
import pdupe.util.JCommanderObjectDeserializerSupplier;
import pdupe.util.Sink;

/**
 * Add directory/file to the blob.
 *
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Add checksum data to the blob")
public class CommandMergeChecksum {

    @Parameter(
            names = "-bi",
            required = true,
            converter = JCommanderObjectDeserializerSupplier.class,
            description = "Binary blob to read.")
    Supplier<Object> bi;

    @Parameter(
            names = "-ci",
            required = true,
            converter = JCommanderLinesSupplierConverter.class,
            description = "Checksum file to read")
    Supplier<List<String>> ci;

    @Parameter(
            names = "-bo",
            required = true,
            converter = JCommanderObjecSerializeSink.class,
            description = "Binary blob to write merged blob.")
    Sink<Object> bo;
}
