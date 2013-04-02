/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library 
 * Description: Library classes
 *
 * Changelog  
 *  $Log: PropertyMap.java,v $
 *  Revision 1.6  2008/02/26 23:53:19  remjohn
 *  Fix null pointer bug
 *
 *  Revision 1.5  2008/01/30 13:38:48  remjohn
 *  Refactor in order to remember original case even for case insensitive map
 *
 *  Revision 1.4  2007/10/05 00:49:05  remjohn
 *  Refactor to avoid warnings
 *
 *  Revision 1.3  2007/09/07 09:33:40  remjohn
 *  Improved formatting
 *
 *  Revision 1.2  2007/09/07 07:37:43  remjohn
 *  Added isCaseSensitive()
 *
 *  Revision 1.1  2007/08/15 13:05:58  rembrink
 *  Added to CVS
 *
 *  Revision 1.1  2007/08/05 08:31:11  john
 *  Converted base package to za.co.softco
 *
 *  Revision 1.1  2007/06/14 10:21:24  goofyxp
 *  Split besterBase from bester library
 *
 *  Revision 1.1  2007/04/16 10:39:49  goofyxp
 *  Added to CVS
 *
 *  Revision 1.11  2007/03/04 07:54:19  goofyxp
 *  Added static getpropertyMap(Map)
 *
 *  Revision 1.10  2007/02/16 20:32:42  goofyxp
 *  Add constructor with initial size
 *
 *  Revision 1.9  2006/11/28 10:54:26  goofyxp
 *  Remove unnecessary casts
 *
 *  Revision 1.8  2006/10/24 17:47:36  goofyxp
 *  Improve handing of NULL
 *
 *  Revision 1.7  2006/09/29 10:23:12  obelix
 *  Bug fix: Copy values of referenced map to fix case
 *  Rename generics parameter
 *
 *  Revision 1.6  2006/07/29 09:30:12  obelix
 *  Added getCaseInsensitiveMap()
 *
 *  Revision 1.5  2006/07/28 15:08:12  obelix
 *  Added constructor
 *
 *  Revision 1.4  2006/05/25 14:59:49  hugo
 *  Format code
 *
 *  Revision 1.3  2006/03/18 19:52:39  obelix
 *  Restructure code to use Map<String, Object> in stead of Properties
 *
 *  Revision 1.2  2006/03/10 07:39:08  goofyxp
 *  Added toString()
 *
 *  Revision 1.1  2005/12/09 10:14:11  obelix
 *  Class added
 *
 *  Created on 08-Dec-2005
 *******************************************************************************/
package za.co.softco.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import za.co.softco.text.DataParser;

/**
 * Proxy class to optionally access a map in a case insensitive fashion
 * @author john
 * @model
 */
public class PropertyMap<T extends Object> implements Map<String, T> {
    private final boolean caseSensitive;

    private final boolean trimKey;

    private final Map<PropertyMap.Key, T> map;

    /**
     * Default constructor
     */
    public PropertyMap() {
        this(new HashMap<String, T>(), false, true);
    }

    /**
     * Default constructor
     * @param initialSize
     */
    public PropertyMap(int initialSize) {
        this(new HashMap<String, T>(initialSize), false, true);
    }

    /**
     * Constructor
     * @param map
     */
    public PropertyMap(Map<String, T> map) {
        this(map, false, true);
    }

    /**
     * Constructor
     * @param map
     * @param caseSensitive
     */
    public PropertyMap(Map<String, T> map, boolean caseSensitive) {
        this(map, caseSensitive, true);
    }

    /**
     * Constructor
     * @param map
     * @param caseSensitive
     * @param trimKey
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PropertyMap(Map<String, T> map, boolean caseSensitive, boolean trimKey) {
        if (map == null)
            throw new IllegalArgumentException("Map parameter is required");
        this.trimKey = trimKey;
        this.caseSensitive = caseSensitive;
        if (map instanceof PropertyMap) {
            this.map = ((PropertyMap) map).map;
            return;
        }
        this.map = createMap(map);
        this.putAll(map);
    }

    /**
     * Create a map with the same functionality as the
     * map parameter. If a new instance could not be created,
     * a best attempt at cloning the functionality is made.
     * @param map
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<PropertyMap.Key, T> createMap(Map<String, T> map) {
        Map<PropertyMap.Key, T> result;
        try {
            result = map.getClass().newInstance();
        } catch (Exception e) {
            if (map instanceof LinkedHashMap)
                result = new LinkedHashMap<PropertyMap.Key, T>();
            else
                result = new HashMap<PropertyMap.Key, T>();
        }
        for (Map.Entry<String,T> e : map.entrySet())
            result.put(fixKey(e.getKey()), e.getValue());
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> read(String properties, String separator, boolean nullIfEmpty) {
        properties = Utils.normalize(properties);
        if (properties == null)
            return (nullIfEmpty ? null : Collections.EMPTY_MAP);
        Map<String,Object> result = new PropertyMap<Object>();
        if (separator == null) { 
            properties = properties.replaceAll(Pattern.quote("\r\n"), "\n");
            properties = properties.replaceAll(Pattern.quote("\r"), "\n");
            properties = properties.replaceAll(Pattern.quote("\n"), ";");
            separator = ";";
        }
        int pos = 0;
        do {
            int start = pos;
            int next = properties.indexOf(separator, start);
            if (next < 0)
                next = properties.length();
            pos = next + 1;
            int eq = properties.indexOf('=', start);
            if (eq == pos)
                continue;
            String name = properties.substring(start, eq).trim();
            if (name.length() == 0)
                continue;
            if (eq < 0) {
                result.put(name, Boolean.TRUE);
                continue;
            }
            String value = Utils.normalize(properties.substring(eq+1, next));
            result.put(name, value);
        } while (pos < properties.length());
        return result;
    }
    
    /**
     * Create a map with the same functionality as the
     * map parameter. If a new instance could not be created,
     * a best attempt at cloning the functionality is made.
     * @param map
     * @return
     */
    public static Map<String, String> createMap(NamedNodeMap attribs) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (attribs == null)
        	return result;
        
        for (int i=0; i<attribs.getLength(); i++) {
        	Node node = attribs.item(i);
        	result.put(node.getNodeName(), node.getNodeValue());
        }
        return result;
    }
    
    /**
     * Constructor
     * @param properties
     */
    public PropertyMap(Properties properties) {
        this(properties, false, true);
    }

    /**
     * Constructor
     * @param properties
     * @param caseSensitive
     */
    public PropertyMap(Properties properties, boolean caseSensitive) {
        this(properties, caseSensitive, true);
    }

    /**
     * Get a case insensitive mapping
     * @param <X>
     * @param map
     * @return
     */
    public static <X> PropertyMap<X> getCaseInsensitiveMap(Map<String, X> map) {
        if (map == null)
            return new PropertyMap<X>();
        if (map instanceof PropertyMap<?> && !((PropertyMap<?>) map).caseSensitive)
            return (PropertyMap<X>) map;
        return new PropertyMap<X>(map);
    }
    
    /**
     * Get a case insensitive mapping
     * @param <X>
     * @param map
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PropertyMap<Object> getPropertyMap(Map<?,?> map) {
        if (map == null)
            throw new IllegalArgumentException("No map specified");
        
        boolean allStrings = true;
        for (Object key : map.keySet()) {
            if (!(key instanceof String)) {
                allStrings = false;
                break;
            }
        }
        if (allStrings)
            return getCaseInsensitiveMap((Map<String, Object>) map);
        
        PropertyMap<Object> result = new PropertyMap<Object>(map.size());
        for (Map.Entry<?, ?> entry : map.entrySet()) 
            result.put(DataParser.cast(entry.getKey(), String.class), entry.getValue());
        return result;
    }
    
    /**
     * Get a case insensitive mapping
     * @param <X>
     * @param map
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <V> PropertyMap<V> getPropertyMap(Map<?,?> map, Class<V> valueType) {
        if (map == null)
            throw new IllegalArgumentException("No map specified");
        
        boolean noCast = true;
        for (Map.Entry<?,?> e : map.entrySet()) {
            if (!(e.getKey() instanceof String)) {
                noCast = false;
                break;
            }
            if (!valueType.isInstance(e.getValue())) {
                noCast = false;
                break;
            }
        }
        if (noCast)
            return getCaseInsensitiveMap((Map<String, V>) map);
        
        PropertyMap<V> result = new PropertyMap<V>(map.size());
        for (Map.Entry<?, ?> entry : map.entrySet()) 
            result.put(DataParser.cast(entry.getKey(), String.class), DataParser.cast(entry.getValue(), valueType));
        return result;
    }
    
    /**
     * Convert a map to an instance of Bindings that can be used with the scripting framework
     * @param map
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Bindings getBindings(Map<?,?> map) {
        if (map == null)
            return new SimpleBindings();
        if (map instanceof Bindings)
            return (Bindings) map;
        if (!(map instanceof PropertyMap)) 
            map = getPropertyMap(map);
        
        @SuppressWarnings({ "rawtypes" })
        PropertyMap tmp = (PropertyMap) map; 
        if (tmp.map instanceof Bindings)
            return (Bindings) tmp.map;
        return new SimpleBindings(tmp.map);
    }

    /**
     * Convert this object to an instance of Bindings
     * @return
     */
    @SuppressWarnings("unchecked")
    public Bindings getBindings() {
        if (this instanceof Bindings)
            return (Bindings) this;
        @SuppressWarnings("rawtypes")
        Map tmp = map;
        if (tmp instanceof Bindings)
            return (Bindings) tmp;
        return new SimpleBindings(tmp);
    }
    
    /**
     * Constructor
     * @param properties
     * @param caseSensitive
     * @param trimKey
     */
    @SuppressWarnings("unchecked")
    public PropertyMap(Properties properties, boolean caseSensitive, boolean trimKey) {
        if (properties == null)
            throw new IllegalArgumentException("Map parameter is required");
        this.trimKey = trimKey;
        this.caseSensitive = caseSensitive;
        this.map = new HashMap<PropertyMap.Key, T>();
        for (Map.Entry<Object,Object> entry : properties.entrySet()) {
            try {
                put(entry.getKey().toString(), (T) entry.getValue());
            } catch (Throwable e) {
                System.err.println(e.getClass().getName().replaceAll(".*.\\.", "") + (e.getMessage() != null ? ": " + e.getMessage() : ""));
                e.printStackTrace();
            }
        }
    }

    /**
     * Fix the case of the key
     * 
     * @param key
     * @return
     * @model
     */
    private PropertyMap.Key fixKey(Object key) {
        if (key == null)
            return null;

        String result = key.toString();
        if (result == null)
            return null;

        if (trimKey)
            result = result.trim();
        
        if (!caseSensitive)
            return new CIKey(result);

        return new Key(result);
    }

    /*
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        return map.size();
    }

    /*
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /*
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(fixKey(key));
    }

    /*
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /*
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public T get(Object key) {
        return map.get(fixKey(key));
    }

    /*
     * @see java.util.Map#put(?, ?)
     */
    @Override
    public T put(String key, T value) {
        return map.put(fixKey(key), value);
    }

    /*
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public T remove(Object key) {
        return map.remove(fixKey(key));
    }

    /*
     * @see java.util.Map#putAll(Map<?, ?>)
     */
    @Override
    public void putAll(Map<? extends String, ? extends T> t) {
        for (Map.Entry<? extends String, ? extends T> entry : t.entrySet())
            put(entry.getKey(), entry.getValue());
    }

    /*
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        map.clear();
    }

    /*
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<String> keySet() {
    	return new PropertyMapKeys();
    }

    /*
     * @see java.util.Map#values()
     */
    @Override
    public Collection<T> values() {
        return map.values();
    }

    /*
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        Map<String, T> result = new LinkedHashMap<String, T>(size());
        for (Map.Entry<PropertyMap.Key, T> e : map.entrySet()) {
            PropertyMap.Key key = e.getKey();
            if (key != null)
                result.put(key.toString(), e.getValue());
        }
        return result.entrySet();
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return map.toString();
    }
    
    /**
     * Returns true if this map is case sensitive
     * @return
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Key used for case sensitive maps
     * @author john
     * @model
     */
    private static class Key {
        protected final String key;
        protected int hashCode;
        protected Key(String key) {
            this.key = key.intern();
            this.hashCode = key.hashCode();
        }
        protected Key(String key, int hashCode) {
            this.key = key.intern();
            this.hashCode = hashCode;
        }
        /*
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashCode;
        }
        /*
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object ref) {
            String sref = (ref != null ? ref.toString() : null);
            if (key == null || sref == null)
                return (key == sref);
            return key.equals(sref);
        }
        /*
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return key;
        }
    }

    /**
     * Key used for case insensitive maps
     * @author john
     * @model
     */
    private static class CIKey extends Key {
    	protected CIKey(String key) {
            super(key, key.toLowerCase().hashCode());
        }
        /*
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object ref) {
            String sref = (ref != null ? ref.toString() : null);
            if (key == null || sref == null)
                return (key == sref);
            return key.equalsIgnoreCase(sref);
        }
        /*
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return key;
        }
    }
    
    /**
     * Used by deepEquals to compare two values of the same key in different maps
     * @param o1
     * @param o2
     * @return
     */
    private boolean equals(Object o1, Object o2) {
        if (o1 == null || o2 == null)
            return (o1 == o2);
        return o1.equals(o2);
    }
    
    /**
     * Compare the contents of this map to the contents of another. If
     * compareKeySet is true, then the number of keys must match
     * and each key in this map must also exist in the reference map. 
     * @param ref
     * @param compareKeySet
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean deepEquals(Map<String, Object> ref, boolean compareKeySet) {
        if (ref == null)
            return false;
        ref = getPropertyMap(ref);
        
        // If key sets must be compared, it implies the size must match
        if (compareKeySet && size() != ref.size())
            return false;
        
        Map.Entry<String,T>[] values = entrySet().toArray(new Map.Entry[size()]);
        for (Map.Entry<String,T> val : values) {
            // If key sets must be compared, it implies the key must also
            // exist in the reference map.
            if (compareKeySet && !ref.containsKey(val.getKey()))
                return false;
            
            if (!equals(val.getValue(), ref.get(val.getKey())))
                return false;
        }

        // If key sets must be compared, it implies no reverse checking is needed,
        // since the ref.containsKey() in the above loop as well as the size check
        // implies that there are no changes.
        if (compareKeySet)
            return true;
        
        values = ref.entrySet().toArray(new Map.Entry[size()]);
        for (Map.Entry<String,T> val : entrySet()) {
            if (!equals(val.getValue(), get(val.getKey())))
                return false;
        }
        
        return true;
    }

    /**
     * Class used for keySet() method
     * @author john
     *
     */
    private class PropertyMapKeys implements Set<String> {
    	private final Set<Key> set;
    	
    	public PropertyMapKeys() {
    		this.set = map.keySet();
    	}
    	
    	public boolean contains(Object key) {
            return set.contains(fixKey(key));
    	}

		@Override
		public int size() {
			return set.size();
		}

		@Override
		public boolean isEmpty() {
			return set.isEmpty();
		}

		@Override
		public Iterator<String> iterator() {
			final Iterator<Key> keys = set.iterator();
			return new Iterator<String>() {
				@Override
				public boolean hasNext() {
					return keys.hasNext();
				}
				@Override
				public String next() {
					Key key = keys.next();
					return (key != null ? key.key : null);
				}
				@Override
				public void remove() {
					keys.remove();
				}
			};
		}

		@Override
		public Object[] toArray() {
			Object[] result = set.toArray();
			if (result == null)
				return new Object[0];
			if (result.length == 0)
				return result;
			for (int i=0; i<result.length; i++) {
				if (result[i] instanceof Key)
					result[i] = ((Key) result[i]).key;
				else if (result[i] != null)
					result[i] = result[i].toString();
			}
			return result;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <X> X[] toArray(X[] a) {
			if (a == null) {
				X[] result = (X[]) toArray(); 
				return result;
			}
			Key[] tmp = set.toArray(new Key[a.length]);
			if (a.getClass().getComponentType() == String.class) {
				if (tmp == null || tmp.length == 0) {
					X[] result = (X[]) new String[0];
					return result;
				}
				if (tmp.length != a.length)
					a = (X[]) Array.newInstance(String.class, tmp.length);
				for (int i=0; i<a.length; i++)
					a[i] = (tmp[i] != null ? (X) tmp[i].key : null);
				return a;
			}
			return (X[]) DataParser.cast(tmp, a.getClass()) ;
		}

		@Override
		public boolean add(String e) {
			return set.add(fixKey(e));
		}

		@Override
		public boolean remove(Object o) {
			return set.remove(fixKey(o));
		}

		private Collection<? extends Key> toKeyList(Collection<?> c) {
			if (c == null || c.size() == 0)
				return new LinkedList<Key>();
			Collection<Key> result = new ArrayList<Key>(c.size());
			for (Object val : c) 
				result.add(fixKey(val));
			return result;
		}
		
		@Override
		public boolean containsAll(Collection<?> c) {
			return set.containsAll(toKeyList(c));
		}

		@Override
		public boolean addAll(Collection<? extends String> c) {
			return set.addAll(toKeyList(c));
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return set.retainAll(toKeyList(c));
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return set.removeAll(toKeyList(c));
		}

		@Override
		public void clear() {
			set.clear();
		}
    }
}
