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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import za.co.softco.text.DataParser;
import za.co.softco.util.PropertyMap;
import za.co.softco.util.Utils;


/**
 * @author john
 *
 */
public class BasicHtmlElement<P extends HtmlElement> implements HtmlElement {

    static final Object TAG = new Object();
    
    private final P parent;
    protected final String elementType;
    protected final StringBuilder html = new StringBuilder(); 
    protected final Collection<HtmlElement> elements = new LinkedList<HtmlElement>();
    protected final Map<String,Object> attributes = new PropertyMap<Object>(new LinkedHashMap<String,Object>());
    private final ElementStack elementStack = new ElementStack();
    private boolean closed = false;
    
    protected BasicHtmlElement(P parent) {
        this(parent, null);
    }

    protected BasicHtmlElement(P parent, String elementType) {
        this.parent = parent;
        this.elementType = Utils.normalize(elementType);
    }

    protected String allowTopElement(String... allowed) {
        return elementStack.allowTopElement(allowed);
    }
    
    protected String failTopElement(String... notAllowed) {
        return elementStack.failTopElement(notAllowed);
    }
    
    protected String peek() {
        return elementStack.peek();
    }
    
    protected String pop() {
        return elementStack.pop();
    }
    
    protected void push(String element) {
        elementStack.push(element);
    }
    
    @Override
    public String getType() {
        return elementType;
    }
    
    @Override
    public String getName() {
        return DataParser.format(getAttribute("name"));
    }
    
    public String getQualifiedName() {
        if (getName() == null)
            return null;
        StringBuilder result = new StringBuilder();
        result.append(getName());
        HtmlElement node = getParent();
        while (node.getParent() != null && !(node instanceof HtmlDocument)) {
            if (node instanceof HtmlDocument) {
                result.insert(0, "document.");
                break;
            }
            String name = node.getName();
            if (name != null) {
                result.insert(0, '.');
                result.insert(0, name);
            }
            node = node.getParent();
        }
        return result.toString();
    }
    
    public void addTag(String tag) {
        addAttribute(tag, TAG);
    }
    
    public boolean hasTag(String tag) {
        return (getAttribute(tag) == TAG);
    }
    
    public Object getAttribute(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name is required");
        return attributes.get("name");
    }
    
    public void removeAttribute(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name is required");
        attributes.remove(name);
    }
    
    public void addAttribute(String name, Object value) {
        if (elementType == null)
            throw new IllegalStateException("Element must be created with an element type in order to have attributes");
        if (name == null)
            throw new IllegalArgumentException("Name is required");
        attributes.put(name, value);
    }
    
    protected <T extends HtmlElement> T addElement(T element) {
        elements.add(element);
        return element;
    }
    
    /**
     * Append some HTML text
     * @param text
     */
    protected HtmlElement append(String text) {
        if (closed)
            throw new IllegalStateException("Already closed");
        if (text != null)
            html.append(text);
        return this;
    }

    /**
     * Append some HTML text
     * @param text
     */
    protected HtmlElement append(Appendable text) {
        if (closed)
            throw new IllegalStateException("Already closed");
        if (text != null)
            html.append(text);
        return this;
    }

    /**
     * Return the HTML document object
     * @return
     */
    public HtmlDocument getDocument() {
        HtmlElement result = this.parent;
        while (result != null && !(result instanceof HtmlDocument))
            result = result.getParent();
        return (HtmlDocument) result;
    }

    /**
     * Return the form that this element is associated with
     * @return
     */
    protected HtmlForm getForm() {
        HtmlElement result = this.parent;
        while (result != null && !(result instanceof HtmlForm))
            result = result.getParent();
        return (HtmlForm) result;
    }
    
    /*
     * @see za.co.softco.text.html.HtmlElement#getParent()
     */
    @Override
    public P getParent() {
        return parent;
    }
    
    /*
     * @see za.co.softco.text.html.HtmlElement#appendTo(java.lang.StringBuilder)
     */
    @Override
    public void appendTo(StringBuilder result) {
        if (elementType != null) {
            result.append('<');
            result.append(elementType);
            for (Map.Entry<String,Object> attrib : attributes.entrySet()) 
                HtmlUtils.appendAttributeTo(result, attrib.getKey(), attrib.getValue());
            if (html.length() > 0 || elements.size() > 0) {
                result.append(">\r\n");
                for (HtmlElement el : elements)
                    el.appendTo(result);
                result.append(html);                
                result.append("</");
                result.append(elementType);
                result.append(">\r\n");
            } else {
                result.append("/>\r\n");
            }
        } else {
            int len = result.length();
            for (HtmlElement el : elements)
                el.appendTo(result);
            result.append(html);
            if (result.length() == len)
                result.append("&nbsp;");
        }
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
    
    /*
     * @see za.co.softco.text.html.HtmlElement#close()
     */
    @Override
    public HtmlElement close() {
        if (closed)
            throw new IllegalStateException("Already closed");
        closed = true;
        return parent;
    }

}
