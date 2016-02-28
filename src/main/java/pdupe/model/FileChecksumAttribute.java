package pdupe.model;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Gabor Imre
 */
public class FileChecksumAttribute extends AbstractAttribute<String> {

    private final transient MessageDigest digest;

    private final String algorithm;

    public FileChecksumAttribute(String alg) {
        this.algorithm = alg;
        try {
            this.digest = MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private Object readResolve() {
        return new FileChecksumAttribute(this.algorithm);
    }


    @Override
    public String extractValue(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
