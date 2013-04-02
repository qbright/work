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
public class HtmlCell extends HtmlContainer<HtmlRow> {

    protected HtmlCell(HtmlRow parent) {
        super(parent, "tr");
        if (!(parent instanceof HtmlRow))
            throw new IllegalArgumentException("Parent must be instance of " + HtmlRow.class.getName());
    }

    protected HtmlCell(HtmlRow parent, String type) {
        super(parent, type);
        if (!(parent instanceof HtmlRow))
            throw new IllegalArgumentException("Parent must be instance of " + HtmlRow.class.getName());
        if (!type.equalsIgnoreCase("th") && !type.equalsIgnoreCase("td"))
            throw new IllegalArgumentException("Type must be TH or TD");
    }

    public HtmlCell colSpan(int colSpan) {
        addAttribute("colspan", Integer.valueOf(colSpan));
        return this;
    }
    
    public HtmlCell rowSpan(int rowSpan) {
        addAttribute("colspan", Integer.valueOf(rowSpan));
        return this;
    }
    
    @Override
    public HtmlRow close() {
        return (HtmlRow) super.close();
    }
}
