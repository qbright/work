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

import za.co.softco.util.Utils;

public class HtmlForm extends HtmlPanel {

    protected HtmlForm(HtmlElement parent, String name) {
        this(parent, name, "POST", null);
    }

    protected HtmlForm(HtmlElement parent, String name, String method, String onLoad) {
        super(parent, "FORM");
        addAttribute("NAME", Utils.normalize(name));
        addAttribute("METHOD", method);
        HtmlJavaScript script = addElement(new HtmlJavaScript(this));
        JavaScriptFunction submit = new JavaScriptFunction(getName() + "_submit");
        submit.append("document." + getName() + ".submit();");
        script.append(submit);
        onLoad = "javascript:doOnLoad();";
        addAttribute("ONLOAD", onLoad);
    }

    public String getSubmitAction() {
        return "javascript:" + getName() + "_submit();";
    }
}
