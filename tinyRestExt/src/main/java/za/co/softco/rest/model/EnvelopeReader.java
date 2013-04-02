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

import java.io.IOException;

/**
 * Implement this interface to extract files from an envelope 
 * @author john
 */
public interface EnvelopeReader extends Iterable<String> {

    /**
     * Implement this method to extract the next entry
     * @return
     * @throws IOException
     */
    public EnvelopeEntry extractNextEntry() throws IOException;
    
    /**
     * Close the envelope file
     * @throws IOException
     */
    public void close() throws IOException;

}
