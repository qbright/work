/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: BeanManager.java,v $
 *  Revision 1.3  2007/12/22 19:37:06  remjohn
 *  Update classes to reflect latest functionality in Bester package
 *
 *  Revision 1.2  2007/10/05 00:48:23  remjohn
 *  Refactor to avoid warnings
 *
 *  Revision 1.1  2007/08/15 13:05:58  rembrink
 *  Added to CVS
 *
 *  Revision 1.1  2007/08/05 08:31:11  john
 *  Converted base package to za.co.softco
 *
 *  Revision 1.1  2007/06/14 10:19:43  goofyxp
 *  Split besterBase from bester library
 *
 *  Revision 1.4  2006/11/07 12:58:38  goofyxp
 *  Added comments and formatted code
 *
 *  Created on 2006/11/07
 *******************************************************************************/
package za.co.softco.bean;

import java.beans.IntrospectionException;
import java.util.Map;
import java.util.Set;

/**
 * Implement this interface to manage beans
 * @author John Bester
 */
public interface BeanManager<T> {
    public enum Mode {
        READ_ONLY, WRITE_ONLY, READ_WRITE;
        public boolean isEditable() {
            switch (this) {
            case READ_ONLY :
                return false;
            case WRITE_ONLY :
            case READ_WRITE :
                return true;
            default :
                return false;
            }
        }

        public boolean isReadable() {
            switch (this) {
            case READ_ONLY :
            case READ_WRITE :
                return true;
            case WRITE_ONLY :
                return false;
            default :
                return false;
            }
        }
    }
    
    /**
     * Return the object managed by this manager
     * @return
     */
    public T getBeanObject();
    
    /**
     * Iterate throw bean properties and build up a map of property name -> java class
     * @return
     */
    public Map<String, Class<?>> getPropertyTypes();

    /**
     * Return a set of property wrappers which can be used to access a property
     * @return
     */
    public Set<Property> getPropertyWrappers();
    
    /**
     * Iterate throw bean properties and build up a map of property name -> value pairs
     * @return
     */
    public Map<String, Object> getProperties();

    /**
     * Iterate throw bean properties and build up a map of property name -> value (text representation) pairs
     * @return
     */
    public Map<String, String> getTextProperties();

    /**
     * Iterate throw bean properties and build up a map of property name -> value pairs
     * @param mode
     * @return
     */
    public Map<String, Object> getProperties(Mode mode);

    /**
     * Iterate throw bean properties and build up a map of property name -> value (text representation) pairs
     * @param mode
     * @return
     */
    public Map<String, String> getTextProperties(Mode mode);

    /**
     * Get the property class of a specific property
     * @param name
     * @return
     * @throws IntrospectionException
     */
    public Class<?> getPropertyClass(String name) throws IntrospectionException;

    /**
     * Set properties at once
     * @param properties
     * @throws IntrospectionException
     */
    public void setProperties(Map<String, Object> properties) throws IntrospectionException;

    /**
     * Set properties at once, using text representation
     * @param properties
     */
    public void setTextProperties(Map<String, String> properties);

    /**
     * Return a clone of the bean object
     * @return
     */
    public T createClone() throws Exception;
    
    /**
     * Determines if the bean contains a specific property
     * @param name
     * @return
     */
    public boolean contains(String name);

    /**
     * Determines if the bean contains a specific readable property
     * @param name
     * @return
     */
    public boolean isReadable(String name);

    /**
     * Determines if the bean contains a specific writable property
     * @param name
     * @return
     */
    public boolean isWritable(String name);

    /**
     * Return mode (READ-ONLY / WRITE-ONLY / READ_WRITE) of property
     * @param name
     * @return
     * @throws IntrospectionException
     */
    public Mode mode(String name) throws IntrospectionException;

    /**
     * Get the value of a specific property
     * @param name
     * @return
     * @throws IntrospectionException
     */
    public Object getValue(String name) throws IntrospectionException;

    /**
     * Get the text represention of the value of a specific property
     * @param name
     * @return
     * @throws IntrospectionException
     */
    public String getAsText(String name) throws IntrospectionException;

    /**
     * Set a property value
     * @param name
     * @param value
     * @throws IntrospectionException
     */
    public void setValue(String name, Object value) throws IntrospectionException;

    /**
     * Set a property value using the text representation
     * @param name
     * @param value
     * @throws IntrospectionException
     */
    public void setAsText(String name, String value) throws IntrospectionException;

}
