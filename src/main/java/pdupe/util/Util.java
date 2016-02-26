package pdupe.util;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Misc utilities.
 *
 * @author Gabor Imre
 */
public final class Util {

    /**
     * No constructor exposed.
     */
    private Util() {};

    /**
     * Format using SI suffix.
     *
     * @param l Number
     * @return formatted number
     */
    public static String formatSi(long l) {
        if (l < 1000) {
            return Long.toString(l);
        } else if (l < 1e6) {
            return (l / 100l) / 10.0 + " k";
        } else if (l < 1e9) {
            return (l / 100000l) / 10.0 + " M";
        } else if (l < 1e12) {
            return (l / 100000000l) / 10.0 + " G";
        }  {
            return (l / 100000000000l) / 10.0 + " T";
        }
    }

    /**
     * Create veto matcher.
     *
     * Matcher defaults to {@link Predicates#alwaysFalse()} when no or empty list passed.
     *
     * @param l List of patterns or {@code null}
     * @param v Verbose matches
     * @return Matcher predicate of {@link Predicates#alwaysFalse()} when no or empty list passed
     */
    public static Predicate<String> vetoFromPatterns(List<String> l, final boolean v) {
        if (l == null || l.isEmpty()) {
            return Predicates.alwaysFalse();
        }
        final List<Pattern> patterns = new ArrayList<>(l.size());
        for (String s : l) {
            final Pattern p = Pattern.compile(s);
            patterns.add(p);
        }
        return new Predicate<String>() {

            @Override
            public boolean apply(String input) {
                for (Pattern p : patterns) {
                    if (p.matcher(input).matches()) {
                        if (v) {
                            System.err.println(p.pattern() + " matches to " + input);
                        }
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static ObjectOutputStream oos(String location) {
        try {
            final OutputStream fos = new FileOutputStream(location);
            final OutputStream bos = new BufferedOutputStream(fos);
            final ObjectOutputStream oos = new ObjectOutputStream(bos);
            return oos;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ObjectInputStream ois(String location) {
        try {
            final InputStream fis = new FileInputStream(location);
            final InputStream bis = new BufferedInputStream(fis);
            final ObjectInputStream ois = new ObjectInputStream(bis);
            return ois;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static PrintStream ps(String location) {
        try {
            final OutputStream fos = new FileOutputStream(location);
            final OutputStream bos = new BufferedOutputStream(fos);
            final PrintStream ps = new PrintStream(bos, true, "UTF-8");
            return ps;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static Optional<File> nonExistenFileFromNullable(String location) {
        if (location == null) {
            return Optional.<File>absent();
        }
        final File ret = new File(location);
        if (ret.exists()) {
            throw new IllegalArgumentException("Already exists " + ret);
        }
        return Optional.of(ret);
    }

}
