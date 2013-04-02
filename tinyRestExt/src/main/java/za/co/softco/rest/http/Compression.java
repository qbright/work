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
 *  Created on 30 Dec 2009
 *******************************************************************************/
package za.co.softco.rest.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import za.co.softco.rest.RestException;
import za.co.softco.rest.content.DataFormat;
import za.co.softco.rest.model.CompressionLevel;
import za.co.softco.text.BooleanParser;
import za.co.softco.util.Utils;

/**
 * Enumeration of supported compression algorithms
 * @author john
 */
public enum Compression {
    GZIP(1, "gz", "application/gzipped") {
        @Override
        public OutputStream compress(OutputStream out, CompressionLevel level) {
            throw new IllegalStateException("Not implemented yet"); 
        }
        @Override
        public InputStream explode(InputStream in) {
            throw new IllegalStateException("Not implemented yet"); 
        }
    },
    DEFLATE(2, "deflated", "application/deflated") {
        @Override
        public OutputStream compress(OutputStream out, CompressionLevel level) {
            switch (level) {
            case NONE :
                return out;
            case FASTEST :
                return new DeflaterOutputStream(out, new Deflater(Deflater.BEST_SPEED));
            case NORMAL :
                return new DeflaterOutputStream(out);
            case BEST :
                return new DeflaterOutputStream(out, new Deflater(Deflater.BEST_COMPRESSION));
            default :
                return new DeflaterOutputStream(out);
            }
        }
        @Override
        public InputStream explode(InputStream in) {
            return new InflaterInputStream(in);
        }
    }, 
    ZIP(3, "zip", "application/zipped") {
        @Override
        public OutputStream compress(OutputStream out, CompressionLevel level) {
            throw new IllegalStateException("Not implemented yet"); 
        }
        @Override
        public InputStream explode(InputStream in) {
            throw new IllegalStateException("Not implemented yet"); 
        }
    }, 
    NONE(99, null, null) {
        @Override
        public OutputStream compress(OutputStream out, CompressionLevel level) {
            return out; 
        }
        @Override
        public InputStream explode(InputStream in) {
            return in; 
        }
    };

    private int priority;
    private final String extension;
    private final String mimeType;
    
    private Compression(int priority, String extension, String mimeType) {
        this.priority = priority;
        this.extension = extension;
        this.mimeType = mimeType;
    }
    
    public String addFileExtenstion(String filename) {
        if (extension != null && filename != null)
            return filename + "." + extension;
        return filename;
    }
    
    public String changeMimeType(String mimeType) {
        if (this.mimeType == null)
            return mimeType;
        if (mimeType == null)
            return this.mimeType;
        int pos = mimeType.lastIndexOf('/');
        if (pos >= 0) 
            return this.mimeType + "-" + mimeType.substring(pos+1);
        return this.mimeType + "-" + mimeType;
    }

    public abstract OutputStream compress(OutputStream out, CompressionLevel level);

    public abstract InputStream explode(InputStream in);
    
    public static Compression parse(String compression, boolean allowCompression) throws RestException {
        return parse(compression, (allowCompression ? Compression.DEFLATE : Compression.NONE));
    }
    
    public static Compression parse(String compression, Compression defaultCompression) throws RestException {
        if (defaultCompression == null || defaultCompression == NONE)
            return NONE;
        compression = Utils.normalize(compression);
        if (compression == null)
            return defaultCompression;
        Compression result = NONE;
        String[] parts = compression.trim().toUpperCase().split(Pattern.quote("+"));
        if (parts.length == 1) {
            try {
                Boolean b = new BooleanParser().cast(compression);
                if (b != null)
                    return (b.booleanValue() ? DEFLATE : NONE);
            } catch (ClassCastException e) {
                // Ignore exception
            }
        }
        for (String cmp : parts) {
            try {
                Compression tmp = Compression.valueOf(cmp.trim());
                if (tmp.priority <  result.priority)
                    result = tmp;
            } catch (IllegalArgumentException e) {
                // Might be something like "csv" or "xml"
                try {
                    DataFormat.valueOf(cmp.trim());
                } catch (IllegalArgumentException e2) {
                    throw new RestException(HttpConstants.HTTP_BAD_REQUEST, "Invalid compression/data format: " + cmp);
                }
            }
        }
        return result;
    }
}
