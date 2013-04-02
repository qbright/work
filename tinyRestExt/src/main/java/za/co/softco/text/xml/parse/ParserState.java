/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 08 Nov 2012
 *******************************************************************************/
package za.co.softco.text.xml.parse;

import java.io.File;
import java.util.Collection;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;


/**
 * @author john
 * <B> Base class for all nodes
 * <R> Root note type
 * <P> Parent node type
 */
public interface ParserState<B,R extends B,P extends B> {

	public ParserFactory<B,R> getFactory();
	
    public <C extends B> Parser<B,R,P,C> getChildParser(Element element);
	
	public R getRoot();
	
	public P getParent();

	public File getXmlFile();
	
	public String getPath();
	
	public Locale getLocale();
	
	public String getRootName();
	
	public AttributeParserFactory getAttributeParser() throws ParserConfigurationException;
	
	public void addNamedItem(String name, B item);
	
	public Collection<B> getNamedItems(String name);
	
	public void addCompletionTask(Runnable task);
	
	public <T extends B> ParserState<B,R,T> getChildState(T child,  File xmlFile, String name);

	public <T extends B> ParserState<B,R,T> getChildState(T child,  File xmlFile);

	public <T extends B> ParserState<B,R,T> getChildState(T child,  String name);

	public <T extends B> ParserState<B,R,T> getChildState(T child);
}
