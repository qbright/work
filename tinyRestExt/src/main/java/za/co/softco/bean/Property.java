/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 05 Jun 2010
 *******************************************************************************/
package za.co.softco.bean;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

import za.co.softco.bean.BeanManager.Mode;
import za.co.softco.text.DataParser;

/**
 * Class used to keep property information
 * @author John Bester
 */
public class Property {
    private final PropertyDescriptor descriptor;
    protected final Class<?> type;

    /**
     * Constructor
     * @param descriptor
     * @throws IntrospectionException
     */
    public Property(PropertyDescriptor descriptor) throws IntrospectionException {
        if (descriptor == null)
            throw new IntrospectionException("No descriptor specified");

        this.descriptor = descriptor;
        this.type = descriptor.getPropertyType();
    }

    /**
     * Create a property editor
     * @param property
     * @return
     */
    public PropertyEditor getPropertyEditor(Object bean) {
        PropertyEditor result = descriptor.createPropertyEditor(bean);
        if (result != null)
            return result;
        return new DefaultPropertyEditor(descriptor, bean);
    }

    /**
     * Get property type
     * @return
     */
    public Class<?> getPropertyType() {
        return type;
    }

    /**
     * Get the read/write mode
     * @return
     */
    public Mode mode() {
        if (descriptor.getReadMethod() != null && descriptor.getWriteMethod() != null)
            return Mode.READ_WRITE;
        if (descriptor.getWriteMethod() != null)
            return Mode.WRITE_ONLY;
        return Mode.READ_ONLY;
    }

    /**
     * Get the value of the property
     * @return
     */
    public Object getValue(Object bean) {
        return getPropertyEditor(bean).getValue();
    }

    /**
     * Get the default value for primitive types
     * @return
     */
    public Object defaultValue() {
        if (type == Byte.TYPE)
            return new Byte((byte) 0);
        if (type == Short.TYPE)
            return new Short((short) 0);
        if (type == Integer.TYPE)
            return new Integer(0);
        if (type == Long.TYPE)
            return new Long(0);
        if (type == Float.TYPE)
            return new Float(0);
        if (type == Double.TYPE)
            return new Double(0);
        if (type == Character.TYPE)
            return new Character((char) 0);
        return null;
    }

    /**
     * Return text representation of value
     * @return
     */
    public String getAsText(Object bean) {
        return DataParser.format(getPropertyEditor(bean).getValue());
    }

    /**
     * Set the property value
     * @param value
     */
    public void setValue(Object bean, Object value) {
        getPropertyEditor(bean).setValue(value);
    }

    /**
     * Set value by parsing text
     * @param value
     */
    public void setAsText(Object bean, String value) {
        getPropertyEditor(bean).setAsText(value);
    }

    /**
     * Get property descriptor
     * @return
     */
    public PropertyDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Get property name
     * @return
     */
    public String getName() {
        return descriptor.getName();
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return descriptor.getName().toUpperCase().hashCode();
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object ref) {
        if (ref instanceof Property)
            return descriptor.getName().equalsIgnoreCase(((Property) ref).getName());
        /*
        if (ref instanceof PropertyDescriptor)
            return descriptor.getName().equalsIgnoreCase(((PropertyDescriptor) ref).getName());
        if (ref instanceof String)
            return descriptor.getName().equalsIgnoreCase((String) ref);
        if (ref instanceof StringBuffer)
            return descriptor.getName().equalsIgnoreCase((String) ref);
        */
        return false;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return descriptor.getName();
    }
}