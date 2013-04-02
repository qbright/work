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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import za.co.softco.util.PropertyMap;

/**
 * @author john
 *
 */
public class BasicState<B,R extends B,P extends B> implements ParserState<B,R,P> {

	private final ParserFactory<B,R> factory;	
	private final BasicState<B,R,? extends B> parentState;
	private final P parentNode;
	private final File xmlFile;
	private final String name;
	private final String path;
	private final Locale locale;
	private AttributeParserFactory attributeParser;
	private Map<String,Object> namedItems;
	private List<Runnable> completionTasks;
	
	protected BasicState(BasicState<B,R,? extends B> state, P node, File xmlFilem, String name) {
		this.factory = null;
		this.parentState = state;
		this.parentNode = node;
		this.xmlFile = null;
		this.name = name;
		this.path = state.path + "/" + (name != null ? name : "?");
		this.locale = null;
	}
	
	public BasicState(ParserFactory<B,R> factory, File xmlFile, P root, Locale locale) {
		this.factory = factory;
		this.parentState = null;
		this.parentNode = root;
		this.xmlFile = xmlFile;
		this.name = (xmlFile != null ? xmlFile.getName() : null);
		this.path = "/" + (this.name != null ? this.name : "");
		this.locale = locale;
	}
	
	public BasicState(ParserFactory<B,R> factory, File xmlFile, P root, AttributeParserFactory attributeParser, Locale locale) {
		this.factory = factory;
		this.parentState = null;
		this.parentNode = root;
		this.xmlFile = xmlFile;
		this.name = (xmlFile != null ? xmlFile.getName() : null);
		this.path = "/" + (this.name != null ? this.name : "");
		this.locale = locale;
		this.attributeParser = (attributeParser != null ? attributeParser : new DummyAttributeParserFactory());
	}
	
	public BasicState(ParserFactory<B,R> factory, String name, P root, Locale locale) {
		this.factory = factory;
		this.parentState = null;
		this.parentNode = root;
		this.xmlFile = null;
		this.name = name;
		this.path = "/" + (name != null ? name : "");
		this.locale = locale;
	}

	public BasicState(ParserFactory<B,R> factory, String name, P root, AttributeParserFactory attributeParser, Locale locale) {
		this.factory = factory;
		this.parentState = null;
		this.parentNode = root;
		this.xmlFile = null;
		this.name = name;
		this.path = "/" + (name != null ? name : "");
		this.locale = locale;
		this.attributeParser = (attributeParser != null ? attributeParser : new DummyAttributeParserFactory());
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getFactory()
	 */
	@Override
    public ParserFactory<B,R> getFactory() {
		if (parentState != null) {
			ParserFactory<B,R> result = parentState.getFactory();
			if (result != null)
				return result;
		}
		return factory; 
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getLocale()
	 */
    @Override
	public Locale getLocale() {
		if (parentState != null)
			return parentState.getLocale();
		return (locale != null ? locale : Locale.getDefault());
	}
	
	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getChildParser(org.w3c.dom.Element)
	 */
    @Override
    public <C extends B> Parser<B,R,P,C> getChildParser(Element element) {
    	return getFactory().getParser(parentNode, element);
    }
	
	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getRoot()
	 */
	@Override		
	@SuppressWarnings("unchecked")
	public R getRoot() {
		if (parentState != null) {
			R result = (R) parentState.getRoot();
			if (result != null)
				return result;
		}
		return (R) parentNode;
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getParent()
	 */
	@Override
	public P getParent() {
		return parentNode;
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getAttributeParser()
	 */
	@Override
	public AttributeParserFactory getAttributeParser() throws ParserConfigurationException {
		if (attributeParser != null)
			return attributeParser;
		if (parentState != null)
			return parentState.getAttributeParser();		
		attributeParser = new DefaultAttributeParserFactory();
		return attributeParser;
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#addNamedItem(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addNamedItem(String name, B item) {
		if (name == null || item == null)
			return;
		if (parentState != null)
			parentState.addNamedItem(name, item);
		if (namedItems == null)
			namedItems = new PropertyMap<Object>();
		
		if (namedItems.containsKey(name)) {
			Object tmp = namedItems.get(name);
			if (!(tmp instanceof Container)) {
				Container<B> cnt = new Container<B>();
				tmp = cnt;
				namedItems.put(name, tmp);
			}
			@SuppressWarnings("unchecked")
			Container<B> cnt = (Container<B>) tmp;
			cnt.add(item);
		} else {
			namedItems.put(name, item);
		}
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getNamedItems(java.lang.String)
	 */
	@Override
	public Collection<B> getNamedItems(String name) {
		if (name == null)
			return Collections.emptyList();
		Collection<B> result = new LinkedList<B>(); 
		if (namedItems != null) {
			Object tmp = namedItems.get(name);
			if (tmp instanceof Container) {
				@SuppressWarnings("unchecked")
				Container<B> items = (Container<B>) tmp;
				result.addAll(items);
			} else if (tmp != null) {
				@SuppressWarnings("unchecked")
				B item = (B) tmp;
				result.add(item);
			}
		}
		if (parentState != null) {
			Collection<B> items = parentState.getNamedItems(name);
			if (items != null && items.size() > 0)
				result.addAll(items);
		}
		return result;
	}
	
	/*
	 * @see za.co.softco.text.xml.parse.ParserState#addCompletionTask(java.lang.Runnable)
	 */
	@Override
	public void addCompletionTask(Runnable task) {
		if (task == null)
			return;
		if (parentState != null)
			parentState.addCompletionTask(task);
		if (completionTasks == null)
			completionTasks = new LinkedList<Runnable>();
		completionTasks.add(task);
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getChildState(java.lang.Object, java.io.File, java.lang.String)
	 */
    @Override
	public <T extends B> ParserState<B,R,T> getChildState(T child, File xmlFile, String name) {
		return new BasicState<B,R,T>(this, child, xmlFile, name);
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getChildState(java.lang.Object, java.io.File)
	 */
    @Override
	public <T extends B> ParserState<B,R,T> getChildState(T child, File xmlFile) {
		return new BasicState<B,R,T>(this, child, xmlFile, null);
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getChildState(java.lang.Object, java.lang.String)
	 */
    @Override
	public <T extends B> ParserState<B,R,T> getChildState(T child, String name) {
		return new BasicState<B,R,T>(this, child, null, name);
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getChildState(java.lang.Object)
	 */
    @Override
	public <T extends B> ParserState<B,R,T> getChildState(T child) {
		return new BasicState<B,R,T>(this, child, null, null);
	}
	
	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getXmlFile()
	 */
	@Override
	public File getXmlFile() {
		return (xmlFile != null ? xmlFile : (parentState != null ? parentState.getXmlFile() : null));
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	/*
	 * @see za.co.softco.text.xml.parse.ParserState#getRootName()
	 */
    @Override
	public String getRootName() {
		return (parentState != null ? parentState.getRootName() : null);
	}
	
	public Map<String,Collection<B>> getNamedItems() {
		if (parentState != null)
			return parentState.getNamedItems();
		if (namedItems != null) {
			Map<String,Collection<B>> result = new PropertyMap<Collection<B>>();
			for (Map.Entry<String,Object> e : namedItems.entrySet())
				result.put(e.getKey(), toCollection(e.getValue()));
			return result;
		}
		return Collections.emptyMap();
	}
	
	public Runnable[] getCompletionTasks() {
		if (parentState != null)
			return parentState.getCompletionTasks();
		if (completionTasks != null)
			return completionTasks.toArray(new Runnable[completionTasks.size()]);
		return new Runnable[0];
	}
	
	@SuppressWarnings("unchecked")
	private Collection<B> toCollection(Object value) {
		if (value instanceof Container)
			return (Collection<B>) value;
		if (value != null)
			return new Container<B>((B) value);
		return Collections.emptyList();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (path != null ? path : "/");
	}
	
	/**
	 * A simple class used to distinguish between single and multiple items on 
	 * a single name
	 * @author john
	 */
	@SuppressWarnings("serial")
	private static class Container<T> extends LinkedList<T> {
		@SafeVarargs
		public Container(T... items) {
			for (T item : items)
				add(item);
		}
		public Container() {
			// Nothing to do
		}
	}
}
