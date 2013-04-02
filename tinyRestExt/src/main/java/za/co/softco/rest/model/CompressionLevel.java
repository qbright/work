/*******************************************************************************
 * Copyright (C) Bester Consulting 2010. All Rights reserved.
 * This file may be distributed under the Softco Share License
 * 
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 04 Jun 2010
 *******************************************************************************/
package za.co.softco.rest.model;

/**
 * Compression levels. Each compression algorithm may or may not
 * implement these levels.
 * @author john
 */
public enum CompressionLevel {
    NONE(false), 
    FASTEST(true),
    NORMAL(true),
    BEST(true);
    
    private final boolean compressed;
    
    private CompressionLevel(boolean compressed) {
        this.compressed = compressed;
    }
    
    public boolean compressed() {
        return compressed;
    }
}
