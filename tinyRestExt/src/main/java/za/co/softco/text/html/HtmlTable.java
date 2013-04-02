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

import za.co.softco.util.Utils;

/**
 * @author john
 *
 */
public class HtmlTable extends BasicHtmlElement<HtmlElement> {

    public HtmlTable(HtmlElement parent, String width, int cellSpacing, int borderWidth, String... colWidths) {
        super(parent, "table");
        addAttribute("width", Utils.normalize(width));
        addAttribute("cellspacing", Integer.valueOf(cellSpacing));
        addAttribute("borderwidth", Integer.valueOf(borderWidth));
        if (colWidths == null)
            return;
        for (String cw : colWidths)
            addElement(new BasicHtmlElement<HtmlElement>(this, "col")).addAttribute("width", Utils.normalize(cw));
    }

    public HtmlRow addRow() {
        return addElement(new HtmlRow(this));
    }

}
