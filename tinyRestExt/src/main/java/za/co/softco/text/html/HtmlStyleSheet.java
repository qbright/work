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

import java.util.Collection;
import java.util.LinkedList;

import za.co.softco.util.Utils;

/**
 * @author john
 *
 */
public class HtmlStyleSheet extends BasicHtmlElement<HtmlElement> {
    
    private final String source;
    protected Collection<Style> styles = new LinkedList<Style>();
    
    HtmlStyleSheet(HtmlElement parent) {
        super(parent);
        this.source = null;
    }
    
    HtmlStyleSheet(HtmlElement parent, String source) {
        super(parent);
        this.source = source;
        close();
    }
    
    public Style addStyle(String name) {
        Style result = new Style(name);
        styles.add(result);
        return result;
    }

    /*
     * @see za.co.softco.text.html.BasicHtmlElement#appendTo(java.lang.StringBuilder)
     */
    @Override
    public void appendTo(StringBuilder result) {
        if (source != null) {
            result.append("<LINK REL='STYLESHEET' HREF='");
            result.append(source);
            result.append("' TYPE='text/css'/>\r\n");
        }
        if (styles.size() > 0) {
            result.append("<STYLE>\r\n");
            for (Style s : styles)
                s.appendTo(result);
            result.append("</STYLE>\r\n");
        }
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        appendTo(result);
        return result.toString();
    }
    
    public class Style implements HtmlElement {
        private final StringBuilder style = new StringBuilder();
        private boolean closed = false;
        
        protected Style(String name) {
            if (Utils.normalize(name) == null)
                throw new IllegalArgumentException("name is required");
            style.append(name);
            style.append(" {\r\n");
        }
        
        @Override
        public String getType() {
            return "style";
        }
        
        @Override
        public String getName() {
            return null;
        }
        
        public Style addProperty(String name, String value) {
            if (Utils.normalize(name) == null)
                throw new IllegalArgumentException("name is required");
            if (Utils.normalize(value) == null)
                throw new IllegalArgumentException("value is required");
            if (closed)
                throw new IllegalStateException("style already closed");
            style.append("  ");
            style.append(name);
            style.append(": ");
            style.append(value);
            style.append(";\r\n");
            return this;
        }
        
        @Override
        public HtmlStyleSheet close() {
            if (closed)
                throw new IllegalStateException("style already closed");
            style.append("}\r\n");
            closed = true;
            return HtmlStyleSheet.this;
        }

        @Override
        public void appendTo(StringBuilder result) {
            result.append(style);
            if (!closed)
                style.append("}\r\n");
        }
        
        @Override
        public HtmlElement getParent() {
            return HtmlStyleSheet.this;
        }
        
        @Override
        public String toString() {
            if (closed)
                return style.toString();
            return style + "}\r\n";
        }
    }
}
