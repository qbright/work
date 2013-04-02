/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *
 *  Created on 2006/11/08
 *******************************************************************************/
package za.co.softco.bean;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import za.co.softco.text.DataParser;
import za.co.softco.util.PropertyMap;

/**
 * Default implementation of BeanManaber interface 
 * @author john
 * @param <T>
 */
public class DefaultBeanManager<T> implements BeanManager<T> {
    public static final Set<Mode> READABLE = new HashSet<Mode>();
    public static final Set<Mode> WRITEABLE = new HashSet<Mode>();
    public static final Set<Mode> READ_ONLY = new HashSet<Mode>();
    public static final Set<Mode> WRITE_ONLY = new HashSet<Mode>();
    public static final Set<Mode> READ_WRITE_FILTER = new HashSet<Mode>();
    
    private final T bean;
    private final boolean silent;
    private final Set<Mode> accessFilter;
    private final Set<String> filter;
    private final Set<Property> properties = new LinkedHashSet<Property>();

    static {
        READ_WRITE_FILTER.add(Mode.READ_WRITE);
        READABLE.add(Mode.READ_ONLY);
        READABLE.add(Mode.READ_WRITE);
        WRITEABLE.add(Mode.READ_WRITE);
        WRITEABLE.add(Mode.WRITE_ONLY);
        READ_ONLY.add(Mode.READ_ONLY);
        WRITE_ONLY.add(Mode.READ_WRITE);
    }
    
    /**
     * Constructor
     * @param bean
     * @throws IntrospectionException
     */
    public DefaultBeanManager(T bean) throws IntrospectionException {
        this(bean, READ_WRITE_FILTER);
    }
    
    /**
     * Constructor
     * @param bean
     * @param silent
     * @throws IntrospectionException
     */
    public DefaultBeanManager(T bean, boolean silent) throws IntrospectionException {
        this(bean, READ_WRITE_FILTER, silent);
    }
    
    /**
     * Constructor
     * @param bean
     * @param accessFilter
     * @throws IntrospectionException
     */
    public DefaultBeanManager(T bean, Set<Mode> accessFilter) throws IntrospectionException {
        this(bean, accessFilter, false);
    }
    
    /**
     * Constructor
     * @param bean
     * @param accessFilter
     * @param silent
     * @throws IntrospectionException
     */
    public DefaultBeanManager(T bean, Set<Mode> accessFilter, boolean silent) throws IntrospectionException {
        this.bean = bean;
        this.filter = getFilter(bean);
        this.accessFilter = (accessFilter != null ? accessFilter : READ_WRITE_FILTER);
        this.silent = silent;
        this.properties.addAll(getProperties(bean.getClass()));
    }

    /**
     * Get a set of properties for a specific bean class
     * @param beanClass
     * @return
     * @throws IntrospectionException
     */
    public static Set<Property> getProperties(Class<?> beanClass) throws IntrospectionException {
        return getProperties(beanClass, false);
    }
    
    /**
     * Get a set of properties for a specific bean class
     * @param beanClass
     * @param silent
     * @return
     * @throws IntrospectionException
     */
    public static Set<Property> getProperties(Class<?> beanClass, boolean silent) throws IntrospectionException {
        if (beanClass == null)
            throw new IllegalArgumentException("Both beanClass and propertyName arguments is required"); 
        Set<Property> result = new LinkedHashSet<Property>(20);
        PropertyDescriptor[] prop = Introspector.getBeanInfo(beanClass).getPropertyDescriptors();
        for (PropertyDescriptor p : prop) {
            try {
                result.add(new Property(p));
            } catch (IntrospectionException e) {
                if (!silent)
                    System.err.println(beanClass + "." + p.getName() + ": " + e.getMessage());
            }
        }
        return result;
    }
    
    /**
     * Get a set of properties for a specific bean class
     * @param beanClass
     * @param propertyName
     * @return
     * @throws IntrospectionException
     */
    public static Property getProperty(Class<?> beanClass, String propertyName) throws IntrospectionException {
        if (beanClass == null  || propertyName == null)
            throw new IllegalArgumentException("Both beanClass and propertyName arguments is required"); 
        PropertyDescriptor[] prop = Introspector.getBeanInfo(beanClass).getPropertyDescriptors();
        for (PropertyDescriptor p : prop) {
            if (p.getName().equals(propertyName))
                return new Property(p);
        }
        for (PropertyDescriptor p : prop) {
            if (p.getName().equalsIgnoreCase(propertyName))
                return new Property(p);
        }
        throw new IntrospectionException("Property \"" + propertyName + "\" does not exist in " + beanClass.getName()); 
    }
    
    private static Set<String> getFilter(Object bean) {
        if (!(bean instanceof BeanFilter))
            return null;

        Set<String> result = new LinkedHashSet<String>();
        for (String prop : ((BeanFilter) bean).getPropertyFilter())
            if (prop != null)
                result.add(prop.trim().toLowerCase());

        return result;
    }

    /*
     * @see za.co.softco.bean.BeanManager#getBeanObject()
     */
    @Override
    public T getBeanObject() {
        return bean;
    }

    /*
     * @see za.co.softco.bean.BeanManager#getPropertyWrappers()
     */
    @Override
    public Set<Property> getPropertyWrappers() {
        return properties;
    }
    
    /**
     * Get a specific property by name
     * @param name
     * @return
     * @throws IntrospectionException
     */
    public Property getProperty(String name) throws IntrospectionException {
        for (Property prop : properties)
            if (prop.getName().equalsIgnoreCase(name.trim()))
                return prop;
        if (name.toLowerCase().startsWith("is")) {
            name = name.substring(2);
            for (Property prop : properties) 
                if ((prop.type == Boolean.class || prop.type == boolean.class) && name.equalsIgnoreCase(prop.getName()))
                    return prop;
        }
        throw new IntrospectionException("Property \"" + name + "\" not found");
    }

    /**
     * Get the value class of a specific property
     * @param name
     * @return 
     * @throws IntrospectionException
     */
    @Override
    public Class<?> getPropertyClass(String name) throws IntrospectionException {
        return getProperty(name).getPropertyType();
    }

    /**
     * Get
     * @param property
     * @param mode
     * @return
     */
    private boolean allowProperty(PropertyDescriptor property, Mode mode) {
        if (property.getName() == null)
            return false;
        if (filter != null && !filter.contains(property.getName().trim().toLowerCase()))
            return false;
        if (DataParser.canParse(property.getPropertyType()))
            return true;
        
        if (mode == null)
            return true;
        
        switch (mode) {
        case WRITE_ONLY :
            return (property.getWriteMethod() != null);
        case READ_ONLY :
            return (property.getReadMethod() != null);
        case READ_WRITE :
            return (property.getWriteMethod() != null &&  property.getReadMethod() != null);
        }
        return false;
    }

    /**
     * Allow property to be added to a list of properties
     * @param property
     * @return
     */
    private boolean allowProperty(Property property, Mode mode) {
        if (accessFilter == null || accessFilter.size() == 0)
            return true;
        if (!accessFilter.contains(property.mode()))
            return false;
        return allowProperty(property.getDescriptor(), mode);
    }

    /*
     * @see za.co.softco.bean.BeanManager#getPropertyTypes()
     */
    @Override
    public Map<String, Class<?>> getPropertyTypes() {
        Map<String, Class<?>> result = new PropertyMap<Class<?>>(new LinkedHashMap<String, Class<?>>(properties.size()));
        for (Property prop : properties) {
            result.put(prop.getName(), prop.getPropertyType());
        }
        return result;
    }
    
    /*
     * @see za.co.softco.bean.BeanManager#getProperties()
     */
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> result = new PropertyMap<Object>(new LinkedHashMap<String, Object>(properties.size()));
        for (Property prop : properties) {
            if (allowProperty(prop, null)) {
                result.put(prop.getName(), prop.getValue(bean));
            }
        }
        if (!result.containsKey("_text"))
            result.put("_text", (bean != null ? bean.toString() : null));
        return result;
    }

    /*
     * @see za.co.softco.bean.BeanManager#getProperties(Mode)
     */
    @Override
    public Map<String, Object> getProperties(Mode mode) {
        Map<String, Object> result = new PropertyMap<Object>(new LinkedHashMap<String, Object>(properties.size()));
        for (Property prop : properties) {
            if (allowProperty(prop, mode)) {
                result.put(prop.getName(), prop.getValue(bean));
            }
        }
        return result;
    }

    /*
     * @see za.co.softco.bean.BeanManager#getTextProperties(Mode)
     */
    @Override
    public Map<String, String> getTextProperties() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (Property prop : properties) {
            if (allowProperty(prop, null))
                result.put(prop.getName(), prop.getAsText(bean));
        }
        return result;
    }

    /*
     * @see za.co.softco.bean.BeanManager#getTextProperties(Mode)
     */
    @Override
    public Map<String, String> getTextProperties(Mode mode) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (Property prop : properties) {
            if (allowProperty(prop, mode))
                result.put(prop.getName(), prop.getAsText(bean));
        }
        return result;
    }

    /*
     * @see za.co.softco.bean.BeanManager#setProperties(java.util.Map)
     */
    @Override
    public void setProperties(Map<String, Object> properties) {
        for (Map.Entry<String, Object> prop : properties.entrySet())
            if (contains(prop.getKey()))
                try {
                    Property p = getProperty(prop.getKey());
                    if (prop.getValue() != null)
                        p.setValue(bean, prop.getValue());
                    else
                        p.setValue(bean, p.defaultValue());
                } catch (IntrospectionException e) {
                    if (!silent)
                        Logger.getLogger(DefaultBeanManager.class).error(e);
                }
    }

    /*
     * @see za.co.softco.bean.BeanManager#setTextProperties(java.util.Map)
     */
    @Override
    public void setTextProperties(Map<String, String> properties) {
        for (Map.Entry<String, String> prop : properties.entrySet())
            if (contains(prop.getKey()))
                try {
                    getProperty(prop.getKey()).setAsText(bean, prop.getValue());
                } catch (IntrospectionException e) {
                    if (!silent)
                        Logger.getLogger(DefaultBeanManager.class).error(e);
                }
    }

    /*
     * @see za.co.softco.bean.BeanManager#createClone()
     */
    @Override
    public T createClone() throws Exception {
        @SuppressWarnings("unchecked")
        T result = (T) getBeanObject().getClass().newInstance();
        new DefaultBeanManager<T>(result).setProperties(getProperties());
        return result;
    }

    private Property findProperty(String name) {
        if (name == null)
            throw new IllegalArgumentException("A valid property name is expected");
        for (Property p : properties)
            if (name.equalsIgnoreCase(p.getName()))
                return p;
        if (name.toLowerCase().startsWith("is")) {
            name = name.substring(2);
            for (Property p : properties) 
                if ((p.type == Boolean.class || p.type == boolean.class) && name.equalsIgnoreCase(p.getName()))
                    return p;
        }
        return null;
    }
    
    /*
     * @see za.co.softco.bean.BeanManager#contains(java.lang.String)
     */
    @Override
    public boolean contains(String name) {
        return (findProperty(name) != null);
    }

    /*
     * @see za.co.softco.bean.BeanManager#isReadable(java.lang.String)
     */
    @Override
    public boolean isReadable(String name) {
        Property p = findProperty(name);
        if (p == null)
            return false;
        Mode m = p.mode();
        return (m != null ? m.isReadable() : false);
    }

    /*
     * @see za.co.softco.bean.BeanManager#isWritable(java.lang.String)
     */
    @Override
    public boolean isWritable(String name) {
        Property p = findProperty(name);
        if (p == null)
            return false;
        Mode m = p.mode();
        return (m != null ? m.isEditable() : false);
    }
    
    /*
     * @see za.co.softco.bean.BeanManager#mode(java.lang.String)
     */
    @Override
    public Mode mode(String name) throws IntrospectionException {
        return getProperty(name).mode();
    }

    /*
     * @see za.co.softco.bean.BeanManager#getValue(java.lang.String)
     */
    @Override
    public Object getValue(String name) throws IntrospectionException {
        return getProperty(name).getValue(bean);
    }

    /*
     * @see za.co.softco.bean.BeanManager#getAsText(java.lang.String)
     */
    @Override
    public String getAsText(String name) throws IntrospectionException {
        return getProperty(name).getAsText(bean);
    }

    /*
     * @see za.co.softco.bean.BeanManager#setValue(java.lang.String, java.lang.Object)
     */
    @Override
    public void setValue(String name, Object value) throws IntrospectionException {
        getProperty(name).setValue(bean, value);
    }

    /*
     * @see za.co.softco.bean.BeanManager#setAsText(java.lang.String, java.lang.String)
     */
    @Override
    public void setAsText(String name, String value) throws IntrospectionException {
        getProperty(name).setAsText(bean, value);
    }

}
