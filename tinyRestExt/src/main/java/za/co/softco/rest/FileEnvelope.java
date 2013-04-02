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
package za.co.softco.rest;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import za.co.softco.rest.envelope.ZipWriter;
import za.co.softco.rest.envelope.ZipReader;
import za.co.softco.rest.http.HttpConstants;
import za.co.softco.rest.model.EnvelopeWriter;
import za.co.softco.rest.model.EnvelopeReader;
import za.co.softco.util.Utils;

/**
 * File envelopes
 * @author john
 */
public enum FileEnvelope {
    ZIP("zip", "application/zip") {
        @Override
        public EnvelopeReader getReader(File envelope, File targetFolder) throws IOException {
            return new ZipReader(envelope, targetFolder);
        }
        @Override
        public EnvelopeWriter getWriter(File envelope, String checksumAlgoritm) throws IOException, NoSuchAlgorithmException {
            return new ZipWriter(envelope, checksumAlgoritm);
        }
    },
    TAR("tar", "application/x-tar") {
        @Override
        public EnvelopeReader getReader(File envelope, File targetFolder) throws IOException {
            throw new IllegalStateException("Not implemented yet");
        }
        @Override
        public EnvelopeWriter getWriter(File envelope, String checksumAlgoritm) throws IOException {
            throw new IllegalStateException("Not implemented yet");
        }
    };
    
    private final String extension;
    private final String contentType;
    
    private FileEnvelope(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }
    
    /**
     * Return file extension
     * @return
     */
    public String getExtension() {
        return extension;
    }
    
    /**
     * Return content type
     * @return
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Get an envelope reader
     * @param envelope
     * @param targetFolder
     * @return
     * @throws IOException
     */
    public abstract EnvelopeReader getReader(File envelope, File targetFolder) throws IOException;

    /**
     * Get an envelope writer
     * @param envelope
     * @param checksumAlgoritm
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public abstract EnvelopeWriter getWriter(File envelope, String checksumAlgoritm) throws IOException, NoSuchAlgorithmException;
    
    /**
     * Parse a string as an FileEnvelopeEntry
     * @param value
     * @return
     * @throws RestException
     */
    public static FileEnvelope parse(String value) throws RestException {
        value = Utils.normalize(value);
        if (value == null)
            return ZIP;
        value = value.toUpperCase();
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new RestException(HttpConstants.HTTP_BAD_REQUEST, "Envelope not supported: " + value);
        }
    }
}
