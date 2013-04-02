/*******************************************************************************
 * Copyright (C) Bester Consulting 2010. All Rights reserved.
 * This file may be distributed under the Softco Share License
 * 
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 20 Nov 2009
 *******************************************************************************/
package za.co.softco.rest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Writer that counts the characters written.
 * @author john
 * @deprecated - Bytes should be counted, not characters
 */
@Deprecated
public class CountingBufferedWriter extends BufferedWriter {

	private long written = 0;
	
	public CountingBufferedWriter(Writer target) {
		super(target);
	}
	
	/*
	 * @see java.io.BufferedWriter#write(int)
	 */
	@Override
    public void write(int c) throws IOException {
    	super.write(c);
    	written++;
    }

	/*
	 * @see java.io.BufferedWriter#write(char[], int, int)
	 */
	@Override
    public void write(char cbuf[], int off, int len) throws IOException {
    	super.write(cbuf, off, len);
    	written += len;
	}

	/*
	 * @see java.io.BufferedWriter#write(java.lang.String, int, int)
	 */
	@Override
    public void write(String s, int off, int len) throws IOException {
    	super.write(s, off, len);
    	written += len;
    }

	/**
	 * Return the number of characters written to the stream
	 * @return
	 */
	public long getWrittenCharacterCount() {
		return written;
	}
}
