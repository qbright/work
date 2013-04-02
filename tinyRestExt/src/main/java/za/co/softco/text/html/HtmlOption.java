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

import java.util.Map;

/**
 * @author john
 *
 */
public class HtmlOption extends BasicHtmlElement<HtmlChoice> {

    protected HtmlOption(HtmlChoice parent, Object key, String text, boolean isDefault) {
        super(parent, "option");
        addAttribute("value", key);
        if (isDefault)
            addTag("selected");
        html.append(text);
    }

    @Override
    public void appendTo(StringBuilder result) {
        result.append('<');
        result.append(elementType);
        for (Map.Entry<String,Object> attrib : attributes.entrySet()) 
            HtmlUtils.appendAttributeTo(result, attrib.getKey(), attrib.getValue());
        if (html.length() > 0) {
            result.append(">");
            result.append(html);
            result.append("</");
            result.append(elementType);
            result.append(">\r\n");
        } else {
            result.append("/>\r\n");
        }
    }
}
