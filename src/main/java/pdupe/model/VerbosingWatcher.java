package pdupe.model;

import java.io.PrintStream;
import pdupe.util.Util;

/**
 * Watcher printing verbose info.
 *
 * @author Gabor Imre
 */
public final class VerbosingWatcher implements TraverseWatcher {
    private final PrintStream ps;
    private long fc;
    private long ts;
    private String le;
    private String lf;
    private final long dt;
    private long lastt;
    private long fvc;
    private long dvc;

    public VerbosingWatcher(PrintStream ps, long dt) {
        this.ps = ps;
        this.fc = 0;
        this.ts = 0;
        this.le = "";
        this.lf = "";
        this.dt = dt;
        this.lastt = 0;
        this.fvc = 0;
        this.dvc = 0;
    }

    @Override
    public void fileToBeAdded(String cp, long length) {
        this.fc++;
        this.ts+= length;
        this.lf = cp;
        ping();
    }

    @Override
    public void directoryEntered(String cp) {
        this.le = cp;
    }

    @Override
    public void directoryDone(String cp) {
        // do nothing
    }

    @Override
    public void fileVetoed(String cp, long length) {
        this.fvc++;
        ping();
    }

    @Override
    public void directoryVetoed(String cp) {
        this.dvc++;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.fc).append(" files added, total size: ").append(Util.formatSi(this.ts)).append("B,");
        if (this.fvc > 0) {
            b.append(" fvc: ").append(this.fvc);
        }
        if (this.dvc > 0) {
            b.append(" dvc: ").append(this.dvc);
        }
        b.append(" last added: ").append(this.lf);
        return b.toString();
    }

    private void ping() {
        final long t = System.currentTimeMillis();
        if (t - this.lastt >= this.dt) {
            this.lastt = t;
            System.err.println(toString());

        }
    }

}
