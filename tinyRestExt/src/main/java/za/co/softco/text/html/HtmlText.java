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
public class HtmlText extends BasicHtmlElement<HtmlElement> {

    HtmlText(HtmlElement parent, String text) {
        super(parent);
        append(text);
    }
    
}
