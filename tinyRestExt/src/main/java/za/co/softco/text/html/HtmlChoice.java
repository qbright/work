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


/**
 * @author john
 *
 */
public class HtmlChoice extends BasicHtmlElement<HtmlElement> {

    private Object value;
    
    public HtmlChoice(HtmlElement parent, String name, boolean multiple, Object defaultValue) {
        super(parent, "select");
        this.value = defaultValue;
        addAttribute("name", name);
        if (multiple)
            addTag("multiple");
    }

    public HtmlChoice addOption(Object key, String text) {
        boolean isDefault = (value != null && key != null && value.equals(key));
        HtmlOption option = new HtmlOption(this, key, text, isDefault);
        option.appendTo(html);
        return this;
    }

    public void postOnChange(boolean postOnChange) {
        if (postOnChange) {
            HtmlForm form = getForm();
            if (form != null && form.getName() != null)
                addAttribute("onchange", "javascript:document." + form.getName() + ".submit();");
        } else {
            removeAttribute("onchange");
        }
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
}
