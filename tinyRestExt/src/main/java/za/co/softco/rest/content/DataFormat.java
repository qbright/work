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
package za.co.softco.rest.content;

import static za.co.softco.rest.http.HttpConstants.*;

import java.util.regex.Pattern;

import za.co.softco.rest.RestException;
import za.co.softco.rest.http.Compression;
import za.co.softco.rest.http.HttpConstants;

/**
 * Enumeration of data formats known to the REST server. This allows
 * standard parsers to be configured in order to uncompress and
 * parse request bodies automatically before handing it over to 
 * the request handler.
 * @author john
 */
public enum DataFormat {
    XML(2, MIME_XML, MIME_DEFLATED_XML, ".xml"), 
    CSV(1, MIME_CSV, MIME_DEFLATED_CSV, ".csv"),
    HTML(3, MIME_HTML, null, ".html"),
    PDF(4, MIME_PDF, null, ".pdf"),
    PS(5, MIME_PS, MIME_DEFLATED_PS, ".ps"),
    TEXT(98, MIME_PLAIN, null, ".txt"), 
    BINARY(99, MIME_BINARY, null, ".bin");
    
    private final int priority;
    private final String mimeType;
    private final String deflatedMimeType;
    private final String extension;
    
    private DataFormat(int priority, String mimeType, String deflatedMimeType, String extension) {
        this.priority = priority;
        this.mimeType = mimeType;
        this.deflatedMimeType = deflatedMimeType;
        this.extension = (extension != null ? extension : "");
    }
    
    /**
     * Return mime type
     * @param compressed
     * @return
     */
    public String getMimeType(boolean compressed) {
        if (compressed && deflatedMimeType != null)
            return deflatedMimeType;
        return mimeType;
    }
    
    /**
     * Return the default file extension
     * @return
     */
    public String getExtension() {
        return extension;
    }
    
    public static DataFormat parse(String format, DataFormat defaultFormat) throws RestException {
        if (format == null)
            return defaultFormat;
        format = format.trim();
        if (format.length() == 0)
            return defaultFormat;
        DataFormat result = (defaultFormat != null ? defaultFormat : XML);
        for (String cmp : format.trim().toUpperCase().split(Pattern.quote("+"))) {
            try {
                DataFormat tmp = DataFormat.valueOf(cmp.trim());
                if (tmp.priority <  result.priority)
                    result = tmp;
            } catch (IllegalArgumentException e) {
                // Might be something like "zip" or "gzip"
                try {
                    Compression.valueOf(cmp.trim());
                } catch (IllegalArgumentException e2) {
                    throw new RestException(HttpConstants.HTTP_BAD_REQUEST, "Invalid compression/data format: " + cmp);
                }
            }
        }
        return result;
    }
}
