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
package za.co.softco.rest.model;

import java.io.File;

/**
 * Encapsulation of an entry in an envelope (typically a ZipFileEntry
 * in a ZipFile)
 * @author john
 */
public class EnvelopeEntry {

    private final String name;
    private final File file;
    
    /**
     * Constructor
     * @param name
     * @param file
     */
    public EnvelopeEntry(String name, File file) {
        this.name = name;
        this.file = file;
    }

    /**
     * Return name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Return file
     * @return
     */
    public File getFile() {
        return file;
    }
    
    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EnvelopeEntry)
            return name.equals(((EnvelopeEntry) obj).name);
        return false;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
