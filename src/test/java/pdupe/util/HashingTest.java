package pdupe.util;

import com.google.common.base.Charsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import pdupe.cli.Checksum;

/**
 * @author Gabor Imre
 */
public class HashingTest {

    @Test
    public void doMd5() throws NoSuchAlgorithmException {
        // data from https://en.wikipedia.org/wiki/MD5
        final MessageDigest md = MessageDigest.getInstance("MD5");
        final byte[] plain = "The quick brown fox jumps over the lazy dog".getBytes(Charsets.UTF_8);
        final byte[] digest = md.digest(plain);
        final String digestHex = Hex.encodeHexString(digest);
        assertThat(digestHex, is("9e107d9d372bb6826bd81d3542a419d6"));
    }

    @Test
    public void doSha1() throws NoSuchAlgorithmException {
        // data from https://en.wikipedia.org/wiki/SHA-1
        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        final byte[] plain = "The quick brown fox jumps over the lazy dog".getBytes(Charsets.UTF_8);
        final byte[] digest = md.digest(plain);
        final String digestHex = Hex.encodeHexString(digest);
        assertThat(digestHex, is("2fd4e1c67a2d28fced849ee1bb76e7391b93eb12"));
    }

    @Test
    public void doSha512() throws NoSuchAlgorithmException {
        // echo -n "Hello World" | sha512sum.exe
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        final byte[] plain = "Hello World".getBytes(Charsets.UTF_8);
        final byte[] digest = md.digest(plain);
        final String digestHex = Hex.encodeHexString(digest);
        assertThat(digestHex, is("2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_surplus_space_found_1() {
        final String line = " SHA1 728519933d0731e980043c61fd7ad *plainfile.txt";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_surplus_space_found_2() {
        final String line = "SHA1 728519933d0731e980043c61fd7ad ";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_surplus_space_found_3() {
        final String line = " SHA1 728519933d0731e980043c61fd7ad *";
        new ChecksumEntry(line);
    }


    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_no_space_star_separator_found_1() {
        final String line = "SHA1 728519933d0731e980043c61fd7ad plainfile.txt";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_no_space_star_separator_found_2() {
        final String line = "SHA1 728519933d0731e980043c61fd7ad  plainfile.txt";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_no_space_star_separator_found_3() {
        final String line = "SHA1 728519933d0731e980043c61fd7ad\t*plainfile.txt";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_no_space_star_separator_found_4() {
        final String line = "SHA1 728519933d0731e980043c61fd7ad*plainfile.txt";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_no_space_star_separator_found_5() {
        final String line = "SHA1 728519933d0731e980043c61fd7ad* plainfile.txt";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_no_space_star_separator_found_6() {
        final String line = "SHA1 728519933d0731e980043c61fd7ad  * plainfile.txt";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_no_method_found() {
        final String line = "728519933d0731e980043c61ff276f4ed36174bfd7ad *file.txt";
        new ChecksumEntry(line);
    }


    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_only_method_found_1() {
        final String line = "SHA1";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_only_method_found_2() {
        final String line = "SHA1 ";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_only_method_found_3() {
        final String line = "SHA1  *";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_only_method_found_4() {
        final String line = "SHA1  * ";
        new ChecksumEntry(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checksum_line_splitting_fails_when_only_method_found_5() {
        final String line = "SHA1  *  ";
        new ChecksumEntry(line);
    }



    @Test
    public void checksum_line_splitting() {
        final ChecksumEntry e1 = new ChecksumEntry("SHA1 728519933d0731e980043c61ff276f4ed36174bfd7ad *a:\\\\__b/c/d e * f  ***    gh\\n ");
        assertThat(e1.getMethod(), is(Checksum.SHA1));
        assertThat(e1.getChecksum(), is("728519933d0731e980043c61ff276f4ed36174bfd7ad"));
        assertThat(e1.getFile(), is("a:\\\\__b/c/d e * f  ***    gh\\n "));

        final ChecksumEntry e2 = new ChecksumEntry("MD5 7 *\\");
        assertThat(e2.getMethod(), is(Checksum.MD5));
        assertThat(e2.getChecksum(), is("7"));
        assertThat(e2.getFile(), is("\\"));
    }
}
