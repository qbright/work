/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 06 Jun 2010
 *******************************************************************************/
package za.co.softco.text.html;

/**
 * @author john
 *
 */
public class HtmlJavaScript implements HtmlElement {

    private final HtmlElement parent;
    private final StringBuilder script = new StringBuilder();

    public HtmlJavaScript(HtmlElement parent) {
        this.parent = parent;
    }
    
    public void append(JavaScriptFunction function) {
        script.append(function.toString());
    }
    
    public void append(String text) {
        script.append(text);
    }
    
    /*
     * @see za.co.softco.text.html.HtmlElement#appendTo(java.lang.StringBuilder)
     */
    @Override
    public void appendTo(StringBuilder result) {
        result.append("<script type=\"text/javascript\" language=\"javascript\">\r\n<!--\r\n");
        result.append(script);
        result.append("-->\r\n</script>\r\n");
    }

    /*
     * @see za.co.softco.text.html.HtmlElement#close()
     */
    @Override
    public HtmlElement close() {
        return parent;
    }

    /*
     * @see za.co.softco.text.html.HtmlElement#getName()
     */
    @Override
    public String getName() {
        return null;
    }

    /*
     * @see za.co.softco.text.html.HtmlElement#getParent()
     */
    @Override
    public HtmlElement getParent() {
        return parent;
    }

    /*
     * @see za.co.softco.text.html.HtmlElement#getType()
     */
    @Override
    public String getType() {
        return "script";
    }

}
