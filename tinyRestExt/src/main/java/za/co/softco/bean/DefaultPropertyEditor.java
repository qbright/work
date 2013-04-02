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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import za.co.softco.text.DataParser;

public class DefaultPropertyEditor implements PropertyEditor {
    private final PropertyDescriptor property;
    private final Object bean;
    private List<PropertyChangeListener> listeners;

    public DefaultPropertyEditor(PropertyDescriptor property, Object bean) {
        this.property = property;
        this.bean = bean;
    }

    private void error(Method method, Exception error) {
        System.err.println(bean.getClass() + "." + property.getName() + ": " + method.getName() + " failed");
        System.err.println("  - " + error.getMessage());
        System.err.println("  - Method: " + method);
    }

    @Override
    public void setValue(Object value) {
        Method set = property.getWriteMethod();
        if (set == null)
            return;

        try {
            Class<?> c = set.getParameterTypes()[0];
            if (listeners != null && listeners.size() > 0) {
                Object oldValue = getValue();
                set.invoke(bean, new Object[] { DataParser.cast(value, c) });
                PropertyChangeEvent ev = new PropertyChangeEvent(bean, property.getName(), oldValue, value);
                for (PropertyChangeListener listener : listeners)
                    listener.propertyChange(ev);
            } else {
                set.invoke(bean, DataParser.cast(value, c));
            }
        } catch (IllegalArgumentException e) {
            error(set, e);
        } catch (IllegalAccessException e) {
            error(set, e);
        } catch (InvocationTargetException e) {
            error(set, e);
        } catch (NullPointerException e) {
            error(set, e);
        }
    }

    @Override
    public Object getValue() {
        Method get = property.getReadMethod();
        if (get == null)
            return null;
        try {
            return get.invoke(bean);
        } catch (IllegalArgumentException e) {
            error(get, e);
            return null;
        } catch (IllegalAccessException e) {
            error(get, e);
            return null;
        } catch (InvocationTargetException e) {
            error(get, e);
            return null;
        } catch (NullPointerException e) {
            error(get, e);
            return null;
        }
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        gfx.drawString(getAsText(), box.x, box.y + (box.height * 2 / 3));
    }

    @Override
    public String getJavaInitializationString() {
        return null;
    }

    @Override
    public String getAsText() {
        Object result = getValue();
        return (result != null ? result.toString() : null);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String old = getAsText();

        if (text == null) {
            if (old != null)
                setValue(null);
            return;
        }

        if (old != null && text.equals(old))
            return;

        Method set = property.getWriteMethod();
        if (set == null)
            return;

        Class<?>[] params = set.getParameterTypes();
        if (params.length != 1)
            throw new IllegalArgumentException("Set method must have one parameter");

        try {
            set.invoke(bean, new Object[] { DataParser.parse(text, params[0]) });
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Could not parse text: \"" + text + "\" (" + e.getMessage() + ")");
        }
    }

    @Override
    public String[] getTags() {
        return null;
    }

    @Override
    public Component getCustomEditor() {
        return null;
    }

    @Override
    public boolean supportsCustomEditor() {
        return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null)
            listeners = new LinkedList<PropertyChangeListener>();
        listeners.add(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listeners != null)
            listeners.remove(listener);
    }
}
