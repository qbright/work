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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;

/**
 * @author john
 *
 */
public class TextInputStream extends PushbackInputStream {

    private final Charset encoding;

    public TextInputStream(InputStream in, Charset encoding) {
        super(in);
        this.encoding = (encoding != null ? encoding : Charset.defaultCharset());
    }

    public TextInputStream(InputStream in, String encoding) {
        this(in, (encoding != null ? Charset.forName(encoding) : Charset.defaultCharset()));
    }

    public TextInputStream(InputStream in) {
        this(in, Charset.defaultCharset());
    }
    
    public String readLine() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int ch = 0;
        loop :
        while ((ch = read()) >= 0) {
            switch (ch) {
            case '\n' :
                break loop;
            case '\r' :
                int next = read();
                switch (next) {
                case '\r' :
                    unread(next);
                    break;
                case '\n' :
                    break;
                default :
                    if (next >= 0)
                        unread(next);
                    break;
                }
                break loop;
            default :
                if (ch < 0)
                    break loop;
                bytes.write(ch);
            }
        }
        byte[] data = bytes.toByteArray();
        if (ch < 0 && data.length == 0)
            return null;
        return new String(data, encoding);
    }
    
}
