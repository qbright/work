/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 13 Oct 2009
 *******************************************************************************/
package za.co.softco.text.xml;

import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author john
 * Iterable implementation for NodeList
 */
public class ElementIterable implements Iterable<Element> {

	protected final Element parent;
	protected final NodeList nodes;
	
	/**
	 * Internal constructor
	 * @param parent
	 * @param nodes
	 */
	private ElementIterable(Element parent, NodeList nodes) {
		this.parent = parent;
		this.nodes = nodes;
	}

	/**
	 * Constructor
	 * @param parent
	 */
	public ElementIterable(Element parent) {
		this(parent, parent.getChildNodes());
	}

	/**
	 * Constructor
	 * @param parent
	 * @param elementName
	 */
	public ElementIterable(Element parent, String elementName) {
		this(parent, parent.getElementsByTagName((elementName != null ? elementName : "*")));
	}

	/*
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Element> iterator() {
		if (nodes == null) {
			return new Iterator<Element>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public Element next() {
					return null;
				}

				@Override
				public void remove() {
					// Nothing to do
				}
			};
		}
		return new Iterator<Element>() {
			private int index = 0;
			private Element current;
			@Override
			public boolean hasNext() {
				int ndx = index;
				while (ndx < nodes.getLength() && !(nodes.item(ndx) instanceof Element))
					ndx++;
				return (ndx < nodes.getLength());
			}

			@Override
			public Element next() {
				int ndx = index;
				while (ndx < nodes.getLength() && !(nodes.item(ndx) instanceof Element))
					ndx++;
				index = ndx + 1;
				current = (ndx < nodes.getLength() ? (Element) nodes.item(ndx) : null);
				return current;
			}

			@Override
			public void remove() {
				throw new IllegalStateException("remove() is not supported");
			}
		};
	}
}
