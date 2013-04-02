/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 01 May 2010
 *******************************************************************************/
package za.co.softco.text.html;

import java.util.Arrays;
import java.util.Stack;

/**
 * @author john
 *
 */
public class ElementStack extends Stack<String> {
    private static final long serialVersionUID = 1679001666232156577L;

    public String allowTopElement(String... allowed) {
        String top = (size() > 0 ? peek() : null);
        if (allowed == null || allowed.length == 0)
            return top;
        if (top == null)
            throw new IllegalStateException("Element stack is empty, but expected one of " + Arrays.toString(allowed));
        top = top.trim();
        for (String el : allowed) {
            if (el == null)
                continue;
            if (el.trim().equalsIgnoreCase(top))
                return top;
        }
        throw new IllegalStateException("Top of element stack is <" + top + ">, but expected one of " + Arrays.toString(allowed));
    }
    
    public String failTopElement(String... notAllowed) {
        String top = (size() > 0 ? peek() : null);
        if (notAllowed == null || notAllowed.length == 0)
            return top;
        if (top == null)
            return top;
        top = top.trim();
        for (String el : notAllowed) {
            if (el == null)
                continue;
            if (el.trim().equalsIgnoreCase(top))
                throw new IllegalStateException("Top of element stack is <" + top + ">, but is not alloed");
        }
        return top;
    }
    
}
