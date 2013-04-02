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
import java.util.Date;
import java.util.LinkedList;

import za.co.softco.util.Utils;

/**
 * @author john
 *
 */
public class HtmlDocument implements HtmlElement {

    private final Collection<HtmlStyleSheet> styleSheets = new LinkedList<HtmlStyleSheet>();
    private final Collection<HtmlElement> headerElements = new LinkedList<HtmlElement>();
    private final Collection<HtmlElement> bodyElements = new LinkedList<HtmlElement>();
    private final StringBuilder javaScript = new StringBuilder();
    private final StringBuilder onLoadScript = new StringBuilder();
    private final ElementStack elementStack = new ElementStack();
    private final String title;
    private final String iconURL;
    private String expiryTime;
    private boolean closed = false;
    
    public HtmlDocument(String title, String iconURL, boolean allowCache) {
        this.title = title;
        this.iconURL = Utils.normalize(iconURL);
        if (!allowCache) {
            BasicHtmlElement<HtmlElement> el = new BasicHtmlElement<HtmlElement>(this, "META");
            el.addAttribute("HTTP-EQUIV", "CACHE-CONTROL");
            el.addAttribute("CONTENT", "no-Cache");
            headerElements.add(el);
        }
    }
    
    public HtmlDocument(String title, String iconURL) {
        this(title, iconURL, false);
    }
    
    public HtmlDocument(String title, boolean allowCache) {
        this(title, "/favicon.ico", allowCache);
    }
    
    public HtmlDocument(String title) {
        this(title, "/favicon.ico", false);
    }
    
    public HtmlStyleSheet addStyleSheet() {
        HtmlStyleSheet result = new HtmlStyleSheet(this);
        styleSheets.add(result);
        return result;
    }
    
    @Override
    public String getName() {
        return "document";
    }
    
    @Override
    public String getType() {
        return "html";
    }
    
    public HtmlDocument addStyleSheet(String source) {
        HtmlStyleSheet result = new HtmlStyleSheet(this, source);
        styleSheets.add(result);
        return this;
    }

    public HtmlDocument setExpiryTime(Date time) {
        this.expiryTime = HtmlUtils.formatTimeForHttpHeader(time);
        BasicHtmlElement<HtmlElement> el = new BasicHtmlElement<HtmlElement>(this, "META");
        el.addAttribute("HTTP-EQUIV", "EXPIRES");
        el.addAttribute("CONTENT", expiryTime);
        headerElements.add(el);
        return this;
    }
    
    /**
     * Add a form to the HTML body
     * @param name
     * @return
     */
    public HtmlForm addForm(String name) {
        HtmlForm result = new HtmlForm(this, name);
        bodyElements.add(result);
        return result;
    }
    
    /**
     * Add a panel to which elements can be added
     * @return
     */
    public HtmlPanel addPanel() {
        HtmlPanel panel = new HtmlPanel(this);
        bodyElements.add(panel);
        return panel;
    }

    /**
     * Add a form to the HTML body
     * @param name
     * @param method
     * @param onLoad
     * @return
     */
    public HtmlForm addForm(String name, String method, String onLoad) {
        HtmlForm result = new HtmlForm(this, name, method, onLoad);
        bodyElements.add(result);
        return result;
    }

    public HtmlDocument addOnLoadJavaScriptLine(String javaScript) {
        if (Utils.normalize(javaScript) != null) {
            this.onLoadScript.append(javaScript);
            this.onLoadScript.append("\r\n");
        }
        return this;
    }
    
    public HtmlDocument addJavaScriptLine(String script) {
        if (Utils.normalize(script) != null) {
            this.javaScript.append(script);
            this.javaScript.append("\r\n");
        }
        return this;
    }
    
    public HtmlTable addTable(String width, int cellSpacing, int borderWidth, String... colWidths) {
        HtmlTable result = new HtmlTable(this, width, cellSpacing, borderWidth, colWidths);
        bodyElements.add(result);
        return result;
    }
    
    public HtmlText addText(String text) {
        HtmlText result = new HtmlText(this, text);
        bodyElements.add(result);
        return result;
    }
 
    public <T extends HtmlElement> T addElement(T element) {
        bodyElements.add(element);
        return element;
    }
    
    public HtmlDocument addLineBreak() {
        addText("<BR>");
        return this;
    }
    
    /*
     * @see za.co.softco.text.html.HtmlElement#getParent()
     */
    @Override
    public HtmlElement getParent() {
        return null;
    }

    /*
     * @see za.co.softco.text.html.HtmlElement#close()
     */
    @Override
    public HtmlDocument close() {
        if (closed)
            throw new IllegalStateException("Already closed");
        closed = true;
        return this;
    }
    
    /**
     * Append HTML to StringBuilder
     * @param result
     */
    @Override
    public void appendTo(StringBuilder result) {
        result.append("<HTML>\r\n");
        boolean needsHeader = (Utils.normalize(title) != null)
            || (styleSheets.size() > 0)
            || (headerElements.size() > 0)
            || (javaScript.length() > 0)
            || (onLoadScript.length() > 0);
        if (needsHeader)  {
            result.append("<HEAD>\r\n");
            if (iconURL != null) {
                result.append("<link rel=\"shortcut icon\" href=\"" + iconURL + "\" type=\"image/x-icon\"/>\r\n");
                result.append("<link rel=\"icon\" href=\"" + iconURL + "\" type=\"image/x-icon\"/>\r\n");
            }
            if (title != null) {
                result.append("<TITLE>");
                result.append(title);
                result.append("</TITLE>\r\n");
            }
            for (HtmlElement element : headerElements)
                element.appendTo(result);
            for (HtmlStyleSheet styleSheet : styleSheets)
                styleSheet.appendTo(result);
            if (javaScript.length() > 0 || onLoadScript.length() > 0) {
                StringBuilder script = new StringBuilder();
                script.append(javaScript); 
                if (onLoadScript.length() > 0) {
                    script.append("function doOnLoad() {\r\n");
                    script.append(onLoadScript);
                    script.append("}\r\n");
                }
                HtmlUtils.appendJavaScriptTo(result, script.toString());
            }
            result.append("</HEAD>\r\n");
        }
        result.append("<BODY>\r\n");
        for (HtmlElement el : bodyElements) 
            el.appendTo(result);
        String[] open = elementStack.toArray(new String[elementStack.size()]);
        for (int i=open.length-1; i>=0; i--) {
            result.append("</");
            result.append(open[i]);
            result.append(">\r\n");
        }
        if (onLoadScript.length() > 0)
            HtmlUtils.appendJavaScriptTo(result, "doOnLoad()");

        result.append("</BODY>\r\n</HTML>\r\n");
    }
    
    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        appendTo(result);
        return result.toString();
    }
}
