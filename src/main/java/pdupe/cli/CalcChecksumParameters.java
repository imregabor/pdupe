package pdupe.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Supplier;
import java.io.PrintStream;
import java.util.List;
import pdupe.util.JCommanderLinesSupplierConverter;
import pdupe.util.JCommanderPrintStremSupplierConverter;

/**
 * Find duplicate candidates in binary files.
 *
 * @author Gabor Imre
 */
@Parameters(commandDescription = "Calculate checksums")
public class CalcChecksumParameters {


    @Parameter(
            names = "-i",
            required = true,
            converter = JCommanderLinesSupplierConverter.class,
            description = "File list to launch checksum calculation")
    Supplier<List<String>> i;

    @Parameter(names = "-m", description = "Method (use MD5, SHA1 or SHA512).")
    Checksum m = Checksum.SHA512;

    @Parameter(
            names = "-o",
            required = true,
            converter = JCommanderPrintStremSupplierConverter.class,
            description = "Output file to write")
    Supplier<PrintStream> o;

}
