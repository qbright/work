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
import java.io.IOException;

/**
 * Implement this interface to add files to an envelope 
 * @author john
 */
public interface EnvelopeWriter {

    /**
     * Add a new file
     * @param relativePath
     * @param file
     * @throws IOException
     */
    public void addFile(String path, File file) throws IOException;
 
    /**
     * Return the size of the envelope file
     * @return
     */
    public long getSize();
    
    /**
     * Close the envelope file
     * @throws IOException
     */
    public void close() throws IOException;
    
    /**
     * Return the checksum of the envelope file
     * @return
     */
    public String getChecksum();
}
