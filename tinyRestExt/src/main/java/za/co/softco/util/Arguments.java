/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: Arguments.java,v $
 *  Revision 1.6  2007/12/22 19:37:34  remjohn
 *  Update classes to reflect latest functionality in Bester package
 *
 *  Revision 1.5  2007/11/29 13:04:57  remjohn
 *  Deleted empty line
 *
 *  Revision 1.4  2007/11/26 10:02:52  remjohn
 *  Added checkHelpParameter()
 *
 *  Revision 1.3  2007/10/05 00:49:05  remjohn
 *  Refactor to avoid warnings
 *
 *  Revision 1.2  2007/09/07 07:38:33  remjohn
 *  Use PropertyMap in stead of MapProxy
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
 *  Revision 1.28  2007/04/14 14:23:30  goofyxp
 *  Added default language property
 *
 *  Revision 1.27  2007/03/04 23:01:47  obelix
 *  Change getEntries() to return a case insensitive map
 *
 *  Revision 1.26  2007/02/12 11:45:32  obelix
 *  Added properties for loading XML from a database table
 *
 *  Revision 1.25  2007/02/02 05:59:49  goofyxp
 *  Added upgrade notice constants and methods
 *
 *  Revision 1.24  2006/12/28 10:21:29  obelix
 *  Added DbPort parameter
 *  Added <?> to avoid generics warning
 *
 *  Revision 1.23  2006/11/28 13:07:37  obelix
 *  Fix problem
 *
 *  Revision 1.21  2006/11/16 13:16:01  goofyxp
 *  Improve best path for config path
 *
 *  Revision 1.20  2006/10/22 06:59:01  obelix
 *  Added getApplicationPath()
 *
 *  Revision 1.19  2006/09/04 13:04:00  obelix
 *  Move connection and database management to SQL class
 *
 *  Revision 1.18  2006/09/04 11:56:51  obelix
 *  Add function to set default database
 *
 *  Revision 1.17  2006/08/31 16:45:09  obelix
 *  Added user login
 *
 *  Revision 1.16  2006/08/24 10:38:01  obelix
 *  Set aliases in all instances
 *
 *  Revision 1.15  2006/06/14 08:35:42  goofyxp
 *  Renamed log functions to use the commons logging standards
 *
 *  Revision 1.14  2006/06/06 14:48:23  obelix
 *  Improve updating of existing file
 *
 *  Revision 1.13  2006/05/25 14:59:27  hugo
 *  Improve getXmlFolder()
 *
 *  Revision 1.12  2006/05/20 16:52:41  obelix
 *  Added XMLFolder and ResourceFolder properties
 *
 *  Revision 1.11  2006/05/12 08:31:27  goofyxp
 *  Fix bug when setting config file
 *
 *  Revision 1.10  2006/05/07 20:27:48  goofyxp
 *  Change SQLConstants to a class and import static
 *  Implement Refreshable interface
 *
 *  Revision 1.9  2006/04/27 08:48:27  goofyxp
 *  Added getBoolean(String, String, boolean)
 *
 *  Revision 1.8  2006/04/10 19:11:29  goofyxp
 *  Add proxy
 *
 *  Revision 1.7  2006/03/13 15:46:00  obelix
 *  Added serialVersionID's
 *
 *  Revision 1.6  2006/03/10 07:37:58  goofyxp
 *  Added getRepositories
 *  Clean up code
 *
 *  Revision 1.5  2006/03/01 23:51:40  goofyxp
 *  Convert to Java 5 syntax
 *
 *  Revision 1.4  2006/02/26 10:57:02  goofyxp
 *  Override getEntries() to include all repositories
 *
 *  Revision 1.3  2006/02/01 20:46:19  goofyxp
 *  Added comments
 *
 *  Revision 1.2  2005/12/06 12:08:36  goofyxp
 *  Add comments
 *  Add function to set config filename
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.util;

import static za.co.softco.util.Constants.OPERATING_SYSTEM;
import static za.co.softco.util.Constants.PROPERTY_RESOURCE_FOLDER;
import static za.co.softco.util.Constants.PROPERTY_XML_FOLDER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import za.co.softco.text.BooleanParser;
import za.co.softco.text.DataParser;
import za.co.softco.text.IntegerParser;
import za.co.softco.text.xml.XMLUtils;


/**
 * @author John Bester
 * 
 * This class is used to easily access command line parameters and configuration
 * file parameters from anywhere inside an application.
 * 
 * To use the getDatabase() function, the following parameters must be
 * specified: -dbe : Database engine (eg. MSSQL, ODBC, -srv : IP address or
 * server name -tcp : TCP/IP port number -db : Database name -usr : Username
 * -pwd : User password
 */
public class Arguments {
    private static final Map<String,Interpreter> interpreters = new PropertyMap<Interpreter>(10);
    // Default refresh time is 60 seconds
    public static final String FLAG_DEBUG = "debug";
    public static final String PARAM_CONFIG = "cfg";
    public static final String PARAM_SPLASH = "splash";
    public static final String PARAM_SPL_TEXTCOL = "splashtextcolor";
    public static final String PARAM_ICON = "icon";
    /* @deprecated  Use Desktop.getDesktop().browse(String) in stead of specified browser */
    public static final String PARAM_PROXY = "proxy";
    public static final String PARAM_PROXY_USER = "proxyusername";
    public static final String PARAM_PROXY_PASSWORD = "proxypassword";
    public static final String PARAM_LOCALE = "locale";
    public static final String NAME_CONFIG = "$CONFIG_FILE";
    public static final String NAME_LOGFILE = "$LOG_FILE";
    public static final String NAME_ERRORFILE = "$ERROR_FILE";
    public static final String NAME_APPLICATION = "$APPLICATION";
    public static final String SAVE_CONFIG = "INSTALL";
    public static final String SHOW_ERROR = "ERROR";
    protected static final String EXT_CONFIG = ".cfg";
    protected static final String EXT_LOGFILE = ".log";
    protected static final String EXT_ERRORFILE = ".err";
    protected static final String DEFAULT_FILE = "arguments";
    protected static final String LINEFEED = "\r\n";
    protected static final String XML_INSTANCE = "instance";
    protected static final int DEFAULT_PROXY_PORT = 3128;
    private static Class<?> MAIN_CLASS;
    private static Arguments instance;
    private static boolean initialized = false;
    private static String xmlFolderName;
    private static File xmlFolder;

    private final Map<String,Object> props = new PropertyMap<Object>();
    private String configFile;
    private Proxy proxy;
    // Default setting for reading user configuration
    private boolean readUserConfig = false;
    
    static {
        MAIN_CLASS = findMainClass();
    }
    
    /**
     * Constructor for Parameters.
     */
    public Arguments() {
        super();
    }

    /**
     * Constructor for Parameters.
     * 
     * @param defaults
     */
    public Arguments(String[] defaults) {
        super();
        setProperties(defaults);
    }

    /**
     * Constructor for Parameters.
     * 
     * @param defaults
     */
    public Arguments(String[][] defaults) {
        super();
        setProperties(defaults);
    }

    /**
     * Remove all internal properties
     * 
     * @return
     */
    public Arguments purge() {
        remove(NAME_APPLICATION);
        remove(NAME_CONFIG);
        remove(SAVE_CONFIG);
        return this;
    }

    /**
     * Returns true if a line starts with #
     * 
     * @param line
     * @return
     */
    public static boolean isComment(String line) {
        if (line == null)
            return false;
        line = line.trim();
        return (line.startsWith("#") || line.startsWith("'") || line.startsWith("//"));
    }

    /**
     * Get a configuration parameter
     * @param name
     * @return
     */
    public Object getObject(String name) {
        return props.get(name);
    }

    /**
     * Set a configuration parameter
     * @param name
     * @param value
     */
    public void setObject(String name, Object value) {
        if (name == null)
            return;
        props.put(name.trim(), value);
    }
    
    /**
     * Get a configuration parameter as a string
     * @param name
     * @return
     */
    public String getString(String name) {
        return Utils.normalize(DataParser.format(getObject(name)));
    }
    
    /**
     * Set a string configuration parameter
     * @param name
     * @param value
     */
    public void setString(String name, String value) {
        setObject(name, value);
    }
    
    /**
     * Return an integer configuration parameter
     * @param name
     * @return
     */
    public int getInt(String name) {
        return IntegerParser.toInt(getObject(name));
    }
    
    /**
     * Return an boolean configuration parameter
     * @param name
     * @return
     */
    public boolean getBoolean(String name) {
        return BooleanParser.toBoolean(getObject(name));
    }
    
    public void setBoolean(String name, boolean value) {
        setObject(name, Boolean.valueOf(value));
    }
    
    /**
     * Get an int value - if the result is 0, then defaultValue is returned
     * 
     * @param name
     * @param description
     * @param defaultValue
     * @return
     */
    public int getInt(String name, String description, int defaultValue) {
        Object check = getObject(name);
        int result;
        boolean isDefault = (check == null);
        if (isDefault)
            result = defaultValue;
        else
            result = getInt(name);

        displaySetting(name, new Integer(result), description, isDefault);
        return result;
    }

    /**
     * Get a string value - if the result is 0, then defaultValue is returned
     * 
     * @param name
     * @param description
     * @param defaultValue
     * @return
     */
    public String getString(String name, String description, String defaultValue) {
        String result = Utils.normalize(getString(name));
        boolean isDefault = (result == null);
        if (isDefault)
            result = defaultValue;

        displaySetting(name, result, description, isDefault);
        return result;
    }

    /**
     * Get a string value - if the result is 0, then defaultValue is returned
     * 
     * @param name
     * @param description
     * @param defaultValue
     * @return
     */
    public String getPassword(String name, String description, String defaultValue) {
        String result = Utils.normalize(getString(name));
        boolean isDefault = (result == null);
        if (isDefault)
            result = defaultValue;

        StringBuilder disp = new StringBuilder();
        if (result != null && result.length()>5) {
            for (int i=0; i<Math.max(result.length(), 5); i++)
                disp.append('*');
        } else {
            disp.append("*****");
        }
        
        displaySetting(name, disp.toString(), description, isDefault);
        return result;
    }

    /**
     * Get a boolean value with a default value
     * @param name
     * @param description
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(String name, String description, boolean defaultValue) {
        boolean result = getBoolean(name);
        boolean isDefault = !props.containsKey(name);
        if (isDefault)
            result = defaultValue;

        displaySetting(name, (result ? "yes" : "no"), description, isDefault);
        return result;
    }
    
    /**
     * Log a setting using the standard Log module
     */
    public static void displaySetting(String name, Object value, String description, boolean isDefault) {
        StringBuffer text = new StringBuffer(name);
        while (text.length() < 18)
            text.append(' ');

        text.append(" = ");
        if (value != null)
            text.append(value.toString());
        else
            text.append("<undefined>");

        if (isDefault)
            text.append(" (*)");

        if (description != null) {
            while (text.length() < 35)
                text.append(' ');

            text.append(" // ");
            text.append(description);
            Logger.getLogger(Arguments.class).info(text.toString());
        }
    }

    /*
     * Do tasks that must be done before system is shut down
     */
    public static void shutdown() {
        if (instance != null)
            instance.dispose();
    }

    /*
     * Do tasks that must be done before system is shut down
     */
    public void dispose() {
        if (getBoolean(SAVE_CONFIG))
            saveConfigFile(getString(NAME_CONFIG));
    }

    /*
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    /**
     * Save configuration settings to a streamed object file
     * 
     * @param filename
     */
    public boolean saveDefaultsFile(String filename) {
        if (filename == null)
            return false;

        Arguments copy = new Arguments();
        copy.props.putAll(props);

        try {
            System.out.print("Saving defaults to '" + filename + "'...");
            new ObjectOutputStream(new FileOutputStream(filename)).writeObject(copy);
            System.out.println("done.");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("failed. (" + e.getMessage() + ")");
            return false;
        } catch (IOException e) {
            System.out.println("failed. (" + e.getMessage() + ")");
            return false;
        }
    }

    /**
     * @see za.co.softco.util.Properties#isInternalKey(String)
     * @param key
     * @return
     */
    private boolean isInternalKey(String key) {
        key = key.trim().toUpperCase();
        return (key.equals(NAME_APPLICATION) || key.equals(NAME_CONFIG) || key.equals(NAME_ERRORFILE) || key
                .equals(NAME_LOGFILE));
    }

    /**
     * Get the user specific configuration folder
     * @param application
     * @param section
     * @return
     */
    public static File getUserConfigurationFolder(String application, String section) {
        String tmp = System.getProperty("user.home");
        if (tmp == null)
            System.err.println("Home folder not configured - cannot initialize settings folder");

        File home = new File((tmp != null ? tmp : "."));
        if (!home.isDirectory()) {
            System.err.println("Home folder does not exist - cannot initialize settings folder (" + tmp + ")");
            return null;
        }

        application = Utils.normalize(application);
        section = Utils.normalize(section);
        
        File result;
        switch (za.co.softco.util.Constants.OPERATING_SYSTEM) {
        case OS_UNIX:
        case OS_LINUX:
            result = new File(home, ".softco");
            if (application != null)
                application = application.toLowerCase();
            break;
        case OS_POCKETPC:
        case OS_UNKNOWN:
            result = new File(new File("."), "settings");
            break;
        case OS_WINDOWS:
            result = new File(home, "AppData/Local/Softco/" + application + "/Settings");
            break;
        default :
            result = new File(new File("."), "settings");
        }
        if (!result.isDirectory())
            result.mkdirs();
        if (application != null) {
            result = new File(result, application);
            result.mkdirs();
        }
        if (section != null) {
            result = new File(result, section);
            result.mkdirs();
        }
        if (!result.isDirectory()) {
            System.err.println("Settings folder does not exist and could not be created (" + result + ")");
            return null;
        }
        return result;
    }
    
    /**
     * Save configuration settings to the default configuration file.
     */
    public boolean saveConfigFile() {
        String cfg = getConfigFilename();
        if (cfg == null)
            return false;
        if (!isXmlConfiguration())
            return saveConfigFile(cfg);
        try {
            DocumentBuilder builder  = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            UpdatedXmlConfig tmpcfg = buildUpdatedXmlConfigFile(cfg);
            if (tmpcfg == null || !tmpcfg.changed)
                return false;
            Document newcfg = tmpcfg.config;
            Document oldcfg = readXmlConfiguration(false, true);
            Document changes = builder.newDocument();
            XMLUtils.extractChanges(changes, null, oldcfg.getDocumentElement(), newcfg.getDocumentElement(), "instance", "name");
            String usrcfg = getUserConfigFilename();
            if (usrcfg != null) {
                File userFile = new File(usrcfg);
                if (userFile.isFile()) {
                    try {
                        Document usercfg = builder.parse(userFile);
                        Document userdbs = builder.newDocument();
                        Element userRoot = usercfg.getDocumentElement();
                        String rootName = userRoot.getNodeName();
                        Element root = userdbs.createElement(rootName);
                        for (Element db : XMLUtils.getXPathElements(usercfg, "/" + rootName + "/Database")) {
                            Element cp = userdbs.createElement(db.getNodeName());
                            XMLUtils.copyAttributes(db, cp);
                            XMLUtils.copyStructure(db, cp);
                        }
                        Document merged = builder.newDocument();
                        XMLUtils.merge(merged, null, root, changes.getDocumentElement(), "instance", "name");
                        changes = merged;
                    } catch (XPathExpressionException e) {
                        System.err.println("Error enumerating database elements: " + e.getMessage());
                        e.printStackTrace();
                    } catch (SAXException e) {
                        Logger.getLogger(Arguments.class).error(e);
                        userFile.delete();
                    }
                }
                XMLUtils.writeXml(userFile, changes);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Saving configuration failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (ParserConfigurationException e) {
            System.err.println("Building configuration failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (TransformerFactoryConfigurationError e) {
            System.err.println("Writing of user configuration failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (TransformerException e) {
            System.err.println("Writing of user configuration failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateSettings(Document config) {
        try {
            Element rootElement = XMLUtils.getXPathElement(config, "/Configuration");
            Map<String,Element> elements = new PropertyMap<Element>();
            for (Element el : XMLUtils.getChildren(rootElement)) {
                elements.put(el.getTagName(), el);
            }
            
            boolean changed = false;
            for (Map.Entry<String, Object> setting : props.entrySet()) {
                String key = setting.getKey();
                Object val = setting.getValue();
                if (key == null || val == null)
                    continue;
                String name = key.toString();
                if (!XMLUtils.isValidElementName(name))
                    continue; // Only simple settings are saved
                Element element = elements.get(name);
                if (element == null) {
                    element = config.createElement(name);
                    rootElement.appendChild(element);
                } else {
                    Collection<Element> children = XMLUtils.getChildren(element);
                    if (children != null && children.size() > 0)
                        continue; // Only simple settings are saved
                }
                XMLUtils.writeText(element, DataParser.format(val));
                changed = true;
            }
            return changed;
        } catch (XPathException e) {
            Logger.getLogger(Arguments.class).error("Could not update configuration settings: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing XML configuration file
     * 
     * @param filename
     * @throws IOException 
     */
    public UpdatedXmlConfig buildUpdatedXmlConfigFile(String filename) {
        if (filename == null)
            filename = getConfigFilename();

        if (filename == null)
            return null;

        if (!filename.toLowerCase().endsWith(".xml"))
            throw new IllegalArgumentException("Filename must end with \".xml\"");
        
        if (!new File(filename).exists()) {
            try {
                return new UpdatedXmlConfig(true, buildXmlConfiguration());
            } catch (ParserConfigurationException e) {
                Logger.getLogger(Arguments.class).error(e);
                return null;
            }
        }
        
        try {
            Document cfg = readXmlConfiguration(false);
            boolean changed = updateSettings(cfg);
            return new UpdatedXmlConfig(changed, cfg);
        } catch (IOException e) {
            Logger.getLogger(Arguments.class).error(e);
            return null;
        } catch (TransformerFactoryConfigurationError e) {
            Logger.getLogger(Arguments.class).error(e);
            return null;
        }
    }
    
    /**
     * Build an XML document with the current configuration
     * @return
     * @throws ParserConfigurationException
     */
    private Document buildXmlConfiguration() throws ParserConfigurationException {
        Document cfg = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = cfg.createElement("Configuration");
        cfg.appendChild(rootElement);
        boolean languageSet = false;
        for (Map.Entry<String, Object> setting : props.entrySet()) {
            Object key = setting.getKey();
            Object val = setting.getValue();
            if (!(key instanceof String) || val == null)
                continue;
            if (key.toString().equalsIgnoreCase("language") && languageSet)
                continue;
            String name = key.toString();
            if (name.contains(".") || name.startsWith("$") || name.startsWith("#"))
                continue; // Only simple settings are saved
            try {
                Element element = cfg.createElement(name);
                rootElement.appendChild(element);
                XMLUtils.writeText(element, DataParser.format(val));
            } catch (DOMException e) {
                System.err.println("Adding XML " + name + " node failed: " + e.getMessage());
            }
        }
        return cfg;
    }
    
    /**
     * Save settings to a XML configuration file
     * 
     * @param filename
     */
    public boolean saveXmlConfigFile(String filename) {
        if (new File(filename).exists()) {
            try {
                Document cfg = buildXmlConfiguration();
                if (cfg != null) 
                    XMLUtils.writeXml(new File(filename), cfg);
            } catch (TransformerFactoryConfigurationError e) {
                Logger.getLogger(Arguments.class).error(e);
                return false;
            } catch (TransformerException e) {
                Logger.getLogger(Arguments.class).error(e);
                return false;
            } catch (IOException e) {
                Logger.getLogger(Arguments.class).error(e);
                return false;
            } catch (ParserConfigurationException e) {
                Logger.getLogger(Arguments.class).error(e);
                return false;
            }
        }

        try {
            UpdatedXmlConfig cfg = buildUpdatedXmlConfigFile(filename);
            if (cfg != null && cfg.config != null) {
                XMLUtils.writeXml(new File(filename), cfg.config);
                return true;
            }
            return false;
        } catch (TransformerFactoryConfigurationError e) {
            Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (TransformerException e) {
            Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (IOException e) {
            Logger.getLogger(Arguments.class).error(e);
            return false;
        }
    }
    
    /**
     * Save configuration settings to a streamed object file
     * 
     * @param filename
     */
    public boolean saveConfigFile(String filename) {
        if (filename == null)
            return false;

        if (isXmlConfiguration())
            return saveXmlConfigFile(filename);
        
        if (new File(filename).exists())
            return updateConfigFile(filename);

        Arguments copy = new Arguments();
        copy.props.putAll(props);

        try {
            System.out.print("Saving configuration to '" + filename + "'...");
            PrintStream config = new PrintStream(new FileOutputStream(filename));
            try {
                for (Map.Entry<String, Object> entry : copy.props.entrySet()) {
                    String key = entry.getKey();
                    if (isInternalKey(key))
                        continue;
                    config.print(key);
                    config.print(" = ");
                    config.println(entry.getValue().toString());
                }
            } finally {
                config.close();
            }
            System.out.println("done.");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("failed.");
            Logger.getLogger(Arguments.class).error(e);
            return false;
        }
    }

    /**
     * Save configuration settings by reading the config file and replacing the
     * values one bny one in order to preserve the sequence and comments in the
     * file
     */
    public boolean updateConfigFile() {
        return updateConfigFile(null);
    }

    /**
     * Save configuration settings by reading a config file and replacing the
     * values one by one in order to preserve the sequence and comments in the
     * file
     * 
     * @param filename
     */
    public boolean updateConfigFile(String filename) {
        if (filename == null)
            filename = getConfigFilename();

        if (filename == null)
            return false;

        if (!new File(filename).exists())
            return saveConfigFile(filename);

        Arguments copy = new Arguments();
        copy.props.putAll(props);
        StringBuffer newConfig = new StringBuffer();

        try {
            System.out.print("Updating configuration in '" + filename + "'...");
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (Utils.normalize(line) == null) {
                    newConfig.append(LINEFEED);
                    continue;
                }

                if (isComment(line)) {
                    newConfig.append(line);
                    newConfig.append(LINEFEED);
                    continue;
                }

                String[] val = line.split("=");
                if (val != null) {
                    String name = Utils.normalize(val[0]);
                    if (name == null) {
                        newConfig.append(line);
                        newConfig.append(LINEFEED);
                        continue;
                    }
                    if (isInternalKey(name))
                        continue;

                    name = name.trim();
                    String value = escape(copy.getString(name));
                    copy.remove(name);
                    newConfig.append(val[0]);
                    if (!val[0].endsWith(" ") && !val[0].endsWith("\t"))
                        newConfig.append(" ");
                    newConfig.append("= ");

                    if (value != null)
                        newConfig.append(value);
                    newConfig.append(LINEFEED);
                }
            }
            reader.close();

            for (Map.Entry<String,Object> entry : copy.props.entrySet()) {
                if (isInternalKey(entry.getKey().toString()))
                    continue;
                newConfig.append(entry.getKey().toString());
                newConfig.append(" = ");
                newConfig.append(entry.getValue().toString());
                newConfig.append(LINEFEED);
            }

            FileOutputStream out = new FileOutputStream(filename);
            out.write(newConfig.toString().getBytes());
            out.close();

            System.out.println("done.");
        } catch (FileNotFoundException e) {
            System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (IOException e) {
            System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        }
        return true;
    }

    /**
     * Read configuration settings from the default configuration file
     * @param readUserConfig
     */
    public boolean readConfigFile(boolean readUserConfig) {
        return readConfigFile(getConfigFilename(), true, readUserConfig);
    }

    /**
     * Read configuration settings from the default configuration file
     */
    public boolean readConfigFile() {
        return readConfigFile(getConfigFilename(), true, readUserConfig);
    }

    /**
     * Read configuration settings from a text file
     * @param filename
     * @param readUserConfig
     */
    public boolean readConfigFile(String filename, boolean readUserConfig) {
        return readConfigFile(filename, false, readUserConfig);
    }

    /**
     * Returns true if configuration is stored in an XML file
     * @return
     */
    public boolean isXmlConfiguration() {
        String filename = getConfigFilename();
        if (filename == null)
            return false;

        return filename.toLowerCase().endsWith(".xml");
    }
    
    /**
     * Read configuration settings from a text file
     * @param filename
     * @param silent
     * @param readUserConfig
     */
    public boolean readConfigFile(String filename, boolean silent, boolean readUserConfig) {
        if (filename == null)
            return false;

        if  (filename.toLowerCase().endsWith(".xml"))
            return readXmlConfigFile(filename, silent, readUserConfig);
        
        try {
            if (!silent)
                System.out.print("Reading configuration from '" + filename + "'...");

            BufferedReader reader = new BufferedReader(new FileReader(filename));
            try {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if (Utils.normalize(line) == null)
                        continue;
    
                    int pos = line.indexOf('#');
                    if (pos >= 0)
                        line = Utils.normalize(line.substring(0, pos));
    
                    if (line == null)
                        continue;
    
                    String[] val = line.split("=");
                    if (val != null) {
                        String name = Utils.normalize(val[0]);
                        switch (val.length) {
                        case 0:
                            break;
                        case 1:
                            setBoolean(name, true);
                            break;
                        case 2:
                        	String tmp = unescape(Utils.normalize(val[1]));
                            setString(name, tmp);
                            break;
                        default:
                            StringBuffer value = new StringBuffer(val[1]);
                            for (int i = 2; i < val.length; i++) {
                                value.append('=');
                                value.append(val[i]);
                            }
                            setString(name, unescape(value.toString()));
                        }
                    }
                }
            } finally {
                reader.close();
            }
            if (!silent)
                System.out.println("done.");
        } catch (AccessControlException e) {
            if (!silent)
                System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (FileNotFoundException e) {
            if (!silent)
                System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (IOException e) {
            if (!silent)
                System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        }
        return true;
    }

    public static void register(String name, Interpreter xmlElementInterpreter) {
        if (name == null || xmlElementInterpreter == null)
            throw new IllegalArgumentException("name and xmlElementInterpreter are required");
        Interpreter existing = interpreters.get(name);
        if (existing != null) {
            if (existing.getClass() == xmlElementInterpreter.getClass())
                Logger.getLogger(Arguments.class).warn("XML interpreter registered already: " + xmlElementInterpreter.getClass().getSimpleName());
            else
                Logger.getLogger(Arguments.class).error("XML interpreter registered already: " + existing.getClass().getSimpleName() + " != "+ xmlElementInterpreter.getClass().getSimpleName());
        }
        interpreters.put(name, xmlElementInterpreter);
    }
    
    /**
     * Return an XML element interpreter
     * @param element
     * @return
     */
    private static Interpreter getInterpreter(Element element) {
        Interpreter result = interpreters.get(element.getNodeName());
        if (result != null)
            return result;
        result = interpreters.get("");
        return (result != null ? result : new DefaultInterpreter());
    }
    
    /**
     * Interpret settings from an XML root element
     * @param root
     * @return
     * @throws SAXException
     */
    @SuppressWarnings("unchecked")
    private Map<String,Object> interpretSettings(Element root) throws SAXException {
        if (!root.getNodeName().equalsIgnoreCase("Configuration"))
            throw new SAXException("Root element must be \"Configuration\"");
        
        NodeList children = root.getChildNodes();
        if (children == null)
            return Collections.EMPTY_MAP;

        
        Map<String,Object> result = new LinkedHashMap<String,Object>();
        for (int i=0; i<children.getLength(); i++) {
            Node chnode = children.item(i);
            if (!(chnode instanceof Element))
                continue;
            Element chel = (Element) chnode;
            getInterpreter(chel).interpret(chel, result);
        }
        
        return result;
    }

    /**
     * Apply a configuration that was read externally
     * @param config
     * @throws SAXException
     */
    public void applyConfiguration(Document config) throws SAXException {
        if (config == null)
            return;
        Element root = config.getDocumentElement();
        if (root == null)
            return;
        Map<String,Object> settings = interpretSettings(root);
        if (settings != null)
            props.putAll(settings);
    }
    
    /**
     * Read the XML file and return the XML document
     * @return
     * @throws IOException
     */
    public Document readXmlConfiguration() throws IOException {
        return readXmlConfiguration(false);
    }
    
    /**
     * Read the XML file and return the XML document
     * @param silent
     * @return
     * @throws IOException
     */
    public Document readXmlConfiguration(boolean silent) throws IOException {
        return readXmlConfiguration(true, silent);
    }
    
    public Document readUserXmlConfiguration(boolean silent) throws IOException {
        String userfilename = getUserConfigFilename();
        if (userfilename == null) 
            return null;
        File userfile = new File(userfilename);
        if (!userfile.isFile()) 
            return null;
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(userfile);
        } catch (SAXException e) {
            System.err.println("Could not parse user configuration in " + userfilename + ": " + e.getMessage());
            return null;
        } catch (ParserConfigurationException e) {
            System.err.println("Could not parse user configuration in " + userfilename + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Read the XML file and return the XML document
     * @param readUserConfig
     * @param silent
     * @return
     * @throws IOException
     */
    public Document readXmlConfiguration(boolean readUserConfig, boolean silent) throws IOException {
        try {
            String filename = getConfigFilename(); 
            if (!silent)
                System.out.print("Reading XML configuration from '" + filename + "'...");
            Document original = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
            if (!readUserConfig) 
                return original;
            Document usercfg = readUserXmlConfiguration(silent);
            if (usercfg == null)
                return original;

            Document result = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            XMLUtils.merge(result, null, original.getDocumentElement(), usercfg.getDocumentElement(), "instance", "name");
            return result;
        } catch (SAXException e) {
            IOException err = new IOException(e);
            err.setStackTrace(e.getStackTrace());
            throw err;
        } catch (ParserConfigurationException e) {
            IOException err = new IOException(e);
            err.setStackTrace(e.getStackTrace());
            throw err;
        }
    }
    
    /**
     * Read configuration settings from a text file
     * @param filename
     * @param silent
     * @param readUserConfig
     */
    public boolean readXmlConfigFile(String filename, boolean silent, boolean readUserConfig) {
        if (filename == null)
            return false;
        
        try {
            if (!silent)
                System.out.print("Reading XML configuration from '" + filename + "'...");

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(filename));
            if (readUserConfig) {
                String userFilename = getUserConfigFilename();
                if (userFilename != null) {
                    File userFile = new File(userFilename);
                    if (userFile.isFile()) {
                        try {
                            Document userdoc = builder.parse(userFile);
                            Document merged = builder.newDocument();
                            XMLUtils.merge(merged, null, doc.getDocumentElement(), userdoc.getDocumentElement(), "instance", "name");
                            doc = merged;
                        } catch (SAXException e1) {
                            userFile.delete();
                        } catch (IOException e1) {
                            userFile.delete();
                        }
                    }
                }
            }
            Element root = doc.getDocumentElement();
            Map<String,Object> result = interpretSettings(root);
            if (result != null)
                props.putAll(result);
            if (!silent)
                System.out.println("done.");
        } catch (AccessControlException e) {
            if (!silent)
                System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (FileNotFoundException e) {
            if (!silent)
                System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (IOException e) {
            if (!silent)
                System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (SAXException e) {
            if (!silent)
                System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        } catch (ParserConfigurationException e) {
            if (!silent)
                System.out.println("failed.");
            if (getBoolean(SHOW_ERROR))
                Logger.getLogger(Arguments.class).error(e);
            return false;
        }
        return true;
    }

    /**
     * Return the filename where the configuration can be found
     * 
     * @return
     */
    public String getConfigFilename() {
        if (configFile != null)
            return configFile;

        configFile = getString(NAME_CONFIG);
        remove(NAME_CONFIG);

        if (configFile == null)
            configFile = getString(PARAM_CONFIG);
        remove(PARAM_CONFIG);

        if (configFile != null)
            return configFile;

        switch (OPERATING_SYSTEM) {
        case OS_LINUX:
        case OS_UNIX:
            configFile = "." + getApplicationName().toLowerCase();
            break;
        default:
            configFile = getApplicationName() + EXT_CONFIG;
        }

        if (configFile.indexOf(File.separatorChar) < 0) {
            try {
                configFile = Utils.setDefaultFolder(configFile, System.getProperty("user.home"));
            } catch (AccessControlException e) {
                System.err.println(e.getMessage());
            }
        }

        if (!new File(configFile).exists()) {
            String tmp = getApplicationConfigPath();
            if (tmp != null)
                configFile = tmp;
        }
        return configFile;
    }

    /**
     * Return a configuration file to use for user specific settings
     * @return
     */
    public String getUserConfigFilename() {
        String cfgpath = getConfigFilename();
        if (cfgpath == null)
            return null;
        File userConfigFolder = getUserConfigurationFolder(getApplicationName(), null);
        if (userConfigFolder == null)
            return cfgpath;
        String cfgfile = new File(cfgpath).getName();
        cfgfile = cfgfile.replace('\\', '/');
        int pos = cfgfile.lastIndexOf('/');
        if (pos >= 0)
            cfgfile = cfgfile.substring(pos+1);
        cfgfile = cfgfile.trim();
        if (cfgfile.length() > 0)
            return new File(userConfigFolder, cfgfile).getAbsolutePath();
        return cfgpath;
    }
    
    /**
     * Set the configuration filename
     * 
     * @param value -
     *            Name of the configuration file
     */
    public void setConfigFilename(String value) {
        setString(NAME_CONFIG, value);
        configFile = value;
    }

    /**
     * Get the log filename to which logs must be written
     * 
     * @return
     */
    public String getLogFilename() {
        if (getBoolean(FLAG_DEBUG))
            return null;

        return getString(NAME_LOGFILE);
    }

    /**
     * Get the log filename to which logs must be written
     * 
     * @return
     */
    public String getErrorFilename() {
        if (getBoolean(FLAG_DEBUG))
            return null;

        String result = getString(NAME_ERRORFILE);
        if (result != null)
            return result;

        result = Utils.setExtension(getConfigFilename(), EXT_ERRORFILE);
        setString(NAME_ERRORFILE, result);
        return result;
    }

    /**
     * Set the name of the application
     * 
     * @param name
     */
    public static void setApplicationName(String name) {
        if (instance != null)
            instance.setString(NAME_APPLICATION, name);
    }

    /**
     * Attempt to get the application name by finding a main method in the stack
     * trace.
     * 
     * @return Name of the class where main method was found
     */
    private static String getApplicationName() {
        if (instance != null) {
            String result = instance.getString(NAME_APPLICATION);
            if (result != null)
                return result;
        }

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            if (trace[i].getMethodName().equals("main"))
                return trace[i].getClassName();
        }
        for (int i = 0; i < trace.length; i++) {
            if (trace[i].getMethodName().equals("init"))
                return trace[i].getClassName();
        }
        return DEFAULT_FILE;
    }
    
    /**
     * Search for a class file in a JAR
     * @param jar
     * @param classFile
     * @return
     */
    private static boolean jarResourceExists(File jar, String classFile) {
        try {
            return (new JarFile(jar).getEntry(classFile) != null);
        } catch (IOException e) {
            System.err.println("Error opening JAR file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the main class from a stack trace. The main
     * class is the class of the highest level function
     * call, provided that the function call is called
     * "main" and that a static method "main(String[])"
     * exists in the class. 
     * @param trace
     * @return  - Returns null if main class not found
     */
    private static Class<?> getMainClass(StackTraceElement[] trace) {
        if (trace == null || trace.length == 0)
            return null;
        StackTraceElement proc = trace[trace.length-1];
        if (!proc.getMethodName().equals("main")) 
            return null;
        
        try {
            Class<?> mainClass = Class.forName(proc.getClassName());
            Method main = mainClass.getMethod(proc.getMethodName(), String[].class);
            if ((main.getModifiers() & Modifier.PUBLIC) == 0)
                return null;
            if ((main.getModifiers() & Modifier.STATIC) == 0)
                return null;
            return mainClass;
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found (" + proc.getClassName() + ")");
            return null;
        } catch (SecurityException e) {
            System.err.println("Security exception accessing " + proc.getClassName() + ".main(String[])");
            return null;
        } catch (NoSuchMethodException e) {
            System.err.println("Could not find main method " + proc.getClassName() + ".main(String[])");
            return null;
        }
    }

    /**
     * Find the main method for this application by checking
     * stack traces of theads called "main".
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Class<?> findMainClass() {
        String name = Thread.currentThread().getName();
        if (name != null && name.equals("main")) {
            Class<?> result = getMainClass(Thread.currentThread().getStackTrace());
            if (result != null)
                return result;
        }
        Map.Entry<Thread, StackTraceElement[]>[] traces = Thread.getAllStackTraces().entrySet().toArray(new Map.Entry[0]);
        for (Map.Entry<Thread, StackTraceElement[]> trace : traces) {
            if (trace.getKey().getName() == null)
                continue;
            if (!trace.getKey().getName().equals("main"))
                continue;
            Class<?> result = getMainClass(trace.getValue());
            if (result != null)
                return result;
        }
        for (Map.Entry<Thread, StackTraceElement[]> trace : traces) {
            Class<?> result = getMainClass(trace.getValue());
            if (result != null)
                return result;
        }
        return null;
    }

    /**
     * Return the main class
     * @return
     */
    public static Class<?> getMainClass() {
        return MAIN_CLASS;
    }
    
    /**
     * Find the path in which the application that currently
     * executes, resides. (This could be the root bin folder
     * where the main class exists in a package folder, or
     * the folder in which the jar which was executes, resides) 
     * @return
     */
    private static String getApplicationConfigPath() {
        Class<?> mainClass = findMainClass();
        if (mainClass == null)
            return ".";
        
        try {
            String classFile = mainClass.getName().replaceAll("\\.", File.separator) + ".class";
            String path = System.getProperty("java.class.path");
            if (path == null)
                return null;
            for (String lib : path.split(File.pathSeparator)) {
                File f = new File(lib);
                if (!f.exists())
                    continue;
                if (f.isDirectory() && new File(f, classFile).exists())
                    return f.getAbsolutePath() + "/" + mainClass.getSimpleName() + ".cfg";
                if (f.isFile() && jarResourceExists(f, classFile)) {
                    if (f.getParentFile() != null)
                        return f.getParentFile().getAbsolutePath() + "/" + mainClass.getSimpleName() + ".cfg";
                    return null;
                }
            }
            return null;
        }  catch (IndexOutOfBoundsException e) {
            Logger.getLogger(Arguments.class).error("Index out of bounds", e);
            return null;
        }
    }

    /**
     * Initialize the default instance.
     * 
     * @param application
     * @param args
     */
    public static void initialize(String application, String[][] args) {
        Arguments inst = getInstance();
        inst.setProperties(args);
        initialize(application, inst, false);
    }

    /**
     * Initialise the default instance.
     * @param application
     * @param readUserConfig
     * @param args
     */
    public static Arguments initialize(String application, boolean readUserConfig, String... args) {
        Arguments inst = getInstance();
        inst.setProperties(args);
        return initialize(application, inst, readUserConfig);
    }

    /**
     * Initialise the default instance.
     * @param readUserConfig
     */
    public static void initialize(String application, boolean readUserConfig) {
        initialize(application, readUserConfig, (String[]) null);
    }

    /**
     * Initialise the default instance.
     */
    public static void initialize() {
        initialize(null, false, (String[]) null);
    }

    /**
     * Initialise the default instance.
     * @param args
     * @param cfgAliases
     */
    public static Arguments initialize(String... args) {
        return initialize(null, false, args);
    }

    /**
     * Initialise the default instance.
     * 
     * @param application
     * @param args
     * @param readUserConfig
     */
    protected static Arguments initialize(String application, Arguments args, boolean readUserConfig) {
        if (instance == null)
            instance = args;

        if (initialized)
            return instance;

        getInstance();

        if (instance.getString(NAME_APPLICATION) == null)
            instance.setString(NAME_APPLICATION, application);

        instance.readUserConfig = readUserConfig;
        instance.readConfigFile(instance.getConfigFilename(), true, readUserConfig);
        instance.props.putAll(args.props);
        instance.setLogFiles();
        
        initialized = true;
        return instance;
    }

    /**
     * Set log and error output streams
     */
    public void setLogFiles() {
        try {
            String log = instance.getLogFilename();
            if (log != null)
                System.setOut(new PrintStream(new FileOutputStream(log)));
        } catch (FileNotFoundException e) {
            Logger.getLogger(Arguments.class).error(e);
        }

        try {
            String err = instance.getLogFilename();
            if (err != null)
                System.setOut(new PrintStream(new FileOutputStream(err)));
        } catch (FileNotFoundException e) {
            Logger.getLogger(Arguments.class).error(e);
        }
    }

    /**
     * Initialize the default instance.
     * 
     * @param args
     */
    public static void initialize(String[][] args) {
        initialize(null, args);
    }

    /**
     * Get the default instance.
     */
    public static Arguments getInstance() {
        if (instance == null) 
            instance = new Arguments();
        return instance;
    }

    /**
     * Escape a string by replacing special characters (<TAB>, <LF>, <CR>) with
     * C-style tokens
     * 
     * @param value
     * @return
     */
    public static String escape(String value) {
        if (value == null)
            return "";
        value = Utils.replaceString(value, "\t", "\\t");
        value = Utils.replaceString(value, "\n", "\\n");
        value = Utils.replaceString(value, "\r", "\\r");
        return value;
    }

    /**
     * Un-escape a string by replacing C-style tokens with characters <TAB>, <LF>
     * and <CR>
     * 
     * @param value
     * @return
     */
    public static String unescape(String value) {
        if (value == null)
            return "";
        value = Utils.replaceString(value, "\\t", "\t");
        value = Utils.replaceString(value, "\\n", "\n");
        value = Utils.replaceString(value, "\\r", "\r");
        return value;
    }

    /**
     * Remove a configuration setting
     * @param key
     * @return
     */
    public synchronized Object remove(Object key) {
        return props.remove(key);
    }

    /**
     * Used to construct a set of properties using the arguments of a Java
     * application.
     */
    public void setProperties(String[][] args) {
        if (args == null)
            return;

        for (int i = 0; i < args.length; i++)
            if (args[i] != null) {
                if (args[i].length == 2)
                    setString(args[i][0], args[i][1]);
                else
                    setBoolean(args[i][0], true);
            }
    }

    /**
     * Used to construct a set of properties using the arguments of a Java
     * application.
     */
    public void setProperties(String[] args) {
        if (args == null)
            return;

        for (int i = 0; i < args.length; i++) {
            String name = args[i];

            int pos = name.indexOf('=');
            if (pos < 0)
                pos = name.indexOf(':');

            if (pos >= 0) {
                String value = Utils.normalize(name.substring(pos + 1));
                name = Utils.normalize(name.substring(0, pos));
                setString(name, value);
            } else if (i < args.length - 1) {
                String value = args[i + 1];

                if (name.startsWith("-") && !value.startsWith("-")) {
                    setString(name, value);
                    i++;
                } else {
                    setString(name, Boolean.TRUE.toString());
                }
            } else {
                setString(name, Boolean.TRUE.toString());
            }
        }
    }

    /**
     * Get the default XML folder
     * @return
     */
    public File getXmlFolder() {
        String folder = getString(PROPERTY_XML_FOLDER);
        if (folder == null)
            return null;
        
        if (xmlFolderName == null || !xmlFolderName.equals(folder)) {
            xmlFolderName = folder;
            xmlFolder = new File(folder);
            if (!xmlFolder.exists() || !xmlFolder.isDirectory()) {
                Logger.getLogger(Arguments.class).error(folder + " is not a valid folder");
                xmlFolder = null;
            }
        }
        return xmlFolder;
    }
    
    /**
     * Get the default resource folder (resources in java library)
     * @return
     */
    public String getResourceFolder() {
        return getString(PROPERTY_RESOURCE_FOLDER);
    }
    
    /**
     * Get the default XML folder
     * @return
     */
    public void setXmlFolder(File value) {
        if (value == null) {
            setString(PROPERTY_XML_FOLDER, null);
            return;
        }
        if (value.exists() && value.isDirectory()) 
            setString(PROPERTY_XML_FOLDER, value.toString());
        else
            Logger.getLogger(Arguments.class).error(value + " is not a valid folder");
    }
    
    /**
     * Get the default resource folder (resources in java library)
     * @return
     */
    public void setResourceFolder(String value) {
        setString(PROPERTY_RESOURCE_FOLDER, value);
    }
    
    /**
     * Set the resource folder by using the package of
     * a class
     * @param resourceClass
     */
    public void setResourceFolder(Class<?> resourceClass) {
        setString(PROPERTY_RESOURCE_FOLDER, resourceClass.getPackage().getName().replaceAll("\\.", "/"));
    }
    
    /**
     * Return the user language
     * @return language
     */
    public String getLocale() {
        String result = Utils.normalize(getString(PARAM_LOCALE));
        if (result == null)
            result = Locale.getDefault().toString();
        return result;
    }

    /**
     * Set the user language
     * @param language
     */
    public void setLocale(String language) {
        language = Utils.normalize(language);
        setString(PARAM_LOCALE, (language != null ? language.trim().toLowerCase() : "en"));
    }

    /**
     * Return the default application icon filename
     * 
     * @return
     */
    public String getIcon() {
        String result = getString(PARAM_ICON);
        return (result != null ? result : "icon.gif");
    }

    /**
     * Return the proxy server defined in the configuration
     * @return
     * @throws UnknownHostException 
     */
    public Proxy getProxy() throws UnknownHostException {
        if (proxy != null)
            return proxy;
        String proxyName = getString(PARAM_PROXY);
        if (proxyName == null)
            return null;
        String[] proxyParts = proxyName.split(":");
        int port = (proxyParts.length > 1 ? IntegerParser.toInt(proxyParts[1]) : 0);
        port = (port > 0 ? port : DEFAULT_PROXY_PORT);
        proxy = new Proxy(Type.HTTP, new InetSocketAddress(InetAddress.getByName(proxyParts[0]), port));
        return proxy;
    }
    
    /**
     * Get configuration parameters
     * @return
     */
    public Map<String, Object> getEntries() {
        Map<String, Object> result = new PropertyMap<Object>();
        result.putAll(props);
        return result;
    }

    /**
     * Check the command line for -h or --help parameter. 
     * If it is found, then print usage and return true.
     * @param args
     * @param usage
     * @return
     */
    public static boolean checkHelpParameter(String[] args, String usage) {
        if (args == null)
            return false;
        for (String a : args) {
            if (a.equals("-h") || a.equals("--help")) {
                if (usage != null)
                    System.out.println(usage);
                return true;
            }
        }
        return false;
    }
    
    private String fixMailKey(String key) {
        String skey = key.toString().trim().toLowerCase();
        if (skey.startsWith("smtp.")) 
            return "mail." + skey;
        if (skey.startsWith("pop3.")) 
            return "mail." + skey;
        if (skey.startsWith("imap.")) 
            return "mail." + skey;
        return key;
    }

    private String normalize(Object value) {
        if (value == null)
            return null;
        return Utils.normalize(value.toString());
    }
    
    private void fixMailUser(Map<String,String> props, String protocol) {
        if (protocol == null)
            return;
        String user = normalize(props.get("mail." + protocol + ".user"));
        if (user == null) 
            user = normalize(props.get("mail." + protocol + ".username"));
        if (user == null) {
            props.put("mail." + protocol + ".auth", "false");
            return;
        }
        
        props.put("mail." + protocol + ".user", user);
        props.put("mail." + protocol + ".auth", "true");
    }
    
    /**
     * Return SMTP (transmit) properties
     * @return
     */
    public Map<String,String> getEmailProperties() {
        Map<String,String> props = new PropertyMap<String>();
        String transmitProtocol = getString("mail.transmit.protocol");
        String storeProtocol = getString("mail.store.protocol");
        boolean useIMAP = false;
        for (Map.Entry<Object,Object> p : System.getProperties().entrySet()) {
            Object key = p.getKey();
            Object val = p.getValue();
            if (!(key instanceof String) || val == null)
                continue;
            String skey = fixMailKey(key.toString());
            if (!skey.startsWith("mail."))
                continue;
            if (skey.startsWith("mail.smtp.") && transmitProtocol == null)
                transmitProtocol = "smtp";
            if (skey.startsWith("mail.pop3.") && storeProtocol == null)
                storeProtocol = "pop3";
            if (skey.startsWith("mail.imap."))
                useIMAP = true;
            props.put(skey, normalize(val));
        }
        for (Map.Entry<String,Object> p : this.props.entrySet()) {
            Object key = p.getKey();
            Object val = p.getValue();
            if (!(key instanceof String) || val == null)
                continue;
            String skey = fixMailKey(key.toString());
            if (!skey.startsWith("mail."))
                continue;
            if (skey.startsWith("mail.smtp.") && transmitProtocol == null)
                transmitProtocol = "smtp";
            if (skey.startsWith("mail.pop3.") && storeProtocol == null)
                storeProtocol = "pop3";
            if (skey.startsWith("mail.imap."))
                useIMAP = true;
            props.put(skey, normalize(val));
        }
        if (transmitProtocol == null && useIMAP) 
            transmitProtocol = "imap";
        if (storeProtocol == null && useIMAP) 
            storeProtocol = "imap";
        
        if (transmitProtocol != null)
            props.put("mail.transmit.protocol", transmitProtocol);
        if (storeProtocol != null)
            props.put("mail.store.protocol", storeProtocol);
        fixMailUser(props, transmitProtocol);
        fixMailUser(props, storeProtocol);
        return props;
    }
    
    /**
     * Return SMTP (transmit) properties
     * @return
     */
    public Map<String,String> getEmailStoreProperties() {
        Map<String,String> props = new PropertyMap<String>();
        String protocol = getString("mail.transmit.protocol");
        for (Map.Entry<Object,Object> p : System.getProperties().entrySet()) {
            Object key = p.getKey();
            Object val = p.getValue();
            if (!(key instanceof String) || val == null)
                continue;
            String skey = key.toString().trim().toLowerCase();
            if (skey.startsWith("smtp.")) {
                props.put(skey, "mail." + val.toString());
                if (protocol == null)
                    protocol = "smtp";
            } else if (skey.startsWith("mail.smtp.")) {
                props.put(skey, val.toString());
                if (protocol == null)
                    protocol = "smtp";
            }
        }
        for (Map.Entry<String,Object> p : this.props.entrySet()) {
            Object key = p.getKey();
            Object val = p.getValue();
            if (!(key instanceof String) || val == null)
                continue;
            String skey = key.toString().trim().toLowerCase();
            if (skey.startsWith("smtp.")) {
                props.put(skey, "mail." + val.toString());
                if (protocol == null)
                    protocol = "smtp";
            } else if (skey.startsWith("mail.smtp.")) {
                props.put(skey, val.toString());
                if (protocol == null)
                    protocol = "smtp";
            }
        }
        if (protocol != null)
            props.put("mail.transmit.protocol", protocol);
        String user = Utils.normalize(props.get("mail." + protocol + ".user"));
        if (user == null) {
            user = Utils.normalize(props.get("mail." + protocol + ".username"));
            if (user != null)
                props.put("mail." + protocol + ".user", user);
        }
        props.put("mail.smtp.auth", (user != null ? "true" : "false"));
        return props;
    }
    
    public static interface Interpreter {
        public void interpret(Element element, Map<String,Object> toAppendTo) throws SAXException;
        public void append(Element parent, Map.Entry<String,Object> setting) throws SAXException;
    }
    
    public static class DefaultInterpreter implements Interpreter {
        public void append(Element parent, String key, Object value) throws SAXException {
            key = Utils.normalize(key);
            if (key == null)
                return;
            Document doc = parent.getOwnerDocument();
            Element el = doc.createElement(key);
            parent.appendChild(el);
            String val = Utils.normalize(DataParser.format(value));
            if (val != null)
                el.appendChild(doc.createTextNode(val));
        }

        public void appendIfNotNull(Element parent, String key, Object value) throws SAXException {
            key = Utils.normalize(key);
            if (key == null)
                return;
            if (value != null)
                append(parent, key, value);
        }

        @Override
        public void append(Element parent, Map.Entry<String, Object> setting) throws SAXException {
            if (setting != null)
                append(parent, setting.getKey(), setting.getValue());
        }

        protected void interpret(Element element, String prefix, Map<String,Object> toAppendTo) throws SAXException {
            if (element == null)
                return;
            String name = element.getNodeName();
            if (name.equalsIgnoreCase("Setting")) {
                String tmp = Utils.normalize(element.getAttribute("name"));
                if (tmp != null)
                    name = tmp; 
            }
            String key = (prefix != null ? prefix + "." + name : name);
            String childPrefix = key;
            
            NamedNodeMap attribs = element.getAttributes();
            if (attribs != null) {
                for (int i=0; i<attribs.getLength(); i++) {
                    Node attrib = attribs.item(i);
                    String atname = attrib.getNodeName();
                    String atval = attrib.getTextContent();
                    if (atname.equalsIgnoreCase(XML_INSTANCE))
                        childPrefix = (childPrefix != null ? childPrefix + "." + atval : atval);
                    else
                        toAppendTo.put(key + "." + atname, atval);
                }
            }
            
            interpretChildren(element, childPrefix, toAppendTo);
        }
        
        protected void interpretChildren(Element element, String prefix, Map<String,Object> toAppendTo) throws SAXException {
            NodeList children = element.getChildNodes();
            StringBuilder buf = null;
            if (children != null) {
                for (int i=0; i<children.getLength(); i++) {
                    Node chnode = children.item(i);
                    if (chnode instanceof Text) {
                        if (buf == null)
                            buf = new StringBuilder();
                        buf.append(((Text) chnode).getData());
                    } 
                    if (chnode instanceof Element) {
                        interpret((Element) chnode, prefix, toAppendTo); 
                    }
                }
            }
            String text = (buf != null ? Utils.normalize(buf.toString()) : null);
            if (text == null)
                text = Utils.normalize(element.getAttribute("value"));
            
            toAppendTo.put(prefix, text);
        }

        @Override
        public void interpret(Element element, Map<String,Object> toAppendTo) throws SAXException {
            interpret(element, null, toAppendTo);
        }
    }


    private class UpdatedXmlConfig {
        public final boolean changed;
        public final Document config;
        
        public UpdatedXmlConfig(boolean changed, Document config) {
            this.changed = changed;
            this.config = config;
        }
        
    }
}
