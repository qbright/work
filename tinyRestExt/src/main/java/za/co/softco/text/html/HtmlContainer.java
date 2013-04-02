/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 04 Jun 2010
 *******************************************************************************/
package za.co.softco.text.html;

import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import za.co.softco.util.PropertyMap;

/**
 * @author john
 *
 */
public class HtmlContainer<P extends HtmlElement> extends BasicHtmlElement<P> {

    
    protected HtmlContainer(P parent) {
        super(parent);
    }

    protected HtmlContainer(P parent, String elementType) {
        super(parent, elementType);
    }

    public HtmlTable addTable(String width, int cellSpacing, int borderWidth, String... colWidths) {
        HtmlTable result = new HtmlTable(this, width, cellSpacing, borderWidth, colWidths);
        elements.add(result);
        return result;
    }
    
    public HtmlText addText(String text) {
        HtmlText result = new HtmlText(this, text);
        elements.add(result);
        return result;
    }
 
    @Override
    public <T extends HtmlElement> T addElement(T element) {
        super.addElement(element);
        return element;
    }
    
    public HtmlContainer<P> addLineBreak() {
        addText("<BR>");
        return this;
    }

    public HtmlInput addSubmitButton(String name, String caption) {
        return addElement(new HtmlInput(this, "submit", name, caption));
    }
    
    public HtmlInput addSubmitButton(String caption) {
        return addSubmitButton(null, caption);
    }
    
    public BasicHtmlElement<HtmlElement> addAnchor(String caption, String reference) {
        BasicHtmlElement<HtmlElement> result = addElement(new BasicHtmlElement<HtmlElement>(this, "a"));
        result.addAttribute("href", reference);
        result.html.append(caption);
        return result;
    }
    
    public BasicHtmlElement<HtmlElement> addAnchor(String caption, URL reference) {
        return addAnchor(caption, reference.toString());
    }
    
    public BasicHtmlElement<HtmlElement> addAnchor(String caption, URI reference) {
        return addAnchor(caption, reference.toString());
    }
    
    public HtmlInput addTextField(String name) {
        return addElement(new HtmlInput(this, "text", name, null));
    }
    
    public HtmlInput addHiddenField(String name) {
        return addElement(new HtmlInput(this, "hidden", name, null));
    }
    
    public HtmlChoice addChoice(String name, boolean multiple, Object defaultKey) {
        return addElement(new HtmlChoice(this, name, multiple, defaultKey));
    }
    
    public HtmlInput addCheckbox(String name) {
        return addElement(new HtmlCheckbox(this, name));
    }
    
    public HtmlInput addButton(String name, String action) {
        HtmlInput result = addElement(new HtmlInput(this, "button", name, null));
        result.addAttribute("onclick", action);
        return result;
    }
    
    public void clear() {
        for (HtmlElement el : elements) {
            if (el instanceof HtmlInput) {
                HtmlInput input = (HtmlInput) el;
                input.setValue(null);
            } else if (el instanceof HtmlContainer<?>) {
                ((HtmlContainer<?>) el).clear();
            }
        }
    }
    
    public HtmlInput getInputComponent(String componentName) {
        for (HtmlElement el : elements) {
            if (el instanceof HtmlInput) {
                HtmlInput input = (HtmlInput) el;
                String name = input.getName();
                if (name.equalsIgnoreCase(componentName))
                    return (HtmlInput) el;
            } else if (el instanceof HtmlContainer<?>) {
                HtmlInput result = ((HtmlContainer<?>) el).getInputComponent(componentName);
                if (result != null)
                    return result;
            }
        }
        return null;
    }
    
    public HtmlContainer<P> setValue(String componentName, Object value) {
        return setValue(getInputComponent(componentName), value);
    }
    
    public HtmlContainer<P> setValue(HtmlInput component, Object value) {
        return setValue(getDocument(), component, value);
    }
    
    private HtmlContainer<P> setValue(HtmlDocument doc, HtmlInput component, Object value) {
        if (component == null)
            return this;
        component.setValue(value);
        if (doc != null && getName() != null && component.getName() != null) {
            String componentPath = "document." + getName() + "." + component.getName();
            doc.addOnLoadJavaScriptLine(componentPath + ".value = " + HtmlUtils.escapeJavaScriptValue(value) + ";");
        }
        return this;
    }
    
    public HtmlContainer<P> setValues(Map<String,Object> values) {
        if (values == null)
            return this;

        HtmlDocument doc = getDocument();
        for (HtmlElement el : elements) {
            if (el instanceof HtmlInput) {
                HtmlInput input = (HtmlInput) el;
                String name = input.getName();
                if (values.containsKey(name)) 
                    setValue(doc, input, values.get(name));
            } else if (el instanceof HtmlContainer<?>) {
                ((HtmlContainer<?>) el).setValues(values);
            } 
        }
        return this;
    }

    public Object getValue(HtmlInput component) {
        return (component != null ? component.getValue() : null);
    }

    public Object getValue(String componentName) {
        return getValue(getInputComponent(componentName));
    }

    public Map<String,Object> getValues() {
        Map<String,Object> result = new PropertyMap<Object>(new LinkedHashMap<String,Object>());
        for (HtmlElement el : elements) {
            if (el instanceof HtmlInput) {
                HtmlInput input = (HtmlInput) el;
                result.put(input.getName(), input.getValue());
            } else if (el instanceof HtmlContainer<?>) {
                Map<String,Object> tmp = ((HtmlContainer<?>) el).getValues();
                if (tmp != null)
                    result.putAll(tmp);
            }
        }
        return result;
    }
    
}
