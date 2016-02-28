

package pdupe.cli;

import com.beust.jcommander.JCommander;
import com.google.common.base.Predicate;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import pdupe.model.Attribute;
import pdupe.model.Model;
import pdupe.model.TraverseWatcher;
import pdupe.model.VerbosingWatcher;
import pdupe.util.Util;

/**
 * CLI entry point.
 *
 * @author Gabor Imre
 */
public class Main {

    public static void main(String [] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchFieldException {


        final MainParameters p = new MainParameters();

        final CommandAddParametes commandAdd = new CommandAddParametes();
        final FindDupeCandidatesParameters commandFind = new FindDupeCandidatesParameters();
        final CalcChecksumParameters commandCalcSum = new CalcChecksumParameters();
        final CommandMergeChecksum commandMergeChecksum = new CommandMergeChecksum();
        final JCommander jc = new JCommander(p);
        jc.addCommand("add", commandAdd);
        jc.addCommand("find", commandFind);
        jc.addCommand("calcsum", commandCalcSum);
        jc.addCommand("mergesum", commandMergeChecksum);

        jc.parse(args);

        if (p.help) {
            jc.setProgramName(Main.class.getName());
            jc.usage();
            return;
        }

        System.err.println("CLI arguments:");
        System.err.println("    " + Arrays.toString(args));

        if ("add".equals(jc.getParsedCommand())) {
            // new model or deserialize
            final Model m = new Model();

            final TraverseWatcher w = new VerbosingWatcher(System.out, 1000);
            final Predicate<String> ids;
            if (commandAdd.ignoreDirPatterns == null || commandAdd.ignoreDirPatterns.isEmpty()) {
                ids = null;
            } else {
                ids = Util.vetoFromPatterns(commandAdd.ignoreDirPatterns, commandAdd.v);
            }
            final Predicate<String> ifs;
            if (commandAdd.ignoreFilePatterns == null || commandAdd.ignoreFilePatterns.isEmpty()) {
                ifs = null;
            } else {
                ifs = Util.vetoFromPatterns(commandAdd.ignoreFilePatterns, commandAdd.v);
            }


            System.err.println("Add files/dirs");
            for (String l : commandAdd.locations) {
                System.err.println("  Add " + l);
                final File fl = new File(l);
                if (!fl.exists()) {
                    throw new IllegalArgumentException("File not found: " + fl.getPath());
                }
                m.addAuto(fl, w, ifs, ids);

            }

            System.err.println();
            System.err.println("Peek into storage");
            System.err.println();
            System.err.println(m.list(20));
            System.err.println();

            System.err.println("Write to " + commandAdd.outfile);
            commandAdd.outfile.put(m);
            System.err.println("Done");

        } else if ("find".equals(jc.getParsedCommand())) {
            System.err.println("Read query blob from " + commandFind.q);
            final Model qm = (Model) commandFind.q.get();
            System.err.println("Read target from " + commandFind.t);
            final Model tm = (Model) commandFind.t.get();
            System.err.println("Done. Query size: " + qm.size() + ", target size: " + tm.size());


            final Attribute targetAttribute = tm.getAttribute(commandFind.a);
            final Attribute queryAttribute = qm.getAttribute(commandFind.a);
            final Set common = targetAttribute.commonValues(queryAttribute);
            final Collection<Long> qids = queryAttribute.getKeys(common);
            final Collection<Long> tids = targetAttribute.getKeys(common);

            if (commandFind.oq != null) {
                commandFind.oq.put(qm.pathesFromIds(qids));
            }
            if (commandFind.ot != null) {
                commandFind.ot.put(tm.pathesFromIds(tids));
            }

            System.err.println("All done.");
        } else if ("calcsum".equals(jc.getParsedCommand())) {

            System.err.println("Read files list from " + commandCalcSum.i);
            final List<String> files = commandCalcSum.i.get();
            System.err.println("   Read " + files.size() + " file names");

            final MessageDigest md = MessageDigest.getInstance(commandCalcSum.m.getAlgorithm());

            try (PrintStream ps = commandCalcSum.o.get()) {
                int n = 0;
                long l = 0;
                long t = System.currentTimeMillis();
                final long tt0 = t;
                for(String f : files) {
                    final byte[] plain = FileUtils.readFileToByteArray(new File(f));
                    final byte[] digest = md.digest(plain);
                    final String digestHex = Hex.encodeHexString(digest);
                    ps.println(commandCalcSum.m.name() + " " + digestHex + " *" + f);

                    n++;
                    l+= plain.length;
                    final long tt = System.currentTimeMillis();

                    if (tt - t >= 5000) {
                        final long dt = tt - tt0 == 0 ? 1 : tt - tt0;
                        System.err.println("   Processed " + n + " files, total size " + Util.formatSi(l) + "B, throughput " + Util.formatSi(1000 * l / dt) + "B/s");
                        t = tt;
                    }

                }
            }
            System.err.println("    Done.");
        } else if ("mergesum".equals(jc.getParsedCommand())) {

            System.err.println("Read model: " + commandMergeChecksum.bi);
            final Model m = (Model) commandMergeChecksum.bi.get();
            System.err.println("   Done. Size: " + m.size());

            m.addChecksums(commandMergeChecksum.ci.get());

            System.err.println("Write to " + commandMergeChecksum.bo);
            commandMergeChecksum.bo.put(m);

            System.err.println("Done.");

        } else {
            throw new IllegalArgumentException("No command specified");
        }

    }
}