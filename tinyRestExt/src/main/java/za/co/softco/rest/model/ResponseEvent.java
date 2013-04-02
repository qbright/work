/*******************************************************************************
 *              Copyright (C) Bester Consulting 2010. All Rights reserved.
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 03 Mar 2012
 *******************************************************************************/
package za.co.softco.rest.model;

import za.co.softco.rest.http.Compression;

/**
 * Event class used with ResponseListener
 * @author john
 */
public class ResponseEvent {

    private final Object source;
    private final Context request;
    private final long size;
    private final String checksum;
    private final String algorithm;
    private final Compression compression;

    
    public ResponseEvent(Object source, Context request, long size, String checksum, String algorithm, Compression compression) {
        this.source = source;
        this.request = request;
        this.size = size;
        this.checksum = checksum;
        this.algorithm = algorithm;
        this.compression = compression;
    }

    /**
     * @return the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * @return the request
     */
    public Context getRequest() {
        return request;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @return the checksum
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * @return the algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * @return the compression
     */
    public Compression getCompression() {
        return compression;
    }

}
