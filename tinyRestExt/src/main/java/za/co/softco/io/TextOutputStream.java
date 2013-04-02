/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 01 Jun 2010
 *******************************************************************************/
package za.co.softco.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import za.co.softco.util.Constants;

/**
 * @author john
 *
 */
public class TextOutputStream extends FilterOutputStream {

    private final Charset encoding;
    private final String lineSeparator = Constants.LINE_SEPERATOR;

    public TextOutputStream(OutputStream out, Charset encoding) {
        super(out);
        this.encoding = (encoding != null ? encoding : Charset.defaultCharset());
    }
    
    public TextOutputStream(OutputStream out, String encoding) {
        this(out, (encoding != null ? Charset.forName(encoding) : Charset.defaultCharset()));
    }
    
    public TextOutputStream(OutputStream in) {
        this(in, Charset.defaultCharset());
    }

    public void write(String text) throws IOException {
        write(text.getBytes(encoding));
    }
    
    public void write(char[] text) throws IOException {
        write(new String(text));
    }
    
    public void write(char[] text, int offset, int length) throws IOException {
        write(new String(text, offset, length));
    }
    
    public void newLine() throws IOException {
        write(lineSeparator);
    }
}
