/*******************************************************************************
 *              Copyright (C) Bester Consulting 2010. All Rights reserved.
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 17 Feb 2012
 *******************************************************************************/
package za.co.softco.rest.envelope;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import za.co.softco.rest.model.EnvelopeWriter;
import za.co.softco.util.Utils;

/**
 * Writer that can add files to a ZIP container 
 * @author john
 */
public class ZipWriter implements EnvelopeWriter {

    private final File envelope;
    private ZipOutputStream out;
    private MessageDigest digest;
    private long size;
    
    public ZipWriter(File envelope, String checksumAlgoritm) throws FileNotFoundException, NoSuchAlgorithmException {
        this.envelope = envelope;
        this.digest = (checksumAlgoritm != null ? MessageDigest.getInstance(checksumAlgoritm) : null);
        if (this.digest != null)
            out = new ZipOutputStream(new DigestOutputStream(new FileOutputStream(envelope), this.digest));
        else
            out = new ZipOutputStream(new FileOutputStream(envelope));
    }
    
    /*
     * @see za.co.softco.rest.model.EnvelopeBuilder#addFile(java.lang.String, java.io.File)
     */
    @Override
    public void addFile(String path, File file) throws IOException {
        if (out == null)
            throw new IOException("Envelope file is closed");
        
        if (path == null)
            path = file.getAbsolutePath();
        
        ZipEntry entry = new ZipEntry(path);
        out.putNextEntry(entry);
        InputStream in = new FileInputStream(file);
        try {
            Utils.copy(in, out);
        } finally {
            in.close();
        }
        out.closeEntry();
        this.size += entry.getCompressedSize();
    }

    /*
     * @see za.co.softco.rest.model.EnvelopeWriter#getSize()
     */
    @Override
    public long getSize() {
        return size;
    }
    
    /*
     * @see za.co.softco.rest.model.EnvelopeBuilder#close()
     */
    @Override
    public void close() throws IOException {
        if (out != null) {
            try {
                out.close();
            } finally {
                out = null;
            }
        }
    }

    /*
     * @see za.co.softco.rest.model.EnvelopeWriter#getChecksum()
     */
    @Override
    public String getChecksum() {
        return (digest != null ? Utils.toHexString(digest.digest()) : null);
    }
    
    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return envelope.getAbsolutePath();
    }
    
}
