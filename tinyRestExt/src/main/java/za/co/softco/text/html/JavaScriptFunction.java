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

/**
 * @author john
 *
 */
public class JavaScriptFunction {

    private final String name;
    private final String[] parameters;
    private final StringBuilder body = new StringBuilder();
    
    public JavaScriptFunction(String name, String... parameterNames) {
        this.name = Utils.normalize(name);
        this.parameters = Utils.normalize(parameterNames);
        if (this.name == null)
            throw new IllegalArgumentException("Function name is required");
        int len1 = (parameterNames != null ? parameterNames.length : 0);
        int len2 = (this.parameters != null ? this.parameters.length : 0);
        if (len1 != len2)
            throw new IllegalArgumentException("Illegal parameter name list (all names must be valid)");
    }
    
    public JavaScriptFunction append(String... scriptLines) {
        if (scriptLines == null) {
            body.append("\r\n");
            return this;
        }
        for (String line : scriptLines) {
            if (line != null)
                body.append(line);
            body.append("\r\n");
        }
        return this;
    }
    
    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("function ");
        result.append(name);
        result.append("(");
        if (parameters != null) {
            for (int i=0; i<parameters.length; i++) {
                if (i > 0)
                    result.append(", ");
                result.append(parameters[i]);
            }
        }
        result.append(") {\r\n");
        result.append(body);
        result.append("}\r\n");
        return result.toString();
    }
}
