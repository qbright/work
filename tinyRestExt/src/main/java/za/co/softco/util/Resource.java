/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Revision 1.2  2007/12/23 17:04:05  remjohn
 *  Use Constants class in stead of hard coded strings
 *
 *  Revision 1.1  2007/12/22 19:36:07  remjohn
 *  Added to CVS
 *
 *  Revision 1.4  2006/12/28 10:23:31  obelix
 *  File.toURL() deprecated from Java 6
 *
 *  Revision 1.3  2006/10/10 10:05:16  goofyxp
 *  Implement default extensions
 *
 *  Revision 1.2  2006/05/21 11:44:47  obelix
 *  Attempt to read resource from root of resource tree as well
 *
 *  Revision 1.1  2006/05/20 16:53:35  obelix
 *  Created
 *
 *  Created on May 20, 2006
 *******************************************************************************/
package za.co.softco.util;

import static za.co.softco.util.Constants.IMAGE_RESOURCE_FOLDER;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * Class that enables automatic searching for a resource
 * @author john
 * @model
 */
public class Resource {

    //private static final String DEFAULT_ICON_EXTENSION = ".gif";
    //private static final String DEFAULT_IMAGE_EXTENSION = ".jpg";
    private static final String[] VALID_ICON_EXTENSIONS = { ".gif", ".png", ".bmp" };
    private static final String[] VALID_IMAGE_EXTENSIONS = { ".jpg", ".png", ".gif", ".bmp" };

    private static final File currentFolder = new File(".");

    private static final File userFolder;

    static {
        String home = System.getProperty("user.home");
        File temp = (home != null ? new File(home) : null);
        userFolder = (temp != null && temp.isDirectory() ? temp : null);
    }

    private final URL resource;

    /**
     * Constructor for a resource.
     * @param name
     * @throws FileNotFoundException
     */
    public Resource(String name) throws FileNotFoundException {
        super();
        this.resource = locateResource(name);
    }

    /**
     * Return a URL to the resource
     * @return
     */
    public URL getURL() {
        return resource;
    }

    /**
     * Load an image resource from one of the libraries / jars in the classpath
     * @param resourceName
     * @return
     */
    public static final Image loadImageResource(String resourceName) {
        if (resourceName == null)
            return null;

        for (String extension : VALID_IMAGE_EXTENSIONS) {
            String tmpResourceName = Utils.setDefaultExtension(resourceName, extension);
            if (tmpResourceName.indexOf('/') < 0)
                tmpResourceName = IMAGE_RESOURCE_FOLDER + "/" + tmpResourceName;
    
            URL url = getResource(tmpResourceName);
            if (url != null)
                return Toolkit.getDefaultToolkit().getImage(url);
        }
        return null;
    }

    /**
     * Load a binary resource from one of the libraries / jars in the classpath
     * @param resourceName
     * @return
     * @throws IOException 
     */
    public static final byte[] loadBinaryResource(String resourceName) throws IOException {
        if (resourceName == null)
            return null;

        URL url = getResource(resourceName);
        if (url == null)
            return null;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            InputStream in = url.openStream();
            try {
                Utils.copy(in, out);
                out.flush();
            } finally {
                in.close();
            }
        } finally {
            out.close();
        }
        return out.toByteArray();
    }

    /**
     * Load an icon resource from one of the libraries / jars in the classpath
     * @param resourceName
     * @return
     */
    public static final Icon loadIconResource(final String resourceName) {
        if (resourceName == null)
            return null;

        for (String extension : VALID_ICON_EXTENSIONS) {
            String tmpResourceName = Utils.setDefaultExtension(resourceName, extension);
            if (tmpResourceName.indexOf('/') < 0)
                tmpResourceName = IMAGE_RESOURCE_FOLDER + "/" + tmpResourceName;
    
            URL url = getResource(tmpResourceName);
            if (url != null)
                return new ImageIcon(url);
        }
        return null;
    }

    /**
     * Load a text resource
     * @param resourceName
     * @return
     */
    public static String loadTextResource(String resourceName) {
        if (resourceName == null)
            return null;

        URL url = getResource(resourceName);
        if (url == null)
            return null;

        try {
            Reader in = new InputStreamReader(url.openStream());
            try {
                StringWriter out = new StringWriter();
                try {
                    Utils.copy(in, out);
                    return out.toString();
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            Logger.getLogger(Resource.class).error("Could not load resource: " + resourceName, e);
            return null;
        }
    }
    
    /**
     * Search various locations for a resource. The XML folder is searched for the resource, if not found, the resource is searched in the libraries
     * (jars). If still not found, the current folder and finally the user folder is searched.
     * @param name
     * @throws FileNotFoundException
     */
    public static URL locateResource(String name) throws FileNotFoundException {
        if (name == null)
            throw new IllegalArgumentException("No resource name specified");

        URL result = getFileResource(Arguments.getInstance().getXmlFolder(), name);
        if (result != null)
            return result;

        String resourceFolder = Arguments.getInstance().getResourceFolder();
        if (resourceFolder != null) {
            result = getResource(resourceFolder + "/" + name);
            if (result != null)
                return result;
        }

        result = getFileResource(currentFolder, name);
        if (result != null)
            return result;

        result = getResource(name);
        if (result != null)
            return result;

        result = getFileResource(userFolder, name);
        if (result != null)
            return result;

        throw new FileNotFoundException("Could not locate resource " + name);
    }

    /**
     * Return a resource by looking in a specific folder
     * @param folder
     * @param name
     * @return
     */
    private static URL getFileResource(File folder, String name) {
        if (name == null)
            throw new IllegalArgumentException("No resource name specified");
        if (folder == null)
            return null;

        File result = new File(folder, name);
        if (result.exists() && result.isFile())
            try {
                return result.toURI().toURL();
            } catch (MalformedURLException e) {
                Logger.getLogger(Resource.class).error(e);
            }
        return null;
    }

    /**
     * Load a resource from one of the libraries / jars in the classpath
     * @param resourceName
     * @return
     */
    public static final URL getResource(String resourceName) {
        if (resourceName == null)
            return null;

        ClassLoader cl = Utils.class.getClassLoader();
        URL result = cl.getResource(resourceName);
        if (result == null)
            System.err.println("Could not load resource (" + resourceName + ")");
        return result;
    }

    /**
     * List resources by traversing entries in a Jar file
     * @param jarFile
     * @param resourceFolder
     * @param result
     */
    private static final List<URL> listResourcesFromJarFile(File jarFile, String resourceFolder, FilenameFilter filter) {
        List<URL> result = new LinkedList<URL>();
        try {
            File dir = new File(resourceFolder);
            JarFile jf = new JarFile(jarFile);
            try {
            	Enumeration<JarEntry> en = jf.entries();
	            while (en.hasMoreElements()) {
	                JarEntry item = en.nextElement();
	                String name = item.getName();
	                if (!name.startsWith(resourceFolder))
	                    continue;
	                name = name.substring(resourceFolder.length());
	                if (name.length() == 0 || name.indexOf('/') > 0)
	                    continue;
	                if (filter != null && !filter.accept(dir, name))
	                    continue;
	                URL url = getResource(resourceFolder + name);
	                if (url != null)
	                    result.add(url);
	            }
            } finally {
            	jf.close();
            }
        } catch (IOException e) {
            Logger.getLogger(Resource.class).error(e);
        }
        return result;
    }
    
    /**
     * List resources by traversing a folder
     * @param jarFile
     * @param resourceFolder
     * @param result
     */
    private static final List<URL> listResourcesFromFolder(File resFolder, FilenameFilter filter) {
        List<URL> result = new LinkedList<URL>();
        File[] files = (filter != null ? resFolder.listFiles(filter) : resFolder.listFiles());
        if (files == null)
            return result;
        for (File f : files) {
            if (!f.isDirectory() && f.canRead())
                try {
                    result.add(f.toURI().toURL());
                } catch (MalformedURLException e) {
                    Logger.getLogger(Resource.class).error(e);
                }
        }
        return result;
    }

    /**
     * List resources by traversing a folder
     * @param jarFile
     * @param resourceFolder
     * @param result
     */
    private static final List<URL> listResourcesFromFolder(File classPathFolder, String resourceFolder, FilenameFilter filter) {
        return listResourcesFromFolder(new File(classPathFolder, resourceFolder), filter);
    }

    /**
     * Load a resource from one of the libraries / jars in the classpath
     * @param resourceName
     * @return
     * @throws IOException
     */
    private static final URL[] listResourcesClassPath(String resourceFolder, FilenameFilter filter) {
        if (resourceFolder == null)
            resourceFolder = "/";
        if (!resourceFolder.endsWith("/"))
            resourceFolder += "/";

        String classPath = System.getProperty("java.class.path");
        if (classPath == null)
            return new URL[0];

        List<URL> result = new LinkedList<URL>();
        for (String entry : classPath.split(File.pathSeparator)) {
            File file = new File(entry);
            String ext = Utils.getFileExtension(entry);
            if (ext != null)
                ext = ext.trim().replaceFirst("\\.", "");
            else
                ext = "";
            if (file.isFile() && ext.equals("jar"))
                result.addAll(listResourcesFromJarFile(file, resourceFolder, filter));
            else if (file.isDirectory())
                result.addAll(listResourcesFromFolder(file, resourceFolder, filter));
            else
                Logger.getLogger(Resource.class).warn("Unexpected class path element: " + entry);
        }

        return result.toArray(new URL[result.size()]);
    }

    
    
    /**
     * Load a resource from one of the libraries / jars in the classpath
     * @param resourceName
     * @return
     */
    public static final URL[] listResources(String resourceFolder, FilenameFilter filter) {
        if (resourceFolder == null)
            resourceFolder = "/";
        if (!resourceFolder.endsWith("/"))
            resourceFolder += "/";

        ClassLoader cl = Utils.class.getClassLoader();
        URL folder = cl.getResource(resourceFolder);
        if (folder == null)
            return listResourcesClassPath(resourceFolder, filter);

        if (folder.getProtocol().equalsIgnoreCase("jar")) {
            String path = folder.getPath();
            if (path.startsWith("file:"))
                path = path.substring(5);
            int ndx = path.indexOf('!');
            if (ndx >= 0)
                path = path.substring(0, ndx);
            List<URL> result = listResourcesFromJarFile(new File(path), resourceFolder, filter);
            if (result != null)
                return result.toArray(new URL[result.size()]);
            return new URL[0];
        } 
        
        String tmpDir = folder.getFile();
        if (tmpDir == null)
            return listResourcesClassPath(resourceFolder, filter);

        List<URL> result = listResourcesFromFolder(new File(tmpDir), filter);
        if (result != null)
            return result.toArray(new URL[result.size()]);
        return new URL[0];
    }

}
