/*******************************************************************************
 * Copyright (C) Bester Consulting 2010. All Rights reserved.
 * This file may be distributed under the Softco / L-Mobile Share License
 * 
 * @author      John Bester
 * Project:     MobiSync
 * Description: Mobile Replication Server
 *
 * Changelog  
 *  $Log$
 *  Created on 03 Mar 2012
 *******************************************************************************/
package za.co.softco.rest;

import static za.co.softco.rest.http.HttpConstants.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import za.co.softco.rest.http.Compression;
import za.co.softco.rest.model.CompressionLevel;
import za.co.softco.rest.model.ResponseEvent;
import za.co.softco.rest.model.ResponseListener;
import za.co.softco.util.Utils;

/**
 * Output stream that allows service methods to write an output response that
 * can be packaged and sent back as an HTTP response. It does checksum calculation
 * as well as compression on the content.
 * @author john
 */
public class ResponseOutputStream extends FilterOutputStream {

    public static final String DEFAULT_CHECKSUM_ALGORITM = "MD5";
    
    private final List<ResponseListener> listeners = new LinkedList<ResponseListener>();
    private final MessageDigest checksumDigest;
    private final RestRequest request;
    private final File tempFile;
    private final String mimeType;
    private final String filename;
    private final Compression compression;
    
    /**
     * Constructor
     * @param request
     * @param checksumDigest
     * @param tempFile
     * @param mimeType
     * @param filename
     * @param compression
     * @param compressLevel
     * @throws IOException
     */
    private ResponseOutputStream(RestRequest request, MessageDigest checksumDigest, File tempFile, String mimeType, String filename, Compression compression, CompressionLevel compressLevel) throws IOException {
        super(compress(new DigestOutputStream(new FileOutputStream(tempFile), checksumDigest), compression, compressLevel));
        this.request = request;
        this.checksumDigest = checksumDigest;
        this.tempFile = tempFile;
        this.mimeType = getMimeType(mimeType, compression);
        this.filename = getFilename(filename, compression);
        this.compression = (compression != null ? compression : Compression.NONE);
    }

    /**
     * Constructor
     * @param request
     * @param checksumDigest
     * @param mimeType
     * @param downloadFilename
     * @param compression
     * @param compressLevel
     * @throws IOException
     */
    public ResponseOutputStream(RestRequest request, MessageDigest checksumDigest, String mimeType, String downloadFilename, Compression compression, CompressionLevel compressLevel) throws IOException {
        this(request, checksumDigest, File.createTempFile("mobisync", ".out"), mimeType, downloadFilename, compression, compressLevel);
    }

    /**
     * Constructor
     * @param request
     * @param checksumAlgorithm
     * @param mimeType
     * @param downloadFilename
     * @param compression
     * @param compressLevel
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public ResponseOutputStream(RestRequest request, String checksumAlgorithm, String mimeType, String downloadFilename, Compression compression, CompressionLevel compressLevel) throws NoSuchAlgorithmException, IOException {
        this(request, getMessageDigest(checksumAlgorithm), mimeType, downloadFilename, compression, compressLevel);
    }

    /**
     * Constructor
     * @param request
     * @param checksumDigest
     * @param mimeType
     * @param downloadFilename
     * @throws IOException
     */
    public ResponseOutputStream(RestRequest request, MessageDigest checksumDigest, String mimeType, String downloadFilename) throws IOException {
        this(request, checksumDigest, File.createTempFile("mobisync", ".out"), mimeType, downloadFilename, Compression.NONE, CompressionLevel.NONE);
    }

    /**
     * Constructor
     * @param request
     * @param checksumAlgorithm
     * @param mimeType
     * @param downloadFilename
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public ResponseOutputStream(RestRequest request, String checksumAlgorithm, String mimeType, String downloadFilename) throws NoSuchAlgorithmException, IOException {
        this(request, getMessageDigest(checksumAlgorithm), mimeType, downloadFilename);
    }

    /**
     * Compress content
     * @param out
     * @param compression
     * @param compressLevel
     * @return
     */
    private static OutputStream compress(OutputStream out, Compression compression, CompressionLevel compressLevel) {
        if (compression == null)
            return out;
        if (compressLevel == null)
            compressLevel = CompressionLevel.NORMAL;
        return compression.compress(out, compressLevel);
    }
    
    /**
     * Build a filename by using compression algorithm to set the proper extension
     * @param filename
     * @param compression
     * @return
     */
    private static String getFilename(String filename, Compression compression) {
        if (compression == null)
            return filename;
        return compression.addFileExtenstion(filename);
    }
    
    /**
     * Return the mime type for a specific mime type combined with a compression algorithm
     * @param mimeType
     * @param compression
     * @return
     */
    private static String getMimeType(String mimeType, Compression compression) {
        if (compression == null)
            return mimeType;
        return compression.changeMimeType(mimeType);
    }
    
    /**
     * Return the content checksum
     * @param algorithm
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static MessageDigest getMessageDigest(String algorithm) throws NoSuchAlgorithmException {
        algorithm = Utils.normalize(algorithm);
        if (algorithm == null)
            algorithm = DEFAULT_CHECKSUM_ALGORITM;
        return MessageDigest.getInstance(algorithm);
    }

    /**
     * Add a response listener
     * @param listener
     */
    public void addListener(ResponseListener listener) {
        if (listener != null && !this.listeners.contains(listener))
            listeners.add(listener);
    }
    
    /**
     * Return all listeners
     * @return
     */
    public ResponseListener[] getListeners() {
        return listeners.toArray(new ResponseListener[listeners.size()]);
    }
    
    /*
     * @see java.io.FilterOutputStream#close()
     */
    @Override
    public void close() throws IOException {
        super.close();
        long size = tempFile.length();
        String checksum = Utils.toHexString(checksumDigest.digest());
        String algorithm = checksumDigest.getAlgorithm();
        request.addResponseHeader(HEADER_CHECKSUM, checksum);
        request.addResponseHeader(HEADER_CHECKSUM_ALGORITHM, algorithm);
        if (compression != null && compression != Compression.NONE)
            request.addResponseHeader(HEADER_COMPRESSION, compression.toString().toLowerCase());
        
        ResponseEvent event = new ResponseEvent(this, request, size, checksum, algorithm, compression);
        for (ResponseListener l : listeners)
            l.onResponseReady(event);
        
        InputStream in = new TempFileInputStream(tempFile, event);
        try {
            request.writeBinaryReply(filename, mimeType, in, size);
        } catch (Exception e) {
            throw Utils.cast(e, IOException.class);
        } finally {
            in.close();
        }
    }
    
    /**
     * Stream used to convert collector from an output stream to an input stream
     * @author john
     */ 
    private class TempFileInputStream extends FilterInputStream {
        private final File tempFile;
        private final ResponseEvent completeEvent;

        public TempFileInputStream(File tempFile, ResponseEvent completeEvent) throws FileNotFoundException {
            super(new FileInputStream(tempFile));
            this.tempFile = tempFile;
            this.completeEvent = completeEvent;
        }

        /*
         * @see java.io.FilterInputStream#close()
         */
        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                tempFile.delete();
                for (ResponseListener l : getListeners())
                    l.onResponseReady(completeEvent);
            }
        }
    }
}
