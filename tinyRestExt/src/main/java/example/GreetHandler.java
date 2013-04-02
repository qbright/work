/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 23 Jun 2010
 *******************************************************************************/
package example;

import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import za.co.softco.rest.RestException;
import za.co.softco.rest.http.HttpConstants;
import za.co.softco.rest.model.Context;
import za.co.softco.text.xml.XMLUtils;
import za.co.softco.util.PropertyMap;

/**
 * This is an example class for writing a REST service using the ReflectionService
 * class to interpret the handler. The only rule is that you have to create
 * a public method with a single RestRequest parameter. The intension is to
 * extend the model to allow the use of annotations to simplify things a bit
 * @author john
 */
public class GreetHandler {

    private static Map<String,String> properties = new PropertyMap<String>();
    
    /**
     * You can call this function with the following URL:<br>
     * <a href="http://locahost:8088/rest/greet/ask?question=What%20is%20your%20name?">http://locahost:8088/rest/greet/ask?question=What%20is%20your%20name?</a><br>
     * @param context
     * @throws Exception
     */
    public void ask(Context context) throws Exception {
        Object question = context.getQueryParameters().get("question");
        if (question == null)
            throw new RestException(HttpConstants.HTTP_BAD_REQUEST, "You have to ask a question, silly!");
        String q = question.toString();
        q = q.replaceAll(" ", "");
        q = q.replaceAll(Pattern.quote("?"), "");
        if (q.equalsIgnoreCase("WhatIsYourName")) {
            context.writeHtmlReply("My name is Peet.");
            return;
        }
        if (q.equalsIgnoreCase("WhereDoYouLive")) {
            context.writeHtmlReply("I live up in the mountains.");
            return;
        }
        context.writeHtmlReply("Sorry, I could not hear you.");
    }
    
    /**
     * You can call this method using the following URL:<br>
     * <a href="http://locahost:8088/rest/greet/property/height">http://locahost:8088/rest/greet/property/height</a><br>
     * The part of the path after "property" can be accessed with the getItem() method
     * @param propertyName
     * @return
     * @throws RestException
     */
    public void property(Context context) throws Exception {
        String propertyName = context.getItem();
        if (propertyName == null)
            throw new RestException(HttpConstants.HTTP_BAD_REQUEST, "You have to specify a property");
        
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("Response");
        doc.appendChild(root);
        root.setAttribute("property", propertyName);
        
        String result = properties.get(propertyName);
        if (result != null)
            root.appendChild(doc.createTextNode(result));
        else if (propertyName.equalsIgnoreCase("height"))
            root.appendChild(doc.createTextNode("Tall enough"));
        else if (propertyName.equalsIgnoreCase("weight"))
            root.appendChild(doc.createTextNode("Yea right, as if I'm going to tell you..."));
        else if (propertyName.equalsIgnoreCase("age"))
            root.appendChild(doc.createTextNode("As young as ever"));
        else 
            root.appendChild(doc.createTextNode("I don't know what \"" + propertyName + "\" means."));
        
        context.writeXmlReply(root);
    }

    /**
     * Save a single property
     * @param node
     * @return
     */
    private String saveProperty(Node node) {
        if (!(node instanceof Element))
            return null;
        Element el = (Element) node;
        String name = el.getAttribute("property");
        if (name == null || name.trim().length() == 0)
            name = el.getAttribute("name");
        if (name == null || name.trim().length() == 0)
            return null;
        String result = el.getAttribute("value");
        if (result != null) {
            properties.put(name, result);
            return result;
        }
        result = XMLUtils.readText(el);
        if (result != null) 
            properties.put(name, result);
        return result;
    }
    
    /**
     * This method allows you to handle a POST with XML content. The important
     * part is to post with "Content-Type: text/xml" in order for content body
     * to be parsed as xml. The XML should look like this:<br>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <Properties>
     *   <Property name="country" value="South Africa"/>
     *   <Property name="weather">Cloudy and cool</Property>
     * </Properties>
     * <br>
     * To test this method, you can make open telnet / netcat / nc session
     * to your service anc copy the contents of setProperties.txt to it.
     * @return
     * @throws RestException
     */
    public void setProperties(Context context) throws Exception {
        Element root = (Element) context.getContentObject();
        if (root == null)
            throw new RestException(HttpConstants.HTTP_BAD_REQUEST, "The content body is empty");

        saveProperty(root);
        NodeList nodes = root.getChildNodes();
        for (int i=0; i<nodes.getLength(); i++)
            saveProperty(nodes.item(i));
    }
}
