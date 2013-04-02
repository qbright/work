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

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author john
 *
 */
public class CounterOuputStream extends OutputStream {

    private final OutputStream out;
    private long length;
    
    public CounterOuputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void  write(byte[] b) throws IOException {
        if (b != null)
            length += b.length;
        if (out != null)
            out.write(b);
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        length += len;
        if (out != null)
            out.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        length++;
        if (out != null)
            out.write(b);
    }
    
    public long length() {
        return length;
    }
}
