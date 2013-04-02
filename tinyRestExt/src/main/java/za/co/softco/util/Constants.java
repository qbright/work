/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library 
 * Description: Library classes
 * 
 * Changelog  
 *  $Log: Constants.java,v $
 *  Revision 1.4  2007/12/23 17:03:08  remjohn
 *  Added constants
 *
 *  Revision 1.3  2007/12/22 19:37:34  remjohn
 *  Update classes to reflect latest functionality in Bester package
 *
 *  Revision 1.2  2007/09/07 07:18:30  remjohn
 *  Comment out constants not used in this library
 *
 *  Revision 1.1  2007/08/15 13:05:58  rembrink
 *  Added to CVS
 *
 *  Revision 1.1  2007/08/05 08:31:11  john
 *  Converted base package to za.co.softco
 *
 *  Revision 1.1  2007/06/14 10:21:24  goofyxp
 *  Split besterBase from bester library
 *
 *  Revision 1.5  2007/02/12 11:45:45  obelix
 *  Added constants for loading XML from a database table
 *
 *  Revision 1.4  2006/05/20 16:52:55  obelix
 *  Added XMLFolder and ResourceFolder properties to Arguments
 *
 *  Revision 1.3  2006/03/01 23:51:40  goofyxp
 *  Convert to Java 5 syntax
 *
 *  Revision 1.2  2006/01/10 15:53:55  goofyxp
 *  Added comments
 *  Added COPYRIGHT character
 *
 *  Created on 1-Jul-2005
 *******************************************************************************/
package za.co.softco.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author John Bester
 * 
 *         Frequently used constant values
 */
public class Constants {
    public enum ComponentSet {
        GRAPHICS_UNKNOWN, GRAPHICS_SWING, GRAPHICS_AWT, GRAPHICS_EWE
    }

    public enum OperatingSystem {
        OS_UNKNOWN, OS_UNIX, OS_LINUX, OS_MAC, OS_WINDOWS, OS_POCKETPC
    }

    public static final String NULL = "null";

    public static final String SRC_BASE_PACKAGE;
    public static final String SRC_BASE_FOLDER;
    public static final String IMAGE_FOLDER = "images";
    public static final String IMAGE_RESOURCE_FOLDER;

    public static final String PROPERTY_OS_NAME = "os.name";
    public static final String PROPERTY_VM_NAME = "java.vm.name";
    public static final String PROPERTY_VM_VENDOR = "java.vm.vendor";
    public static final String PROPERTY_VM_VERSION = "java.vm.version";
    public static final String PROPERTY_USER_HOME = "user.home";
    public static final String PROPERTY_TEMP_DIR = "java.io.tmpdir";

    public static final String DEFAULT_SPLASH = "images/splash";

    public static final String PROPERTY_XML_FOLDER = "XMLFolder";
    public static final String PROPERTY_XMLDB_TABLE = "XMLDBTable";
    public static final String PROPERTY_XMLDB_NAME = "XMLDBIdField";
    public static final String PROPERTY_XMLDB_TEXT = "XMLDBTextField";
    public static final String PROPERTY_RESOURCE_FOLDER = "#ResourceFolder";

    public static final String LINE_SEPERATOR;
    public static final char COPYRIGHT = '\u00A9';

    public static final String VM_NAME;
    public static final String VM_VENDOR;
    public static final float VM_VERSION; // vmVersion = Float.MIN_VALUE;
    public static final OperatingSystem OPERATING_SYSTEM;
    public static final ComponentSet COMPONENT_SET;
    
    public static final boolean UTF8_IS_DEFAULT;
    public static final Charset UTF8;
    public static final Charset ASCII;

    static {
        LINE_SEPERATOR = System.getProperty("line.separator");
        VM_VERSION = parseVersion(System.getProperty(PROPERTY_VM_VERSION));
        VM_VENDOR = System.getProperty(PROPERTY_VM_VENDOR).trim();
        VM_NAME = System.getProperty(PROPERTY_VM_NAME).trim();
        OPERATING_SYSTEM = getOperatingSystem();
        COMPONENT_SET = getComponentSet();

        String tmp = Constants.class.getPackage().getName();
        int pos = tmp.lastIndexOf('.');
        SRC_BASE_PACKAGE = tmp.substring(0, pos);
        SRC_BASE_FOLDER = SRC_BASE_PACKAGE.replaceAll("\\.", "/");
        IMAGE_RESOURCE_FOLDER = SRC_BASE_FOLDER + "/" + IMAGE_FOLDER;

        UTF8 = Charset.forName("UTF-8");
        ASCII = Charset.forName("US-ASCII");
        boolean defaultIsUTF8 = false;
        try {
            Charset cs = Charset.defaultCharset();
            defaultIsUTF8 = (UTF8 != null && cs != null && cs.equals(UTF8)); 
        } catch (UnsupportedCharsetException e) {
            // Ignore exception
            defaultIsUTF8 = false;
        }
        UTF8_IS_DEFAULT = defaultIsUTF8;
    }

    protected static float parseVersion(String version) {
        if (version == null)
            return 0;

        version = version.trim();

        int i = 0;
        int dec = 0;
        while (i < version.length()) {
            char c = version.charAt(i);
            if (c == '.')
                dec++;
            else if (c < '0' || c > '9')
                break;

            if (dec > 1)
                break;

            i++;
        }

        version = version.substring(0, Math.min(i, version.length() - 1));
        if (version.equals(""))
            return 0;

        return Float.parseFloat(version);
    }

    private static final ComponentSet getComponentSet() {
        String vendor = Constants.VM_VENDOR.toUpperCase();
        if (vendor.startsWith("SUN MICROSYSTEMS")) {
            if (Constants.VM_VERSION > 1.2)
                return ComponentSet.GRAPHICS_SWING;
            return ComponentSet.GRAPHICS_AWT;
        }
        if (vendor.startsWith("ORACLE")) {
            if (Constants.VM_VERSION > 1.2)
                return ComponentSet.GRAPHICS_SWING;
            return ComponentSet.GRAPHICS_AWT;
        }
        String vm = Constants.VM_NAME.toUpperCase();
        if (vm.startsWith("CREME"))
            return ComponentSet.GRAPHICS_AWT;
        else if (vm.startsWith("EWE"))
            return ComponentSet.GRAPHICS_EWE;
        return ComponentSet.GRAPHICS_UNKNOWN;
    }

    private static final OperatingSystem getOperatingSystem() {
        String os = System.getProperty(PROPERTY_OS_NAME).trim();
        if (os != null) {
            os = os.trim().toLowerCase();
            if (os.equals("linux"))
                return OperatingSystem.OS_LINUX;
            else if (os.startsWith("windows"))
                return OperatingSystem.OS_WINDOWS;
            else if (os.startsWith("mac"))
                return OperatingSystem.OS_MAC;
            else
                return OperatingSystem.OS_UNKNOWN;
        }
        return OperatingSystem.OS_UNKNOWN;
    }
}
