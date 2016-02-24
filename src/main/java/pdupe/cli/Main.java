

package pdupe.cli;

import com.beust.jcommander.JCommander;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
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

    public static void main(String [] args) throws IOException, ClassNotFoundException {
        final MainParameters p = new MainParameters();
        final CommandAddParametes commandAdd = new CommandAddParametes();
        final FindDupeCandidatesParameters commandFind = new FindDupeCandidatesParameters();
        final JCommander jc = new JCommander(p);
        jc.addCommand("add", commandAdd);
        jc.addCommand("find", commandFind);
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
        } else {
            throw new IllegalArgumentException("No command specified");
        }

    }
}