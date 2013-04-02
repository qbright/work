/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Revision 1.2  2007/12/23 17:05:15  remjohn
 *  Fixed comments
 *
 *  Revision 1.1  2007/12/22 19:35:33  remjohn
 *  Added to CVS
 *
 *  Revision 1.2  2007/11/26 08:07:40  john
 *  Added descriptions
 *
 *  Revision 1.1  2007/09/07 09:11:32  john
 *  Refactor: Moved to za.co.softco.bean
 *
 *  Revision 1.1  2007/06/06 08:25:56  obelix
 *  Added to CVS
 *
 *  Created on 05 Jun 2007
 *******************************************************************************/
package za.co.softco.text.xml;

import java.io.BufferedWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import za.co.softco.bean.DefaultBeanManager;
import za.co.softco.util.Utils;

/**
 * Write a bean as XML
 * @author john
 * @model
 */
public class XMLBeanWriter {

    private final Map<Class<?>, XMLBeanWriter.Factory<?>> factories = new HashMap<Class<?>, XMLBeanWriter.Factory<?>>();
    private final XMLBeanWriter.Factory<?> NULL_FACTORY = new NullFactory();
    private final XMLBeanWriter.Factory<?> COLLECTION_FACTORY = new CollectionFactory();
    private final XMLBeanWriter.Factory<?> MAP_FACTORY = new MapFactory();
    private final XMLBeanWriter.Factory<?> COMPOSITE_FACTORY = new CompositeFactory();
    private final BufferedWriter writer;

    {
        factories.put(Boolean.class, new SimpleFactory("Boolean"));
        factories.put(String.class, new SimpleFactory("String"));
        factories.put(StringBuffer.class, new SimpleFactory("StringBuffer"));
        factories.put(StringBuilder.class, new SimpleFactory("StringBuilder"));
        factories.put(Byte.class, new SimpleFactory("Byte"));
        factories.put(Short.class, new SimpleFactory("Short"));
        factories.put(Integer.class, new SimpleFactory("Integer"));
        factories.put(Long.class, new SimpleFactory("Long"));
        factories.put(Float.class, new SimpleFactory("Float"));
        factories.put(Double.class, new SimpleFactory("Double"));
        factories.put(BigDecimal.class, new SimpleFactory("BigDecimal"));
        factories.put(BigInteger.class, new SimpleFactory("BigInteger"));
    }

    /**
     * Constructor
     * @param writer
     */
    public XMLBeanWriter(Writer writer) {
        this.writer = new BufferedWriter(this.writer);
    }

    /**
     * Get a factory
     * @param <T>
     * @param elementClass
     * @return
     */
    private <T> Factory<?> getFactory(Class<T> elementClass) {
        if (elementClass == null)
            throw new IllegalArgumentException("Element class must be specified");
        Factory<?> result = factories.get(elementClass);
        if (result != null)
            return result;

        Class<? super T> superClass = elementClass.getSuperclass();
        if (superClass == null)
            return null;
        return getFactory(superClass);
    }

    /**
     * Get a factory
     * @param <T>
     * @param element
     * @return
     */
    private <T> Factory<?> getFactory(T element) {
        if (element == null)
            return NULL_FACTORY;

        if (element instanceof Map<?,?>)
            return MAP_FACTORY;

        if (element instanceof Collection<?>)
            return COLLECTION_FACTORY;

        Factory<?> result = getFactory(element.getClass());
        if (result != null)
            return result;
        return COMPOSITE_FACTORY;
    }

    /**
     * Write out an element
     * @param writer
     * @param name
     * @param element
     * @param prefix
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected void writeElement(BufferedWriter writer, String name, Object element, String prefix) throws Exception {
        @SuppressWarnings("rawtypes")
        Factory factory = getFactory(element);
        name = Utils.normalize(name);
        factory.write(writer, name, element, prefix);
    }

    /**
     * Write the XML for an object
     * @param bean
     * @throws Exception
     */
    public void write(Object bean) throws Exception {
        writeElement(writer, null, bean, "");
        writer.flush();
    }

    /**
     * Register a factory
     * @param clazz
     * @param factory
     */
    public void registerFactory(Class<?> clazz, Factory<?> factory) {
        factories.put(clazz, factory);
    }

    /**
     * Override this factory to write different kinds of objects
     * @author john
     * @model
     */
    public interface Factory<T> {
        public void write(BufferedWriter writer, String name, T object, String prefix) throws Exception;
    }

    public class NullFactory implements Factory<Object> {
        @Override
        public void write(BufferedWriter writer, String name, Object object, String prefix) throws Exception {
            writer.write(prefix);
            if (name != null)
                writer.write("<NULL Name=\"" + name + "\"/>");
            else
                writer.write("<NULL/>");
            writer.newLine();
        }
    }

    public class CompositeFactory implements Factory<Object> {
        @Override
        public void write(BufferedWriter writer, String name, Object object, String prefix) throws Exception {
            writer.write(prefix);
            if (name != null)
                writer.write("<BEAN Class=\"" + object.getClass().getName() + "\">");
            else
                writer.write("<BEAN Name=\"" + name + "\" Class=\"" + object.getClass().getName() + "\">");
            writer.newLine();
            String indented = prefix + "  ";
            Map<String, Object> properties = new DefaultBeanManager<Object>(object).getProperties();
            for (Map.Entry<String, Object> prop : properties.entrySet()) {
                writeElement(writer, prop.getKey(), prop.getValue(), indented);
            }
            writer.write(prefix);
            writer.write("</BEAN>");
            writer.newLine();
        }
    }

    public class ArrayFactory<A> implements Factory<A[]> {
        @Override
        public void write(BufferedWriter writer, String name, A[] object, String prefix) throws Exception {
            writer.write(prefix);
            if (name != null)
                writer.write("<ARRAY Name=\"" + name + "\">");
            else
                writer.write("<ARRAY>");
            writer.newLine();
            String indented = prefix + "  ";
            for (A element : object)
                writeElement(writer, name, element, indented);
            writer.write(prefix);
            writer.write("</ARRAY>");
            writer.newLine();
        }
    }

    public class CollectionFactory implements Factory<Collection<?>> {
        @Override
        public void write(BufferedWriter writer, String name, Collection<?> object, String prefix) throws Exception {
            writer.write(prefix);
            if (name != null)
                writer.write("<COLLECTION Name=\"" + name + "\" Class=\"" + object.getClass().getName() + "\">");
            else
                writer.write("<COLLECTION>");
            writer.newLine();
            String indented = prefix + "  ";
            for (Object element : object)
                writeElement(writer, name, element, indented);
            writer.write(prefix);
            writer.write("</COLLECTION>");
            writer.newLine();
        }
    }

    public class MapFactory implements Factory<Map<?, ?>> {
        @Override
        public void write(BufferedWriter writer, String name, Map<?, ?> object, String prefix) throws Exception {
            writer.write(prefix);
            if (name != null)
                writer.write("<COLLECTION Name=\"" + name + "\" Class=\"" + object.getClass().getName() + "\">");
            else
                writer.write("<COLLECTION>");
            writer.newLine();

            String indented = prefix + "  ";
            String indented2 = indented + "  ";
            String indented3 = indented2 + "  ";
            for (Map.Entry<?, ?> element : object.entrySet()) {
                writer.write(indented);
                writer.write("<ENTRY>");
                writer.newLine();
                writer.write(indented2);
                writer.write("<KEY>");
                writer.newLine();
                writeElement(writer, name, element, indented3);
                writer.write(indented2);
                writer.write("</KEY>");
                writer.newLine();
                writer.write(indented);
                writer.write("</ENTRY>");
                writer.newLine();
            }
            writer.write(prefix);
            writer.write("</COLLECTION>");
            writer.newLine();
        }
    }

    public class SimpleFactory implements Factory<Object> {
        private final String elementType;

        public SimpleFactory(String elementType) {
            this.elementType = elementType.toUpperCase().trim();
        }

        @Override
        public void write(BufferedWriter writer, String name, Object object, String prefix) throws Exception {
            writer.write(prefix);
            if (name != null)
                writer.write("<" + elementType + " Name=\"" + name + "\" Value=\"" + object + "\"/>");
            else
                writer.write("<" + elementType + " Value=\"" + object + "\"/>");
            writer.newLine();
            writer.newLine();
        }
    }
}
