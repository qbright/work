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
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import za.co.softco.rest.model.EnvelopeEntry;
import za.co.softco.rest.model.EnvelopeReader;
import za.co.softco.util.Utils;

/**
 * Reader that can extract files from a ZIP container
 * @author john
 */
public class ZipReader implements EnvelopeReader {

    private final File envelope;
    private final File targetFolder;
    private ZipInputStream in;
    
    public ZipReader(File envelope, File targetFolder) throws FileNotFoundException {
        this.envelope = envelope;
        this.targetFolder = (targetFolder != null ? targetFolder : new File("."));
        this.in = new ZipInputStream(new FileInputStream(envelope));
    }
    
    /*
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<String> iterator() {
        try {
            return new ZipNameIterator(envelope);
        } catch (IOException e) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Iterator<String> result = (Iterator) Collections.emptyList().iterator();
            return result;
        }
    }

    /*
     * @see za.co.softco.rest.model.EnvelopeReader#extractNextEntry()
     */
    @Override
    public EnvelopeEntry extractNextEntry() throws IOException {
        if (in == null)
            throw new IOException("Envelope file is closed");
        ZipEntry entry = in.getNextEntry();
        File target = new File(targetFolder, entry.getName());
        File parent = target.getParentFile();
        if (!parent.exists())
            parent.mkdirs();
        FileOutputStream out = new FileOutputStream(target);
        try {
            Utils.copy(in, out);
        } finally {
            out.close();
        }
        return new EnvelopeEntry(entry.getName(), target);
    }

    /*
     * @see za.co.softco.rest.model.EnvelopeReader#close()
     */
    @Override
    public void close() throws IOException {
        if (in != null) {
            try {
                in.close();
            } finally {
                in = null;
            }
        }
    }
    
    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return envelope.toString();
    }

    /**
     * Class that iterates the names of entries in a Zip file
     * @author john
     */
    private static class ZipNameIterator implements Iterator<String> {
        private final ZipInputStream in;
        private ZipEntry entry;

        public ZipNameIterator(File envelope) throws IOException {
            this.in = new ZipInputStream(new FileInputStream(envelope));
            this.entry = in.getNextEntry();
        }

        /*
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return (entry != null);
        }

        /*
         * @see java.util.Iterator#next()
         */
        @Override
        public String next() {
            if (entry == null)
                return null;
            String result = entry.getName();
            try {
                entry = in.getNextEntry();
                return result;
            } catch (IOException e) {
                Logger.getLogger(ZipReader.class).error(e);
                return null;
            }
        }

        /*
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new IllegalStateException("Function not available");
        }
    }
}
