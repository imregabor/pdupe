

package pdupe.cli;

import com.beust.jcommander.JCommander;
import pdupe.model.Model;

/**
 * CLI entry point.
 *
 * @author Gabor Imre
 */
public class Main {
    public static void main(String [] args) {
        final MainParameters p = new MainParameters();
        final CommandAddParametes commandAdd = new CommandAddParametes();
        final JCommander jc = new JCommander(p);
        jc.addCommand("add", commandAdd);
        jc.parse(args);

        if (p.help) {
            jc.setProgramName(Main.class.getName());
            jc.usage();
            return;
        }

        // new model or deserialize
        final Model m = new Model();

        if ("add".equals(jc.getParsedCommand())) {
            System.err.println("Add files/dirs");
            for (String l : commandAdd.locations) {
                System.err.println("  Add " + l);
            }

        } else {
            throw new IllegalArgumentException("No command specified");
        }

    }
}