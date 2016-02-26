

package pdupe.cli;

import com.beust.jcommander.JCommander;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
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

    public static void main(String [] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        final MainParameters p = new MainParameters();
        final CommandAddParametes commandAdd = new CommandAddParametes();
        final FindDupeCandidatesParameters commandFind = new FindDupeCandidatesParameters();
        final CalcChecksumParameters commandCalcSum = new CalcChecksumParameters();
        final JCommander jc = new JCommander(p);
        jc.addCommand("add", commandAdd);
        jc.addCommand("find", commandFind);
        jc.addCommand("calcsum", commandCalcSum);
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

            System.err.println("Write to " + commandAdd.outfile);
            try (ObjectOutputStream oos = Util.oos(commandAdd.outfile)) {
                oos.writeObject(m);
                System.err.println("    Done.");
            }

        } else if ("find".equals(jc.getParsedCommand())) {
            System.err.println("Read query: " + commandFind.q);
            final Model qm;
            try (ObjectInputStream ois = Util.ois(commandFind.q)) {
                qm = (Model) ois.readObject();
            }
            System.err.println("   Done. Size: " + qm.size());
            System.err.println("Read target: " + commandFind.t);
            final Model tm;
            try (ObjectInputStream ois = Util.ois(commandFind.t)) {
                tm = (Model) ois.readObject();
            }
            System.err.println("   Done. Size: " + tm.size());


            final Collection<Long> qids;
            final Collection<Long> tids;
            switch (commandFind.mm) {
                case NAME:
                    final Set<String> names = tm.matchingNames(qm);
                    qids = qm.idsFromNames(names);
                    tids = tm.idsFromNames(names);
                    break;
                case SIZE:
                    final Set<Long> sizes = tm.matchingSizes(qm);
                    qids = qm.idsFromSizes(sizes);
                    tids = tm.idsFromSizes(sizes);
                    break;
                default:
                    throw new AssertionError();
            }

            final Optional<File> oqf = Util.nonExistenFileFromNullable(commandFind.oq);
            final Optional<File> otf = Util.nonExistenFileFromNullable(commandFind.ot);
            if (oqf.isPresent()) {
                FileUtils.writeLines(oqf.get(), "UTF-8", qm.pathesFromIds(qids));
            }
            if (otf.isPresent()) {
                FileUtils.writeLines(otf.get(), "UTF-8", tm.pathesFromIds(tids));
            }

            System.err.println("All done.");
        } else if ("calcsum".equals(jc.getParsedCommand())) {

            System.err.println("Read files list from " + commandCalcSum.i);
            final List<String> files = FileUtils.readLines(new File(commandCalcSum.i), Charsets.UTF_8);
            System.err.println("   Read " + files.size() + " file names");

            final MessageDigest md = MessageDigest.getInstance(commandCalcSum.m.getConst());

            try (PrintStream ps = Util.ps(commandCalcSum.o)) {
                int n = 0;
                long l = 0;
                long t = System.currentTimeMillis();
                final long tt0 = t;
                for(String f : files) {
                    final byte[] plain = FileUtils.readFileToByteArray(new File(f));
                    final byte[] digest = md.digest(plain);
                    final String digestHex = Hex.encodeHexString(digest);
                    ps.println(digestHex + " *" + f);

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

        } else {
            throw new IllegalArgumentException("No command specified");
        }

    }
}