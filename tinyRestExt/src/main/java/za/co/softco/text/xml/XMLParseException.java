/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: XMLParseException.java,v $
 *  Created on Feb 18, 2009
 *******************************************************************************/
package za.co.softco.text.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import za.co.softco.util.Utils;

/**
 * Exception used to trace where the exception occurred
 * @author john
 * @model
 */
public class XMLParseException extends Exception {

    private static final long serialVersionUID = 3036784440788984190L;
    private final String path;
    private final String type;
    private final String name;
    
    public XMLParseException(String message) {
        super(message);
        this.path = null;
        this.type = null;
        this.name = null;
    }
    
    public XMLParseException(Throwable cause) {
        super(cause);
        this.path = null;
        this.type = null;
        this.name = null;
        if (cause != null)
            setStackTrace(cause.getStackTrace());
    }
    
    public XMLParseException(String path, Throwable cause) {
        super(cause);
        this.path = path;
        this.type = null;
        this.name = null;
        if (cause != null)
            setStackTrace(cause.getStackTrace());
    }
    
    public XMLParseException(Node node, Throwable cause) {
        super(cause);
        this.path = buildPath(node);
        this.type = (node != null ? node.getNodeName() : null);
        this.name = (node instanceof Element ? ((Element) node).getAttribute("Name") : null);
        if (cause != null)
            setStackTrace(cause.getStackTrace());
    }

    /**
     * Build a path of an XML element
     * @param node
     * @return
     */
    private static String buildPath(Node node) {
        if (node == null)
            return null;
        String type = node.getNodeName();
        String name = null;
        if (node instanceof Element) {
            Element el = (Element) node;
            name = el.getAttribute("Name");
            if (Utils.normalize(name) == null)
                name = el.getAttribute("Description");
        }
        String desc = (type != null ? type : "?") + ":" + (name != null ? name : "?");
        String parent = buildPath(node.getParentNode());
        if (parent != null)
            return parent + "/" + desc;
        return desc;
    }

    /**
     * Return the path to the element in the XML file
     * @return
     */
    public String getPath() {
        if (path != null)
            return path;
        if (type == null || name == null)
            return "";
        StringBuilder result = new StringBuilder();
        if (Utils.normalize(type) != null)
            result.append(type);
        else
            result.append("?");
        result.append(":");
        if (Utils.normalize(name) != null)
            result.append(name);
        else
            result.append("?");
        if (getCause() instanceof XMLParseException) {
            result.append("/");
            result.append(((XMLParseException) getCause()).getPath());
        }
        return result.toString();
    }
    
    @Override
    public String getMessage() {
        Throwable cause = getCause();
        String message = (cause != null ? cause.getMessage() : super.getMessage());
        if (path != null)
            return path + ": " + message;
        return message;
    }
}
