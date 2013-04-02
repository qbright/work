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
package za.co.softco.text.html;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

import za.co.softco.bean.DefaultBeanManager;
import za.co.softco.util.Utils;

/**
 * @author john
 *
 */
public class HtmlUtils {

    public static String escapeAttributeValue(String value) {
        value = Utils.normalize(value);
        if (value == null)
            return "\"\"";
        boolean javaScript = value.trim().toLowerCase().startsWith("javascript:");
        StringBuilder result = new StringBuilder();
        if (javaScript)
            result.append("'");
        else
            result.append('"');
        String tmp = value.toString();
        for (char c : tmp.toCharArray()) {
            switch (c) {
            case '\r' :
                result.append("\\r");
                break;
            case '\n' :
                result.append("\\n");
                break;
            case '\t' :
                result.append("\\t");
                break;
            case '"' :
                if (javaScript)
                    result.append('"');
                else
                    result.append("'");
                break;
            case '\'' :
                if (javaScript)
                    result.append("\\'");
                else
                    result.append('"');
                break;
            default :
                result.append(c);
            }
        }
        if (javaScript)
            result.append("'");
        else
            result.append('"');
        return result.toString();
    }

    public static String escapeJavaScriptValue(Object value) {
        if (value == null)
            return "null";
        if (value instanceof Number)
            return value.toString();
        if (value instanceof Boolean)
            return value.toString().toLowerCase();
    
        StringBuilder result = new StringBuilder();
        result.append("\"");
        String tmp = value.toString();
        for (char c : tmp.toCharArray()) {
            switch (c) {
            case '\r' :
                result.append("\\r");
                break;
            case '\n' :
                result.append("\\n");
                break;
            case '\t' :
                result.append("\\t");
                break;
            case '"' :
                result.append("\\\"");
                break;
            default :
                result.append(c);
            }
        }
        result.append("\"");
        return result.toString();
    }

    /**
     * Append an attribute
     * @param name
     * @param value
     */
    public static void appendAttributeTo(StringBuilder result, String name, Object value) {
        if (name == null || value == null)
            return;
        if (value instanceof CharSequence) 
            value = escapeAttributeValue(value.toString());
        else
            value = "\"" + value + "\"";
        result.append(' ');
        result.append(name);
        if (value != BasicHtmlElement.TAG) {
            result.append("=");
            result.append(value);
        }
    }

    public static void appendJavaScriptTo(StringBuilder result, String... scripts) {
        if (scripts == null || scripts.length == 0)
            return;
        result.append("<script type=\"text/javascript\" language=\"javascript\">\r\n<!--\r\n");
        for (String script : scripts) {
            if (script != null) {
                result.append(script);
                if (!script.trim().endsWith(";"))
                    result.append(";");
            }
            result.append("\r\n");
        }
        result.append("-->\r\n</script>\r\n"); 
    }

    public static String formatTimeForHttpHeader(Date time) {
        if (time == null)
            return null;
        DateFormat fmt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        //fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return fmt.format(time);
    }

    public static Date getImmediateExipiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -2);
        return cal.getTime();
    }

    public static String buildJavaScriptArray(String variableName, Object... values) {
        variableName = Utils.normalize(variableName);
        StringBuilder result = new StringBuilder();
        if (variableName != null) {
            result.append("var ");
            result.append(variableName);
            result.append(" = ");
        }
        result.append("new Array(");
        if (values != null) {
            int i = 0;
            for (Object value : values) {
                if (i++ > 0)
                    result.append(", ");
                result.append(escapeJavaScriptValue(value));
            }
        }
        result.append(");");
        return result.toString();
    }

    @SafeVarargs
    public static <T> String buildJavaScriptArrayFromBeans(String variableName, String fieldName, T... beans) {
        fieldName = Utils.normalize(fieldName);
        if (fieldName == null)
            throw new IllegalArgumentException("Field name argument is required");
        
        if (beans == null)
            return buildJavaScriptArray(variableName); 
        
        Object[] values = new Object[beans.length];
        try {
            for (int i=0; i<values.length; i++) 
                values[i] = new DefaultBeanManager<T>(beans[i]).getValue(fieldName);
            return buildJavaScriptArray(variableName, values);
        } catch (IntrospectionException e) {
            Logger.getLogger(HtmlUtils.class).error("Build array for field \"" + fieldName + "\" failed", e);
            return buildJavaScriptArray(variableName, values);
        }
    }

    public static JavaScriptFunction buildJavaScriptFunction(String functionName, String... parameterNames) {
        return new JavaScriptFunction(functionName,  parameterNames);
    }

    private static String extractErrorMessageFromTag(String orginal, String lower, int start, String tag) {
        tag = tag.trim().toLowerCase();
        int pos = lower.indexOf("<" + tag);
        while (pos >= 0) {
            int end = lower.indexOf("</" + tag + ">", pos);
            if (end > pos) {
                String result = orginal.substring(pos, end);
                int ndx = result.indexOf('>');
                result = Utils.normalize(result.substring(ndx+1));
                if (result != null) {
                    int exp = result.toLowerCase().indexOf("exception:");
                    while (exp >= 0) {
                        result = Utils.normalize(result.substring(exp + "exception:".length()));
                        exp = result.toLowerCase().indexOf("exception:");
                    }
                    if (result != null)
                        return result;
                }
            }
            pos = lower.indexOf("<" + tag, pos);
        }
        return null;
    }
    
    public static String extractHtmlErrorMessage(String html) {
        if (html == null)
            return "Unknown error";
        String lower = html.toLowerCase();
        int pos = lower.indexOf("<html"); 
        if (pos < 0) 
            return html;
        if (pos > 0) {
            lower = lower.substring(pos);
            html = html.substring(pos);
        }
        String result = extractErrorMessageFromTag(html, lower, 0, "pre");
        if (result != null)
            return result;
        result = extractErrorMessageFromTag(html, lower, 0, "title");
        if (result != null)
            return result;
        result = extractErrorMessageFromTag(html, lower, 0, "h1");
        if (result != null)
            return result;
        result = extractErrorMessageFromTag(html, lower, 0, "h2");
        if (result != null)
            return result;
        result = extractErrorMessageFromTag(html, lower, 0, "h3");
        if (result != null)
            return result;
        result = extractErrorMessageFromTag(html, lower, 0, "body");
        if (result != null)
            return result;
        return html;
    }

    public static String extractHtmlErrorMessage(Exception exception) {
        return extractHtmlErrorMessage(exception.getMessage());
    }
    
    public static Document parseSwingDocument(InputStream in) throws IOException {
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument(); 
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        try {
            kit.read(in, doc, 0);
        } catch (BadLocationException e) {
            throw Utils.cast(e, IOException.class);
        }
        return doc;
    }

    public static Document parseSwingDocument(Reader in) throws IOException {
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument(); 
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        try {
            kit.read(in, doc, 0);
        } catch (BadLocationException e) {
            throw Utils.cast(e, IOException.class);
        }
        return doc;
    }

    public static Document parseSwingDocument(String text) throws IOException {
        return parseSwingDocument(new StringReader(text));
    }
}
