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

/**
 * A listener that allows a class to add header information or save statistics
 * when a response is completely built or when it has been sent successfully.
 * @author john
 */
public interface ResponseListener {

    /**
     * The response is ready (checksums have been calculated)
     * @param event
     */
    public void onResponseReady(ResponseEvent event);
    
    /**
     * Response have been successfully written to the HTTP client 
     * @param event
     */
    public void onResponseSent(ResponseEvent event);
}
