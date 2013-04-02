/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 14 Jan 2011
 *******************************************************************************/
package za.co.softco.text.xml.parse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import za.co.softco.bean.Builder;
import za.co.softco.reflect.AddChild;
import za.co.softco.reflect.Content;
import za.co.softco.reflect.Default;
import za.co.softco.reflect.Param;
import za.co.softco.reflect.Parent;
import za.co.softco.reflect.SetParent;
import za.co.softco.text.DataParser;
import za.co.softco.text.xml.XMLUtils;
import za.co.softco.util.Log;
import za.co.softco.util.TextConfiguration;
import za.co.softco.util.Utils;

/**
 * Default parser - uses bean manager and method names to build objects
 * @author john
 * @param <R> - Type of parsed root node 
 * @param <P> - Type of parsed parent node of nodes on which this parser operates
 * @param <T> - Type to return when the parser parses a node 
 */
public class DefaultParser<B,R extends B,P extends B,T extends B> extends AbstractParser<B,R,P,T> {

    private static final Method NO_METHOD;
    
    private final Map<Relation,Method> addChildMethods = new HashMap<Relation,Method>(10);
    private final Map<Relation,Method> setParentMethods = new HashMap<Relation,Method>(10);
    private final Class<T> elementClass;
    protected final String addToParentMethod;
    protected final String setParentMethod;
    protected final String setTextMethod;
    private final Method textSetter;
    
    static {
        NO_METHOD = DefaultParser.class.getMethods()[0];
    }
    
    /**
     * Constructor
     * @param elementClass
     * @param addToParentMethod
     * @param setParentMethod
     * @param setTextMethod
     * @param parseChildren
     * @throws ParserConfigurationException
     */
    public DefaultParser(Class<T> elementClass, String addToParentMethod, String setParentMethod, String setTextMethod, boolean parseChildren) {
        super(parseChildren);
        this.elementClass = elementClass;
        this.addToParentMethod = addToParentMethod;
        this.setParentMethod = setParentMethod;
        this.setTextMethod = setTextMethod;
        try {
			this.textSetter = findMethod(elementClass, setTextMethod, String.class);
		} catch (ParserConfigurationException e) {
			throw new IllegalArgumentException("setTextMethod (" + setTextMethod + ") is invalid for " + elementClass.getName());
		}
    }

    /**
     * Constructor
     * @param elementClass
     * @param parseChildren
     * @throws ParserConfigurationException
     */
    public DefaultParser(Class<T> elementClass, boolean parseChildren) {
        this(elementClass, null, null, null, parseChildren);
    }

    /**
     * Constructor
     * @param elementClass
     * @throws ParserConfigurationException
     */
    public DefaultParser(Class<T> elementClass) {
        this(elementClass, true);
    }

    /**
     * Return constructors based on parameter annotations
     * @param parent
     * @param attribs
     * @return
     */
	protected ObjectBuilder<P,T>[] getAnnotationConstructors(P parent, Map<String,String> attribs, String content) {
	    @SuppressWarnings("rawtypes")
    	Constructor[] cons = elementClass.getConstructors();
	    if (cons == null || cons.length == 0)
	    	return null;
		List<ObjectBuilder<P,T>> builders = new ArrayList<ObjectBuilder<P,T>>(cons.length);
    	for (int i=0; i<cons.length; i++) {
    		@SuppressWarnings("unchecked")
    		Constructor<T> con = cons[i];
    		if ((con.getModifiers() & Modifier.PUBLIC) == 0)
    			continue;
    		ObjectBuilder<P,T> builder = new ObjectBuilder<P,T>(con, parent, attribs, content);
    		if (builder.hasAnnotations())
    			builders.add(builder);
    	}
    	if (builders.size() == 0)
    		return null;
    	
		@SuppressWarnings("unchecked")
    	ObjectBuilder<P,T>[] result = builders.toArray(new ObjectBuilder[builders.size()]);
    	if (result.length > 1)
    		Arrays.sort(result);
    	return result;
    }
    
    /*
     * @see za.co.softco.text.xml.parse.Parser#parse(za.co.softco.text.xml.parse.ParserFactory, java.lang.Object, java.lang.Object, org.w3c.dom.Element, za.co.softco.text.xml.parse.AttributeParserFactory)
     */
    @Override
    public T parse(ParserState<B,R,P> state, Element node) throws ParseException {
    	Map<String,String> attribs = XMLUtils.getAttributes(node);
        T result = null;
        String content = XMLUtils.readText(node);
        P parent = (state != null ? state.getParent() : null);
        ObjectBuilder<P,T>[] builders = getAnnotationConstructors(parent, attribs, content);
        ParseException err = null;
        if (builders != null) {
        	for (ObjectBuilder<P,T> builder : builders) {
        		try {
        			result = builder.build();
        			if (result != null)
        				break;
                } catch (SecurityException e) {
                    // Ignore exception - try default constructor
                } catch (InstantiationException e) {
                	if (err == null)
                		err = Utils.cast(e, ParseException.class);
                } catch (IllegalAccessException e) {
                	if (err == null)
                		err = Utils.cast(e, ParseException.class);
                } catch (IllegalArgumentException e) {
                	if (err == null)
                		err = Utils.cast(e, ParseException.class);
                } catch (InvocationTargetException e) {
                	if (err == null)
                		err = Utils.cast(e, ParseException.class);
        		}
        	}
        }
        try {
            if (result == null && parent != null)
                result = elementClass.getConstructor(parent.getClass()).newInstance(parent);
        } catch (NoSuchMethodException e) {
            // Ignore exception - try default constructor
        } catch (SecurityException e) {
            // Ignore exception - try default constructor
        } catch (InstantiationException e) {
            throw Utils.cast(e, ParseException.class);
        } catch (IllegalAccessException e) {
            throw Utils.cast(e, ParseException.class);
        } catch (IllegalArgumentException e) {
            throw Utils.cast(e, ParseException.class);
        } catch (InvocationTargetException e) {
            throw Utils.cast(e, ParseException.class);
        }
        try {
            if (result == null)
                result = elementClass.newInstance();
            state.getAttributeParser().setAttributes(result, attribs);
            if (parent != null && result != null) {
                addToParent(parent, result);
                setParent(result, parent);
            }
            setText(parent, result, XMLUtils.readText(node));
            link(parent, result);
            parseChildren(node, state.getChildState(result));
            return result;
        } catch (ParseException e) {
        	if (err != null)
        		throw err;
            throw e;
        } catch (InstantiationException e) {
        	if (err != null)
        		throw err;
            throw Utils.cast(e, ParseException.class);
        } catch (IllegalAccessException e) {
        	if (err != null)
        		throw err;
            throw Utils.cast(e, ParseException.class);
		} catch (ParserConfigurationException e) {
        	if (err != null)
        		throw err;
            throw Utils.cast(e, ParseException.class);
		} catch (RuntimeException e) {
        	if (err != null)
        		throw err;
            throw Utils.cast(e, ParseException.class);
		}
    }
    
    /*
     * @see za.co.softco.model.parser.Parser#complete(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public void complete(R root, P parent, T node) {
        // This method does nothing by default and must be overwritten to be active 
    }
    
    /**
     * Link parent to child
     * @param parent
     * @param child
     * @return
     * @throws ParseException 
     */
    protected T link(P parent, T child) throws ParseException {
    	if (parent == null || child == null) 
        	return child;

    	for (Method m : parent.getClass().getMethods()) {
    		Class<?>[] params = m.getParameterTypes();
    		Annotation a = m.getAnnotation(AddChild.class);
    		if (a != null) { 
    			switch (params.length) {
    			case 0 :	
        			throw new ParseException(m.toString() + " has annotation " + AddChild.class.getSimpleName() + ", but method has no parameters", 0);
    			case 1 :
    				if (params[0].isInstance(child)) {
						try {
							m.invoke(parent, child);
						} catch (IllegalAccessException e) {
							throw Utils.cast(e, ParseException.class);
						} catch (IllegalArgumentException e) {
							throw Utils.cast(e, ParseException.class);
						} catch (InvocationTargetException e) {
							throw Utils.cast(e, ParseException.class);
						}
    				}
    				break;
    			case 2 :
    				break;
    			default :	
        			throw new ParseException(m.toString() + " has annotation " + AddChild.class.getSimpleName() + ", but method has too many parameters", 0);
    			}
    		}
    	}
    	
    	for (Method m : child.getClass().getMethods()) {
    		Class<?>[] params = m.getParameterTypes();
    		Annotation a = m.getAnnotation(SetParent.class);
    		if (a != null) { 
    			switch (params.length) {
    			case 0 :	
        			throw new ParseException(m.toString() + " has annotation " + SetParent.class.getSimpleName() + ", but method has no parameters", 0);
    			case 1 :
    				if (params[0].isInstance(parent)) {
						try {
							m.invoke(child, parent);
						} catch (IllegalAccessException e) {
							throw Utils.cast(e, ParseException.class);
						} catch (IllegalArgumentException e) {
							throw Utils.cast(e, ParseException.class);
						} catch (InvocationTargetException e) {
							throw Utils.cast(e, ParseException.class);
						}
    				}
    				break;
    			default :	
        			throw new ParseException(m.toString() + " has annotation " + SetParent.class.getSimpleName() + ", but method has too many parameters", 0);
    			}
    		}
    	}
    	
    	return child;
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws ParserConfigurationException {
        if (clazz == null)
            throw new IllegalArgumentException("Class must be specified");
        if (methodName == null)
            return null;
        try {
            Method result = clazz.getMethod(methodName, parameterTypes);
            if ((result.getModifiers() & Modifier.PUBLIC) == 0)
                throw new ParserConfigurationException("Method must be public: " + result.toString());
            if ((result.getModifiers() & Modifier.STATIC) != 0)
                throw new ParserConfigurationException("Method may not be static: " + result.toString());
            return result;
        } catch (SecurityException e) {
            throw Utils.cast(e, ParserConfigurationException.class);
        } catch (NoSuchMethodException e) {
            throw Utils.cast(e, ParserConfigurationException.class);
        }
    }
    
    protected static <K> Method getMethod(Map<K,Method> methods, K key, Object object, String methodName, Object... parameters) {
        if (object == null || methodName == null)
            return null;
        Method method = methods.get(key);
        if (method == NO_METHOD)
            return null;
        if (method != null) 
            return method;
        methods:
        for (Method m : object.getClass().getMethods()) {
            if (!methodName.equals(m.getName()))
                continue;
            Class<?>[] types = m.getParameterTypes();
            if (types.length != parameters.length)
                continue;
            if ((m.getModifiers() & Modifier.PUBLIC) == 0)
                continue;
            if ((m.getModifiers() & Modifier.STATIC) != 0)
                continue;
            params:
            for (int i=0; i<parameters.length; i++) {
                if (parameters[i] == null)
                    continue params;
                if (!types[i].isInstance(parameters[i]))
                    continue methods;
            }
            if (method != null)
                throw new IllegalStateException("Relation " + key + " has more than one possible child add method");
            method = m;
        }
        if (method != null) {
            //System.out.println("Adding relationship builder method for " + key + ": " + method.toString());
            methods.put(key, method);
        } else {
            methods.put(key, NO_METHOD);
        }
        return method;
    }

    protected static <K> boolean invoke(Map<K,Method> methods, K key, Object object, String regex, Object... parameters) throws ParseException {
        Method method = getMethod(methods, key, object, regex, parameters);
        if (method == null)
            return false;
        try {
            method.invoke(object, parameters);
            return true;
        } catch (IllegalArgumentException e) {
            throw Utils.cast(e, IllegalStateException.class);
        } catch (IllegalAccessException e) {
            throw Utils.cast(e, IllegalStateException.class);
        } catch (InvocationTargetException e) {
            throw Utils.cast(e, ParseException.class);
        }
    }
    
    /**
     * This method can be overwritten to add a child to a parent in a specific manner
     * or not at all.
     * @param parent
     * @param child
     * @throws ParseException
     */
    protected void addToParent(P parent, T child) throws ParseException {
        if (parent == null || child == null)
            return;
        invoke(addChildMethods, new Relation(parent, child), parent, addToParentMethod, child);
    }
    
    /**
     * This method can be overwritten to add a child to a parent in a specific manner
     * or not at all.
     * @param child
     * @param parent
     * @throws ParseException
     */
    protected void setParent(T child, P parent) throws ParseException {
        if (parent == null || child == null)
            return;
        invoke(setParentMethods, new Relation(parent, child), child, setParentMethod, parent);
    }
    
    /*
     * @see za.co.softco.model.parser.Parser#setText(java.lang.Object, java.lang.String)
     */
    @Override
    public void setText(P parent, T object, String text) throws ParseException {
        if (object == null || text == null)
            return;
        if (textSetter != null) {
            try {
                textSetter.invoke(object, text);
                return;
            } catch (IllegalArgumentException e) {
                throw Utils.cast(e, ParseException.class);
            } catch (IllegalAccessException e) {
                throw Utils.cast(e, ParseException.class);
            } catch (InvocationTargetException e) {
                throw Utils.cast(e, ParseException.class);
            }
        }
        if (object instanceof TextConfiguration) {
            ((TextConfiguration) object).setText(text);
        }
    }
    
    /**
     * Parent child class relationship
     * @author john
     */
    private static class Relation {
        private final Class<?> parent;
        private final Class<?> child;
        
        public Relation(Class<?> parent, Class<?> child) {
            this.parent = parent;
            this.child = child;
        }
        
        public Relation(Object parent, Object child) {
            this(parent.getClass(), child.getClass());
        }
        
        @Override
        public int hashCode() {
            return parent.hashCode() + child.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Relation))
                return false;
            Relation ref = (Relation) obj;
            return ref.parent.equals(parent) && ref.child.equals(child);
        }
        
        @Override
        public String toString() {
            return parent.getSimpleName() + " -> " + child.getSimpleName();
        }
    }

    protected static class ObjectBuilder<P,T> implements Comparable<ObjectBuilder<P,T>> {
    	private final Constructor<T> constructor;
    	private final Object[] parameters;
    	private final int weight;
    	public ObjectBuilder(Constructor<T> constructor, P parent, Map<String,String> attribs, String content) {
    		this.constructor = constructor;
    		Class<?>[] types = constructor.getParameterTypes();
    		this.parameters = new Object[(types != null ? types.length : 0)];
    		if (parameters.length > 0) {
    			Annotation[][] anno = constructor.getParameterAnnotations();
    			int w = 0;
    			for (int i=0; i<parameters.length; i++) {
    				String dflt = null;
    				for (Annotation a : anno[i]) {
    					if (Parent.class.isInstance(a)) {
    						this.parameters[i] = Builder.cast(parent, types[i]);
    						w += 10000;
    						break;
    					}
    					if (Content.class.isInstance(a)) {
    						this.parameters[i] = Builder.cast(content, types[i]);
    						w += 5000;
    						break;
    					}
    					if (Default.class.isInstance(a)) {
    						dflt = ((Default) a).value(); 
    					}
    					if (Param.class.isInstance(a)) {
    						String name = ((Param) a).value();
    						if (name == null) {
    							w -= 1000;
    						} else if (attribs.containsKey(name)) {
        						w += 100;
    							try {
            						this.parameters[i] = Builder.cast(attribs.get(name), types[i]);
    							} catch (ClassCastException e) {
    								w -= 10;
    							}
    						} else {
        						w -= 1;
    						}
    					}
    				}
    				if (this.parameters[i] == null && dflt != null)
    					this.parameters[i] = Builder.cast(dflt, types[i]);
    			}
    			this.weight = w;
    		} else {
    			this.weight = 0;
    		}
    	}

    	public boolean hasAnnotations() {
    		return (weight > 0 ? true : false);
    	}
    	
    	/*
    	 * @see java.lang.Comparable#compareTo(java.lang.Object)
    	 */
    	@Override
    	public int compareTo(ObjectBuilder<P,T> o) {
    		if (o == null)
    			return -1;
    		return Integer.valueOf(o.weight).compareTo(Integer.valueOf(weight));
    	}
    	
    	/**
    	 * Build error message
    	 * @param parameters
    	 * @param e
    	 * @return
    	 */
    	private String buildMessage(Object[] parameters, Throwable e) {
    		e = Log.unwrap(e);
    		return constructor.getName() + "\r\nParameters: " + DataParser.format(parameters) + "\r\nError: " + e.getMessage();
    	}
    	
    	/**
    	 * Build the object
    	 * @return
    	 * @throws InstantiationException
    	 * @throws IllegalAccessException
    	 * @throws IllegalArgumentException
    	 * @throws InvocationTargetException
    	 */
    	public T build() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    		if (parameters != null && parameters.length > 0) {
    			Class<?>[] types = constructor.getParameterTypes();
				for (int i=0; i<parameters.length; i++) {
					if (this.parameters[i] == null && types[i].isPrimitive())	
						this.parameters[i] = Builder.cast(null, types[i]);
				}
    		}
    		try {
    			return constructor.newInstance(parameters);
    		} catch (InstantiationException e) {
    			throw Utils.cast(e, InstantiationException.class, buildMessage(parameters, e));
    		} catch (IllegalAccessException e) {
    			throw Utils.cast(e, IllegalAccessException.class, buildMessage(parameters, e));
    		} catch (IllegalArgumentException e) {
    			throw Utils.cast(e, IllegalArgumentException.class, buildMessage(parameters, e));
    		} catch (InvocationTargetException e) {
    			throw Utils.cast(e, InvocationTargetException.class, buildMessage(parameters, e.getTargetException()));
    		}
    	}
    }
}
