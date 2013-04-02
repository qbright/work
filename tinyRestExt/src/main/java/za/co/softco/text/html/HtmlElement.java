/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 01 May 2010
 *******************************************************************************/
package za.co.softco.text.html;

/**
 * @author john
 *
 */
public interface HtmlElement {

    /**
     * Append element to a string builder 
     * @param result
     */
    public void appendTo(StringBuilder result);
    
    /**
     * Close the element and return the parent element
     * @return
     */
    public HtmlElement close();
    
    /**
     * Return the parent element
     * @return
     */
    public HtmlElement getParent();
    
    /**
     * Return the element type (for example "SELECT", "INPUT" etc). If the
     * element does not have a type, then return null.
     * @return
     */
    public String getType();
    
    /**
     * Return the element's "name" property or null if it does not have a valid
     * name in the "name" property
     * @return
     */
    public String getName();
}
