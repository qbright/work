/*******************************************************************************
 *              Copyright (C) Bester Consulting 2010. All Rights reserved.
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 16 Oct 2010
 *******************************************************************************/
package za.co.softco.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import za.co.softco.util.Utils;

/**
 * @author john
 * Statistics (such as checksum and content length) used to determine whether an upload is correct
 */
public class PacketStats {

    public static final PacketStats EMPTY_PACKET_STATS;
    
    static {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
        } catch (NoSuchAlgorithmException e) {
            // Ignore exception
        }
        EMPTY_PACKET_STATS = new PacketStats(0, digest);
    }
    
    public final long length;
    public final String algorithm;
    public final String checksum;
    
    /**
     * Constructor
     * @param length
     */
    public PacketStats(long length) {
        this.length = length;
        this.algorithm = null;
        this.checksum = null;
    }

    /**
     * Constructor
     * @param length
     * @param algorithm
     * @param checksum
     */
    public PacketStats(long length, String algorithm, String checksum) {
        this.length = length;
        this.algorithm = (algorithm != null ? algorithm.trim() : null);
        this.checksum = (checksum != null ? checksum.trim() : null);
    }

    /**
     * Constructor
     * @param length
     * @param digest
     */
    public PacketStats(long length, MessageDigest digest) {
        this.length = length;
        this.algorithm = (digest != null ? digest.getAlgorithm() : null);
        this.checksum = (digest != null ? Utils.toHexString(digest.digest()) : null);
    }

    /**
     * Constructor
     * @param file
     * @param algorithm
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public PacketStats(File file, String algorithm) throws NoSuchAlgorithmException, IOException {
        this.length = (file != null ? file.length() : 0);
        this.algorithm = (Utils.normalize(algorithm) != null ? algorithm.trim() : null);
        this.checksum = calculateChecksum(file, this.algorithm);
    }

    /**
     * Calculate a checksum
     * @param file
     * @param algorithm
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String calculateChecksum(File file, String algorithm) throws NoSuchAlgorithmException, IOException {
        if (file == null)
            return null;
        if (Utils.normalize(algorithm) == null)
            return null;
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
        DigestInputStream in = new DigestInputStream(new FileInputStream(file), digest);
        try {
            byte[] buf = new byte[10240];
            while (in.read(buf) > 0) {
                // Do nothing
            }
        } finally {
            in.close();
        }
        return Utils.toHexString(digest.digest());
    }
    
    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (((int) length + (checksum != null ? checksum.hashCode() : 0)) % 10000);
    }
    
    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object ref) {
        if (!(ref instanceof PacketStats))
            return false;
        PacketStats ps = (PacketStats) ref;
        if (ps.length != length)
            return false;
        if (algorithm == null || checksum == null || ps.algorithm == null || ps.checksum == null)
            return true;
        if (!ps.algorithm.equalsIgnoreCase(algorithm))
            return false;
        return ps.checksum.equalsIgnoreCase(checksum);
    }
    
    /**
     * Parse a checksum and return a PacketStats instance for it 
     * @param checksum
     * @param algorithm
     * @return
     */
    public static PacketStats parseChecksum(String checksum, String algorithm) {
        checksum = Utils.normalize(checksum);
        algorithm = Utils.normalize(algorithm);
        if (checksum == null)
            return new PacketStats(0, null);
        String[] tmp = checksum.replace(':','/').split("/");
        switch (tmp.length) {
        case 0 :
            return new PacketStats(0, algorithm, null);
        case 1 :
            return new PacketStats(0, algorithm, Utils.normalize(tmp[0]));
        default :
            if (algorithm == null)
                algorithm = Utils.normalize(tmp[0]);
            return new PacketStats(0, algorithm, Utils.normalize(tmp[1]));
        }
    }
}
