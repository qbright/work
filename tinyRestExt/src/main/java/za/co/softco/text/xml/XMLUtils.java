/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: XMLUtils.java,v $
 *  Created on Feb 15, 2009
 *******************************************************************************/
package za.co.softco.text.xml;


import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import za.co.softco.text.DataParser;
import za.co.softco.util.Constants;
import za.co.softco.util.PropertyMap;
import za.co.softco.util.Utils;

/**
 * XML utility functions
 * @author john
 * @model
 */
public class XMLUtils {

    /**
     * Build an array of nodes in which items can be set to null if no longer needed
     * @param parent
     * @return
     */
    private static Node[] getChildNodes(Element parent) {
        NodeList children = parent.getChildNodes();
        List<Node> result = new ArrayList<Node>(children.getLength());
        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof CharacterData) {
                String text = ((CharacterData) child).getData();
                if (Utils.normalize(text) != null)
                    result.add(child);
            } else if (child instanceof Element) {
                result.add(child);
            }
        }
        return result.toArray(new Node[result.size()]);
    }
    
    /**
     * Build an array of nodes in which items can be set to null if no longer needed
     * @param parent
     * @return
     */
    public static Map<String,String> getIdentifyingAttributes(Element element, String... nodeIdentifyingAttributes) {
        if (nodeIdentifyingAttributes == null || nodeIdentifyingAttributes.length == 0)
            return null;
        Map<String,String> attribs = getAttributes(element);
        if (attribs == null || attribs.size() == 0)
            return null;
        Map<String,String> result = new PropertyMap<String>(new LinkedHashMap<String,String>(nodeIdentifyingAttributes.length*2));
        for (String key : nodeIdentifyingAttributes) {
            String val = Utils.normalize(attribs.get(key));
            if (val != null)
                result.put(key,val);
        }
        if (result.size() == 0)
            return null;
        return result;
    }

    /**
     * Find an Element entry from an array of nodes by returning the first Element entry
     * of which the first specified attribute in the list of node identifying attributes
     * corresponds with the value in the identifying attributes.
     * @param nodes
     * @param nodeIdentifyingAttributes
     * @return
     */
    @SuppressWarnings("unchecked")
    private static int findElement(Node[] nodes, String elementType, Map<String,String> nodeIdentifyingAttributes) {
        if (nodes == null || nodes.length == 0)
            return -1;
        
        if (nodeIdentifyingAttributes != null && nodeIdentifyingAttributes.size() == 0)
            nodeIdentifyingAttributes = null;
        
        Map.Entry<String,String>[] identifiers;
        if (nodeIdentifyingAttributes != null)
            identifiers = nodeIdentifyingAttributes.entrySet().toArray(new Map.Entry[nodeIdentifyingAttributes.size()]);
        else
            identifiers = null;
        
        for (int i=0; i<nodes.length; i++) {
            if (!(nodes[i] instanceof Element))
                continue;
            String elType = ((Element) nodes[i]).getNodeName();
            if (!elementType.equalsIgnoreCase(elType))
                continue;
            Map<String,String> attribs = getAttributes(nodes[i]);
            if (attribs == null || attribs.size() == 0) {
                if (identifiers == null)
                    return i;
                continue;
            }
            if (identifiers == null)
                continue;
            for (Map.Entry<String,String> attrib : identifiers) {
                if (attrib.getKey() == null || attrib.getValue() == null)
                    continue;
                String ref = Utils.normalize(attribs.get(attrib.getKey()));
                if (ref == null)
                    continue;
                if (ref.equalsIgnoreCase(attrib.getValue()))
                    return i;
            }
        }
        return -1;
    }

    /**
     * Returns true if two nodes contains the same character data (data is trimmed before comparing) 
     * @param src
     * @param tgt
     * @return
     */
    private static boolean compareCharacterDataNodes(Node src, Node tgt) {
        if (!(src instanceof CharacterData))
            return false;
        if (!(tgt instanceof CharacterData))
            return false;
        String srcData = ((CharacterData) src).getData();
        String tgtData = ((CharacterData) tgt).getData();
        if (srcData == tgtData)
            return true;
        if (srcData == null)
            srcData = "";
        if (tgtData == null)
            tgtData = "";
        return srcData.trim().equals(tgtData.trim());
    }
    
    /**
     * Find a corresponding node by comparing contents
     * @param nodes
     * @param reference
     * @param nodeIdentifyingAttributes
     * @return
     */
    private static int findCorrespondingNode(Node[] nodes, Node reference, String... nodeIdentifyingAttributes) {
        if (nodes == null || nodes.length == 0 || reference == null)
            return -1;

        if (reference instanceof Element) {
            if (nodeIdentifyingAttributes == null || nodeIdentifyingAttributes.length == 0)
                return -1;
            Map<String,String> identifier = getIdentifyingAttributes((Element) reference, nodeIdentifyingAttributes);
            return findElement(nodes, reference.getNodeName(), identifier);
        }

        if (reference instanceof CDATASection) {
            for (int i=0; i<nodes.length; i++) {
                if (nodes[i] instanceof CDATASection) {
                    if (compareCharacterDataNodes(reference, nodes[i]))
                        return i;
                }
            }
        } else if (reference instanceof Text) {
            for (int i=0; i<nodes.length; i++) {
                if (nodes[i] instanceof Text && !(nodes[i] instanceof CDATASection)) {
                    if (compareCharacterDataNodes(reference, nodes[i]))
                        return i;
                }
            }
        } else if (reference instanceof Comment) {
            for (int i=0; i<nodes.length; i++) {
                if (nodes[i] instanceof Comment) {
                    if (compareCharacterDataNodes(reference, nodes[i]))
                        return i;
                }
            }
        } else if (reference instanceof CharacterData) {
            for (int i=0; i<nodes.length; i++) {
                if (nodes[i] instanceof CharacterData && !(nodes[i] instanceof Comment)) {
                    if (compareCharacterDataNodes(reference, nodes[i]))
                        return i;
                }
            }
        } else if (reference instanceof DocumentFragment) {
            for (int i=0; i<nodes.length; i++) {
                if (nodes[i] instanceof DocumentFragment) {
                    String src = Utils.normalize(reference.getNodeValue());
                    String tgt = Utils.normalize(reference.getNodeValue());
                    if (src != null && tgt != null && src.equals(tgt))
                        return i;
                }
            }
        }
        return -1;
    }

    /**
     * Override attributes
     * @param attribs
     * @param overridingAttribs
     * @param ifValueNotNull
     * @return
     */
    private static Map<String,String> merge(Map<String,String> attribs, Map<String,String> overridingAttribs, boolean ifValueNotNull) {
        if (attribs == null)
            return overridingAttribs;
        if (overridingAttribs == null)
            return attribs;
        for (Map.Entry<String,String> attr : overridingAttribs.entrySet()) {
            if (!ifValueNotNull || attr.getValue() != null)
                attribs.put(attr.getKey(), attr.getValue());
        }
        return attribs;
    }
    
    /**
     * Merge two XML trees
     * @param resultDocument
     * @param resultRoot
     * @param source
     * @param target
     * @param name
     */
    public static Element merge(Document resultDocument, Element resultRoot, Element primaryRoot, Element overridingRoot, String... nodeIdentifyingAttributes) {
        if (resultDocument == null) 
            throw new IllegalArgumentException("resultDocument parameter is required");
        if (primaryRoot == null && overridingRoot == null) 
            return resultRoot;
        if (primaryRoot == null || overridingRoot == null) {
            if (resultRoot == null && primaryRoot != null) {
                resultRoot = resultDocument.createElement(primaryRoot.getNodeName());
                resultDocument.appendChild(resultRoot);
            }
            if (resultRoot == null)
                throw new IllegalStateException("Could not create result node (primaryRoot node is null)");
            if (overridingRoot == null) {
                copyAttributes(primaryRoot, resultRoot);
                copyStructure(primaryRoot, resultRoot);
                return resultRoot;
            }
            copyAttributes(overridingRoot, resultRoot);
            copyStructure(overridingRoot, resultRoot);
            return resultRoot;
        }
        if (resultRoot == null) {
            resultRoot = resultDocument.createElement(primaryRoot.getNodeName());
            resultDocument.appendChild(resultRoot);
        }
        copyAttributes(primaryRoot, resultRoot);
        copyAttributes(overridingRoot, resultRoot);
        Node[] primChildren = getChildNodes(primaryRoot);
        if (primChildren == null || primChildren.length == 0) {
            copyStructure(overridingRoot, resultRoot);
            return resultRoot;
        }
        Node[] overChildren = getChildNodes(overridingRoot);
        if (overChildren == null || overChildren.length == 0) {
            copyStructure(primaryRoot, resultRoot);
            return resultRoot;
        }
        
        for (Node primChild : primChildren) {
            if (primChild instanceof Element) {
                Element resultChild = resultDocument.createElement(primChild.getNodeName());
                Map<String,String> attrs = getAttributes(primChild);
                Map<String,String> identifier = getIdentifyingAttributes((Element) primChild, nodeIdentifyingAttributes);
                int j = findElement(overChildren, primChild.getNodeName(), identifier);
                if (j >= 0) {
                    Node overChild = overChildren[j];
                    overChildren[j] = null;
                    if (overChild instanceof Element) {
                        attrs = merge(attrs, getAttributes((Element) overChild), false);
                        merge(resultDocument, resultChild, (Element) primChild, (Element) overChild, nodeIdentifyingAttributes);
                    }
                } else {
                    merge(resultDocument, resultChild, (Element) primChild, null, nodeIdentifyingAttributes);
                }
                setAttributes(resultChild, attrs);
                resultRoot.appendChild(resultChild);
            }
        }
        boolean cdataAdded = false;
        boolean textAdded = false;
        boolean commentAdded = false;
        for (Node overChild : overChildren) {
            if (overChild == null)
                continue;
            if (overChild instanceof Element) {
                Element newNode = resultDocument.createElement(overChild.getNodeName());
                copyAttributes((Element) overChild, (Element) newNode);
                copyStructure(overChild, newNode);
                resultRoot.appendChild(newNode);
            } else if (overChild instanceof CDATASection) {
                resultRoot.appendChild(resultDocument.createCDATASection(((CDATASection) overChild).getData()));
                cdataAdded = true;
            } else if (overChild instanceof Text) {
                resultRoot.appendChild(resultDocument.createTextNode(((Text) overChild).getData()));
                textAdded = true;
            } else if (overChild instanceof Comment) {
                resultRoot.appendChild(resultDocument.createComment(((Comment) overChild).getData()));
                commentAdded = true;
            }
        }
        for (Node primChild : primChildren) {
            if (primChild == null)
                continue;
            if (primChild instanceof CDATASection && !cdataAdded) {
                resultRoot.appendChild(resultDocument.createCDATASection(((CDATASection) primChild).getData()));
                cdataAdded = true;
            } else if (primChild instanceof Text && !textAdded) {
                resultRoot.appendChild(resultDocument.createTextNode(((Text) primChild).getData()));
                textAdded = true;
            } else if (primChild instanceof Comment && !commentAdded) {
                resultRoot.appendChild(resultDocument.createComment(((Comment) primChild).getData()));
                commentAdded = true;
            }
        }
        return resultRoot;
    }

    /**
     * Get a set of attributes that have changed in the changedRoot (compared to primaryRoot)
     * @param primaryRoot
     * @param changedRoot
     * @return
     */
    private static Map<String,String> extractChangedAttributes(Element primaryRoot, Element changedRoot) {
        if (changedRoot == null)
            return null;
        Map<String,String> changedAttribs = getAttributes(changedRoot);
        if (changedAttribs == null || changedAttribs.size() == 0)
            return null;
        if (primaryRoot == null)
            return changedAttribs;
        Map<String,String> primAttribs = getAttributes(primaryRoot);
        if (primAttribs == null || primAttribs.size() == 0)
            return changedAttribs;
        Map<String,String> result = new PropertyMap<String>(new LinkedHashMap<String,String>(primAttribs.size() + changedAttribs.size()));
        for (Map.Entry<String,String> primEntry : primAttribs.entrySet()) {
            String key = Utils.normalize(primEntry.getKey());
            if (key == null)
                continue;
            String oldVal = Utils.normalize(primEntry.getValue());
            String newVal = Utils.normalize(changedAttribs.get(key));
            changedAttribs.remove(primEntry.getKey());
            if (newVal == null)
                continue;
            if (oldVal == null || !newVal.equals(oldVal)) 
                result.put(key, newVal);
        }
        for (Map.Entry<String,String> newEntry : changedAttribs.entrySet()) {
            String key = Utils.normalize(newEntry.getKey());
            String newVal = Utils.normalize(newEntry.getValue());
            if (key != null && newVal != null)
                result.put(key, newVal);
        }
        if (result.size() > 0)
            return result;
        return null;
    }
    
    /**
     * Extract all changes by comparing two XML documents
     * @param resultDocument - The document to create new nodes in the result
     * @param resultRoot - The parent node in which to set changes (corresponds to the primaryRoot and changedRoot nodes)
     * @param primaryRoot
     * @param modifiedRoot
     * @param nodeIdentifyingAttributes
     * @return true if any changes are found
     */
    public static boolean extractChanges(Document resultDocument, Element resultRoot, Element primaryRoot, Element modifiedRoot, String... nodeIdentifyingAttributes) {
        if (resultDocument == null)
            throw new IllegalArgumentException("changesDocument parameter is required");
        if (resultRoot == null)
            resultRoot = resultDocument.getDocumentElement();
        if (modifiedRoot == null)
            return false;
        if (resultRoot == null) {
            resultRoot = resultDocument.createElement(modifiedRoot.getNodeName());
            resultDocument.appendChild(resultRoot);
        }
        if (primaryRoot == null) {
            copyAttributes(modifiedRoot, resultRoot);
            copyStructure(modifiedRoot, resultRoot);
            return (resultRoot.getChildNodes().getLength() > 0);
        }
        boolean result = false;
        Map<String,String> changedAttrs = extractChangedAttributes(primaryRoot, modifiedRoot);
        if (changedAttrs != null && changedAttrs.size() > 0) {
            setAttributes(resultRoot, changedAttrs);
            result = true;
        }

        Node[] primChildren = getChildNodes(primaryRoot);
        if (primChildren == null || primChildren.length == 0) {
            copyStructure(modifiedRoot, resultRoot);
            return (result || (resultRoot.getChildNodes().getLength() > 0));
        }
        Node[] changedChildren = getChildNodes(modifiedRoot);
        if (changedChildren == null || changedChildren.length == 0)
            return result;
        
        for (Node primChild : primChildren) {
            if (primChild instanceof Element) {
                Map<String,String> identifier = getIdentifyingAttributes((Element) primChild, nodeIdentifyingAttributes);
                int j = findElement(changedChildren, primChild.getNodeName(), identifier);
                if (j >= 0) {
                    Node changedChild = changedChildren[j];
                    changedChildren[j] = null;
                    if (changedChild instanceof Element) {
                        Element resultNode = resultDocument.createElement(primChild.getNodeName());
                        Map<String,String> changed = extractChangedAttributes((Element) primChild, (Element) changedChild);
                        boolean hasChanged = false;
                        if (changed != null && changed.size() > 0) {
                            setAttributes(resultNode, changed);
                            hasChanged = true;
                        }
                        if (extractChanges(resultDocument, resultNode, (Element) primChild, (Element) changedChild, nodeIdentifyingAttributes)) {
                            hasChanged = true;
                        }
                        if (hasChanged) {
                            if (identifier != null) 
                                setAttributes(resultNode, identifier);
                            resultRoot.appendChild(resultNode);
                            result = true;
                        }
                    }
                }
            } else {
                int j = findCorrespondingNode(changedChildren, primChild);
                if (j >= 0) 
                    changedChildren[j] = null;
            }
        }
        for (Node changedChild : changedChildren) {
            if (changedChild == null)
                continue;
            if (changedChild instanceof Element) {
                Node newNode = resultDocument.createElement(changedChild.getNodeName());
                copyAttributes((Element) changedChild, (Element) newNode);
                copyStructure(changedChild, newNode);
                resultRoot.appendChild(newNode);
                result = true;
            } else if (changedChild instanceof CDATASection) {
                resultRoot.appendChild(resultDocument.createCDATASection(((CDATASection) changedChild).getData()));
                result = true;
            } else if (changedChild instanceof Text) {
                resultRoot.appendChild(resultDocument.createTextNode(((Text) changedChild).getData()));
                result = true;
            } else if (changedChild instanceof Comment) {
                resultRoot.appendChild(resultDocument.createComment(((Comment) changedChild).getData()));
                result = true;
            } else if (changedChild instanceof DocumentFragment) {
                resultRoot.appendChild(resultDocument.createDocumentFragment());
                result = true;
            }
        }
        return result;
    }
    
    /**
     * Copy a single attribute
     * @param source
     * @param target
     * @param name
     */
    public static void copyAttribute(Element source, Element target, String name) {
        if (source == null || target == null)
            return;
        String attrib = source.getAttribute(name);
        if (Utils.normalize(attrib) != null)
            target.setAttribute(name, attrib);
    }

    /**
     * Copy all attributes from one node to another
     * @param source
     * @param target
     */
    public static void copyAttributes(Element source, Element target) {
        copyAttributes(source, target, false);
    }

    /**
     * Copy all attributes from one node to another
     * @param source
     * @param target
     * @param ifValueNotNull - If true, only copy values which are not null when normalized
     */
    public static void copyAttributes(Element source, Element target, boolean ifValueNotNull) {
        if (source == null || target == null)
            return;
        NamedNodeMap attribs = source.getAttributes();
        for (int i = 0; i < attribs.getLength(); i++) {
            Node attrib = attribs.item(i);
            if (ifValueNotNull) {
                String value = Utils.normalize(attrib.getNodeValue());
                if (value != null)
                    target.setAttribute(attrib.getNodeName(), attrib.getNodeValue());
            } else {
                target.setAttribute(attrib.getNodeName(), attrib.getNodeValue());
            }
        }
    }

    /**
     * Copy all sub-elements and its attributes recursively
     * @param source
     * @param target
     */
    public static void copyStructure(Node source, Node target) {
        if (source == null || target == null)
            return;
        Document doc = target.getOwnerDocument();
        for (Node oldNode = source.getFirstChild(); oldNode != null; oldNode = oldNode.getNextSibling()) {
            if (oldNode instanceof Element) {
                Node newNode = doc.createElement(oldNode.getNodeName());
                copyAttributes((Element) oldNode, (Element) newNode);
                copyStructure(oldNode, newNode);
                target.appendChild(newNode);
            } else if (oldNode instanceof CDATASection) {
                target.appendChild(doc.createCDATASection(((CDATASection) oldNode).getData()));
            } else if (oldNode instanceof Text) {
                target.appendChild(doc.createTextNode(((Text) oldNode).getData()));
            } else if (oldNode instanceof Comment) {
                target.appendChild(doc.createComment(((Comment) oldNode).getData()));
            } else if (oldNode instanceof DocumentFragment) {
                target.appendChild(doc.createDocumentFragment());
            }
        }
    }

    /**
     * Clone a complete element by creating new elements inside a new document
     * @param source
     * @param target
     * @param copyStructure
     * @param setAsTargetRoot
     */
    public static Element clone(Element source, Document target, boolean copyStructure, boolean setAsTargetRoot) {
        if (source == null || target == null)
            return null;
        Element result = target.createElement(source.getNodeName());
        if (setAsTargetRoot) {
            if (target.getDocumentElement() != null)
                target.removeChild(target.getDocumentElement());
            target.appendChild(result);
        }
        copyAttributes(source, result);
        if (copyStructure)
            copyStructure(source, result);
        return result;
    }

    /**
     * Clone a complete element by creating new elements inside a new document
     * @param source
     * @param target
     * @param setAsTargetRoot
     */
    public static Element clone(Element source, Document target, boolean setAsTargetRoot) {
        return clone(source, target, true, setAsTargetRoot);
    }
    
    /**
     * Clone an entire XML document
     * @param document
     * @return
     * @throws ParserConfigurationException
     */
    public static Document clone(Document document) throws ParserConfigurationException {
        if (document == null)
            return null;
        Document result = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        clone(document.getDocumentElement(), result, true);
        return result;
    }

    /**
     * Read a single attributes of an XML node
     * @param node
     * @return
     */
    public static String getAttribute(Node node, String attribute) {
        if (node == null || attribute == null)
            return null;
        
        NamedNodeMap attrs = node.getAttributes();
        if (attrs == null)
            return null;
        
        attribute = attribute.trim();
        Node a = attrs.getNamedItem(attribute);
        if (a != null)
            return a.getNodeValue(); 
        
        for (int i=0; i<attrs.getLength(); i++) {
            a = attrs.item(i);
            if (a.getNodeName().equalsIgnoreCase(attribute))
                return a.getNodeValue(); 
        }
        return null;
    }

    /**
     * Convert the attributes of an XML node into a case-insensitive value map
     * @param attribs
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> convertAttributes(NamedNodeMap attribs) {
        if (attribs == null)
            return Collections.EMPTY_MAP;
        int count = attribs.getLength();
        if (count == 0)
            return Collections.EMPTY_MAP;
        Map<String, String> result = new PropertyMap<String>(count);
        for (int i = 0; i < count; i++) {
            Node attrib = attribs.item(i);
            result.put(attrib.getNodeName(), attrib.getNodeValue());
        }
        return result;
    }

    /**
     * Read the attributes of an XML node into a case-insensitive value map
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getAttributes(Node node) {
        if (node == null)
            return Collections.EMPTY_MAP;
        return convertAttributes(node.getAttributes());
    }

    /**
     * Set the attributes of an XML node from an attribute map
     * @param node
     * @param attributes
     */
    public static void setAttributes(Node node, Map<String,?> attributes) {
        setAttributes(node, attributes, false);
    }
    
    /**
     * Set the attributes of an XML node from an attribute map
     * @param node
     * @param attributes
     * @param ifValueNotNull - Only set attributes of which normalized value is not null
     */
    public static void setAttributes(Node node, Map<String,?> attributes, boolean ifValueNotNull) {
        if (node == null || attributes == null)
            return;
        if (!(node instanceof Element))
            return;
        Element el = (Element) node;
        for (Map.Entry<String,?> attrib : attributes.entrySet()) {
            String key = Utils.normalize(attrib.getKey());
            if (key == null)
                continue;
            String val = DataParser.format(attrib.getValue());
            if (ifValueNotNull && Utils.normalize(val) == null)
                continue;
            el.setAttribute(key, val);
        }
    }

    /**
     * Remove all child elements of a specific type (or all if "*")
     * @param parent
     * @param elementName
     */
    public static void removeElements(Element parent, String elementName) {
        NodeList elements = parent.getElementsByTagName((elementName != null ? elementName : "*"));
        for (int i = elements.getLength() - 1; i >= 0; i--)
            parent.removeChild(elements.item(i));
    }
    
    /**
     * Remove all attributes of an element
     * @param node
     */
    public static void removeAttributes(Element node) {
        NamedNodeMap attribs = node.getAttributes();
        if (attribs == null)
            return;
        int count = attribs.getLength();
        if (count == 0)
            return;
        List<String> names = new ArrayList<String>(count);
        for (int i=0; i<count; i++)
            names.add(attribs.item(i).getNodeName());
        for (String name : names) 
            node.removeAttribute(name);
    }
    
    /**
     * Read a text node from an element
     * @param element
     * @return
     */
    public static String readText(Element element) {
        if (element == null)
            return null;
        NodeList children = element.getChildNodes();
        if (children == null || children.getLength() == 0)
            return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Text) {
                String tmp = ((Text) child).getData();
                if (tmp != null)
                    result.append(tmp);
            }
        }
        return Utils.normalize(result.toString());
    }

    /**
     * Clear all child nodes
     * @param element
     * @param text
     */
    public static void removeAllChildren(Element element) {
        if (element == null)
            return;

        NodeList children = element.getChildNodes();
        if (children != null && children.getLength() > 0) {
            for (int i = 0; i < children.getLength(); i++)
                element.removeChild(children.item(i));
        }
    }

    /**
     * Clear all child elements and add a single text node with a value
     * @param element
     * @param text
     */
    public static void writeText(Element element, String text) {
        if (element == null)
            return;

        removeAllChildren(element);
        if (text != null)
            element.appendChild(element.getOwnerDocument().createTextNode(text));
    }

    /**
     * Return a child node by name and optionally create one if it does not exist
     * @param parent
     * @param name
     * @param create
     * @return
     * @throws XPathExpressionException
     */
    public static Element getElement(Node parent, String name, boolean create) throws XPathExpressionException {
        XPathExpression xp = XPathFactory.newInstance().newXPath().compile(name);
        Node node = (Node) xp.evaluate(parent, XPathConstants.NODE);
        if (node instanceof Element)
            return (Element) node;
        if (create) {
            Element result = parent.getOwnerDocument().createElement(name);
            parent.appendChild(result);
            return result;
        }
        return null;
    }

    /**
     * Return a child node by name and optionally create one if it does not exist
     * @param parent
     * @param name
     * @return
     * @throws XPathExpressionException
     */
    public static List<Element> getElements(Node parent, String name) {
        List<Element> result = new LinkedList<Element>();
        if (!(parent instanceof Element) || name == null)
            return result;
        
        NodeList children = parent.getChildNodes();
        if (children == null)
            return result;
        
        int count = children.getLength();
        for (int i=0; i<count; i++) {
            Node child = children.item(i);
            if (!(child instanceof Element))
                continue;
            if (child.getNodeName().equalsIgnoreCase(name))
                result.add((Element) child);
        }
        return result;
    }

    /**
     * Return an element by searching a document using an XPath expression
     * @param doc
     * @param path
     * @return
     * @throws XPathExpressionException
     */
    public static Element getXPathElement(Document doc, String path) throws XPathExpressionException {
        XPathExpression xp = XPathFactory.newInstance().newXPath().compile(path);
        Node node = (Node) xp.evaluate(doc, XPathConstants.NODE);
        if (node instanceof Element)
            return (Element) node;
        return null;
    }

    /**
     * Return an element by searching a document using an XPath expression
     * @param doc
     * @param path
     * @return
     * @throws XPathExpressionException
     */
    public static List<Element> getXPathElements(Document doc, String path) throws XPathExpressionException {
        XPathExpression xp = XPathFactory.newInstance().newXPath().compile(path);
        NodeList nodes = (NodeList) xp.evaluate(doc, XPathConstants.NODESET);
        List<Element> result = new LinkedList<Element>();
        if (nodes != null && nodes.getLength() > 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node instanceof Element)
                    result.add((Element) node);
            }
        }
        return result;
    }

    /**
     * Return an element by searching a document using an XPath expression
     * @param parent
     * @param path
     * @return
     * @throws XPathExpressionException
     */
    public static Element getXPathElement(Element parent, String path) throws XPathExpressionException {
        XPathExpression xp = XPathFactory.newInstance().newXPath().compile(path);
        Node node = (Node) xp.evaluate(parent, XPathConstants.NODE);
        if (node instanceof Element)
            return (Element) node;
        return null;
    }

    /**
     * Return an element by searching a document using an XPath expression
     * @param parent
     * @param path
     * @return
     * @throws XPathExpressionException
     */
    public static List<Element> getXPathElements(Element parent, String path) throws XPathExpressionException {
        XPathExpression xp = XPathFactory.newInstance().newXPath().compile(path);
        NodeList nodes = (NodeList) xp.evaluate(parent, XPathConstants.NODESET);
        List<Element> result = new LinkedList<Element>();
        if (nodes != null && nodes.getLength() > 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node instanceof Element)
                    result.add((Element) node);
            }
        }
        return result;
    }

    /**
     * Return an element by searching a document using an XPath expression
     * @param doc
     * @param path
     * @return
     * @throws XPathExpressionException
     */
    public static List<Element> getChildren(Element parent) throws XPathExpressionException {
        NodeList nodes = parent.getChildNodes();
        List<Element> result = new LinkedList<Element>();
        if (nodes != null && nodes.getLength() > 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node instanceof Element)
                    result.add((Element) node);
            }
        }
        return result;
    }

    /**
     * Read XML file
     * @param file
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static Document readXmlFile(File file) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    }

    /**
     * Read XML from a URL
     * @param url
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static Document readXml(String url) throws SAXException, IOException, ParserConfigurationException {
        if (url.indexOf("://") > 0) {
            InputStream in = new URL(url).openStream();
            try {
                return readXml(in);
            } finally {
                in.close();
            }
        } else {
            File f = new File(url);
            if (f.exists()) {
                InputStream in = new FileInputStream(f);
                try {
                    return readXml(in);
                } finally {
                    in.close();
                }
            }
            throw new IOException("Invalid URL or file not found: " + url);
        }
    }

    /**
     * Read XML from a stream
     * @param in
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static Document readXml(InputStream in) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new InputStreamReader(in, Constants.UTF8)));
    }

    /**
     * Read XML from a text stream
     * @param in
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static Document readXml(Reader in) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(in));
    }

    /**
     * Write a XML document to a file
     * @param file
     * @param document
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws IOException 
     */
    public static void writeXml(File file, Document document) throws TransformerFactoryConfigurationError, TransformerException, IOException {

        DOMImplementation impl = document.getImplementation();
        if (impl.hasFeature("LS", "3.0") && impl.hasFeature("Core", "2.0")) {
            DOMImplementationLS domils = (DOMImplementationLS) impl.getFeature("LS", "3.0");
            LSSerializer lsSerializer = domils.createLSSerializer();
            DOMConfiguration domcfg = lsSerializer.getDomConfig();
            if (domcfg.canSetParameter("format-pretty-print", Boolean.TRUE)) {
                lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
                LSOutput lsOutput = domils.createLSOutput();
                lsOutput.setEncoding("UTF-8");
                Writer fw = new FileWriter(file);
                try {
                    lsOutput.setCharacterStream(fw);
                    lsSerializer.write(document, lsOutput);
                } finally {
                    fw.close();
                }
                return;
            }
        }
        
        DOMSource domSource = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult sr = new StreamResult(new OutputStreamWriter(new FileOutputStream(file), Constants.UTF8.newEncoder()));
        transformer.transform(domSource, sr);
    }

    /**
     * Write a XML document to a stream
     * @param out
     * @param document
     * @param encoding
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws IOException
     */
    public static void writeXml(OutputStream out, Document document, String encoding) throws TransformerFactoryConfigurationError, TransformerException, IOException {
    	encoding = Utils.normalize(encoding);
    	if (encoding == null)
    		encoding = "UTF-8";
        DOMSource domSource = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult sr = new StreamResult(new OutputStreamWriter(out, Constants.UTF8.newEncoder()));
        transformer.transform(domSource, sr);
        out.flush();
    }

    /**
     * Write a XML document to a stream
     * @param out
     * @param document
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws IOException
     */
    public static void writeXml(OutputStream out, Document document) throws TransformerFactoryConfigurationError, TransformerException, IOException {
    	writeXml(out, document, "UTF-8");
    }

    /**
     * Write XML to a Writer
     * @param writer
     * @param document
     * @param encoding
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws IOException
     */
    public static void writeXml(Writer writer, Document document, String encoding) throws TransformerFactoryConfigurationError, TransformerException, IOException {
    	encoding = Utils.normalize(encoding);
    	if (encoding == null)
    		encoding = "UTF-8";
        DOMSource domSource = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult sr = new StreamResult(writer);
        transformer.transform(domSource, sr);
        writer.flush();
    }

    /**
     * Write XML to a Writer
     * @param writer
     * @param document
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws IOException
     */
    public static void writeXml(Writer writer, Document document) throws TransformerFactoryConfigurationError, TransformerException, IOException {
    	writeXml(writer, document, "UTF-8");
    }

    /**
     * Create XML text
     * @param document
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws IOException
     */
    public static String toXML(Document document) throws TransformerFactoryConfigurationError, TransformerException, IOException {
        CharArrayWriter writer = new CharArrayWriter();
        writeXml(writer, document);
        return writer.toString();
    }

    /**
     * Returns true if a string can be used as a XML element name
     * @param name
     * @return
     */
    public static boolean isValidElementName(String name) {
        name = Utils.normalize(name);
        if (name == null)
            return false;
        for (char c : name.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'z')
                continue;
            if (c >= '0' && c <= '9')
                continue;
            switch (c) {
            case '_':
            case '-':
                continue;
            default:
                return false;
            }
        }
        return true;
    }
}
