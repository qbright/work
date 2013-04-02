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

import za.co.softco.text.DataParser;
import za.co.softco.util.Utils;

/**
 * @author john
 *
 */
public class HtmlInput extends BasicHtmlElement<HtmlElement> {

    public HtmlInput(HtmlElement parent, String type, String name, String caption) {
        super(parent, "input");
        addAttribute("type", type);
        addAttribute("name", name);
        addAttribute("value", caption);
    }
 
    public String getInputType() {
        String result = Utils.normalize(DataParser.format(getAttribute("type")));
        return (result != null ? result : "text");
    }
    
    public void setValue(Object value) {
        addAttribute("value", value);
        HtmlDocument doc = getDocument();
        if (doc != null)
            doc.addOnLoadJavaScriptLine(getQualifiedName() + ".value = " + HtmlUtils.escapeJavaScriptValue(value) + ";");
    }

    public Object getValue() {
        return getAttribute("value");
    }
}
