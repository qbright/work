/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 05 Jun 2010
 *******************************************************************************/
package za.co.softco.text.html;

/**
 * @author john
 *
 */
public class HtmlRow extends BasicHtmlElement<HtmlTable> {

    protected HtmlRow(HtmlTable parent) {
        super(parent, "tr");
        if (!(parent instanceof HtmlTable))
            throw new IllegalArgumentException("Parent must be instance of " + HtmlTable.class.getName());
    }

    public HtmlCell addCell() {
        return addElement(new HtmlCell(this, "td"));
    }

    public HtmlCell addHeaderCell() {
        return addElement(new HtmlCell(this, "th"));
    }

    public HtmlCell addCell(String contents) {
        HtmlCell result = addElement(new HtmlCell(this, "td"));
        result.addText(contents);
        return result;
    }

    public HtmlCell addHeaderCell(String contents) {
        HtmlCell result = addElement(new HtmlCell(this, "th"));
        result.addText(contents);
        return result;
    }
    
    @Override
    public HtmlTable close() {
        return (HtmlTable) super.close();
    }
}
