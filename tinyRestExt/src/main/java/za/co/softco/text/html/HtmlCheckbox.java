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

import za.co.softco.text.BooleanParser;


/**
 * @author john
 *
 */
public class HtmlCheckbox extends HtmlInput {

    public HtmlCheckbox(HtmlElement parent, String name) {
        super(parent, "checkbox", name, null);
    }

    @Override
    public void addAttribute(String name, Object value) {
        if (name.equalsIgnoreCase("value"))
            name = "checked";
        super.addAttribute(name, value);
    }

    @Override
    public void setValue(Object value) {
        boolean boolval = BooleanParser.toBoolean(value);
        String htmlval = (boolval ? "yes" : "no");
        addAttribute("value", htmlval);
        HtmlDocument doc = getDocument();
        if (doc != null)
            doc.addOnLoadJavaScriptLine(getQualifiedName() + ".checked = " + boolval + ";");
    }

    @Override
    public Object getValue() {
        return getAttribute("selected");
    }
}
