/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 04 Jun 2010
 *******************************************************************************/
package za.co.softco.text.html;


/**
 * @author john
 *
 */
public class HtmlPanel extends HtmlContainer<HtmlElement> {

    
    protected HtmlPanel(HtmlElement parent) {
        super(parent);
    }

    protected HtmlPanel(HtmlElement parent, String elementType) {
        super(parent, elementType);
    }

    @Override
    public <T extends HtmlElement> T addElement(T element) {
        super.addElement(element);
        return element;
    }
    
}
