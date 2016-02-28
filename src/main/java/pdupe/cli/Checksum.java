/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdupe.cli;

/**
 *
 * @author imreg
 */
public enum Checksum {
    MD5("MD5"), SHA1("SHA-1"), SHA512("SHA-512");

    final String algorithm;

    private Checksum(String c) {
        this.algorithm = c;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

}
