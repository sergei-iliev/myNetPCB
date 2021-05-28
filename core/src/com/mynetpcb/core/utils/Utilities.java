package com.mynetpcb.core.utils;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.d2.shapes.Point;


public final class Utilities {
    public static final int PIN_LENGTH = 2 * Utilities.POINT_TO_POINT;
    
    public static final int DEVICE_INCH = 72;

    public static final int USER_INCH = 72;

    public static final int USER_OFFSET = USER_INCH / 12;

    public static final int INNER_OFFSET = USER_INCH / 5;

    public static final int POINT_TO_POINT = USER_INCH / 9; //point to point on the grid

    public static final int ROTATION_LEFT = 0x01;

    public static final int ROTATION_RIGHT = 0x02;

    public static String toHex(Color color){
        return null;
    }
    public static Color fromHex(String value){
        return null;
    }
    /** Read content from resources folder*/
    public static String readResourceFile(Class clazz,String fileName) throws IOException{
        try(
        		BufferedReader reader = new BufferedReader(new InputStreamReader(clazz.getResourceAsStream(fileName)));
        		){                	    
        	    StringBuilder sb = new StringBuilder();
        	    String line;
        	    	while ((line = reader.readLine()) != null) {
        	    		sb.append(line);
        	    		sb.append("\n");
        	    	}
               return sb.toString(); 
        }        
        
    }
    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon loadImageIcon(Object o, String filename) {
        Image image = null;
        try {
            // get a stream to read the image
            InputStream in = o.getClass().getResource(filename).openStream();
            // buffering -> more efficient
            BufferedInputStream bufIn = new BufferedInputStream(in);
            // the byte array that will contain the image
            byte bytes[] = new byte[10000];
            // read the image
            int count = bufIn.read(bytes, 0, 10000);
            // create the image from the byte array
            image = Toolkit.getDefaultToolkit().createImage(bytes, 0, count);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        if (image != null) {
            // build up the ImageIcon
            return new ImageIcon(image);
        }

        return null;
    }

    public static double roundDouble(double number){
         return BigDecimal.valueOf(number).setScale(5, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
    public static double roundDouble(double number,int rounding){
         return BigDecimal.valueOf(number).setScale(rounding, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }

    public static void IncrementRect(Rectangle2D rect, int x, int y) {
        rect.setRect(rect.getX() - x, rect.getY() - y, rect.getWidth() + 2 * x, rect.getHeight() + 2 * y);
    }



    /*
     * Find out in which quadrant is a point B, in regard to a point origine A
     *
     *        2   |  1
     *        ----------
     *        3   |  4
     */

    public enum QUADRANT {
        FIRST,
        SECOND,
        THIRD,
        FORTH
    }
    
//    public static QUADRANT getQuadrantLocation(Point origin,int x,int y) {
//        if (x >= origin.getX() && y <= origin.getY())
//            return QUADRANT.FIRST;
//        else if (x <= origin.getX() && y <= origin.getY())
//            return QUADRANT.SECOND;
//        else if (x <= origin.getX() && y >= origin.getY())
//            return QUADRANT.THIRD;
//        else
//            return QUADRANT.FORTH;
//    }
    public static QUADRANT getQuadrantLocation(Point origin, Point B) {
        if (B.x >= origin.x && B.y <= origin.y)
            return QUADRANT.FIRST;
        else if (B.x <= origin.x && B.y <= origin.y)
            return QUADRANT.SECOND;
        else if (B.x <= origin.x && B.y >= origin.y)
            return QUADRANT.THIRD;
        else
            return QUADRANT.FORTH;
    }

    public static Document buildDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = null;
        builder = domFactory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

    /**
     * Add item(Category/Symbol) to tree
     * @param moduleTree
     * @param root
     * @param library
     * @param subLibrary
     * @param module
     * @param filename
     */
    public static void addLibraryModule(JTree moduleTree, DefaultMutableTreeNode root, String library, String category,
                                        String module, String filename) {
        //no category
        if (category == null || ("").equals(category)) {
            DefaultMutableTreeNode moduleNode =
                new DefaultMutableTreeNode(new AttachedItem.Builder(module).setLibrary(library).setFileName(filename).build());
            ((DefaultTreeModel) moduleTree.getModel()).insertNodeInto(moduleNode, root, root.getChildCount());

        } else {
            DefaultMutableTreeNode categoryNode = null;
            for (int i = 0; i < root.getChildCount(); i++) {
                categoryNode = (DefaultMutableTreeNode) root.getChildAt(i);
                AttachedItem data = (AttachedItem) categoryNode.getUserObject();
                if (data.getCategory() != null &&
                    data.getCategory().equalsIgnoreCase(category)) {
                    //***add module
                    ((DefaultTreeModel) moduleTree.getModel()).insertNodeInto(new DefaultMutableTreeNode(new AttachedItem.Builder(module).setLibrary(library).setCategory(category).setFileName(filename).build()),
                                                                              categoryNode,
                                                                              categoryNode.getChildCount());
                    return;
                }
            }
            //***add category node
            categoryNode =
                new DefaultMutableTreeNode(new AttachedItem.Builder(category).setLibrary(library).setCategory(category).build()) {
                    @Override
                    public boolean isLeaf() {
                        return false;
                    }
                };
            ((DefaultTreeModel) moduleTree.getModel()).insertNodeInto(categoryNode, root, root.getChildCount());
            //***add module
            if (module != null && module.length() > 0) {
                ((DefaultTreeModel) moduleTree.getModel()).insertNodeInto(new DefaultMutableTreeNode(new AttachedItem.Builder(module).setLibrary(library).setCategory(category).setFileName(filename).build()),
                                                                          categoryNode, categoryNode.getChildCount());
            }
        }
    }

    /*
     *Add xml node to content root node
     */
    public static String addNode(String content, String nodeName, String nodeValue) throws ParserConfigurationException,
                                                                                           SAXException, IOException,
                                                                                           TransformerConfigurationException,
                                                                                           TransformerException {
        StreamResult result = null;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(content));
        Document doc = docBuilder.parse(is);

        Node root = doc.getDocumentElement();

        Node newElement = doc.createElement(nodeName);
        newElement.setTextContent(nodeValue);
        root.appendChild(newElement);


        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");


        result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        return result.getWriter().toString();
    }
    
    public static void drawCrosshair(Graphics2D g2,
                                     Point resizingPoint, int length, Point... points) {

        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line = (Line2D) provider.getShape();

        g2.setStroke(new BasicStroke(1));

        for (Point point : points) {
            if (resizingPoint != null && resizingPoint.equals(point))
                g2.setColor(Color.YELLOW);
            else
                g2.setColor(Color.BLUE);
            line.setLine(point.x- length, point.y, point.x + length, point.y);
            g2.draw(line);

            line.setLine(point.x, point.y - length, point.x, point.y + length);
            g2.draw(line);
        }
        provider.reset();
    }

    //helper - avoid repetition


    public static void selectTreeNode(JTree tree, AttachedItem attachedItem) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            if (!node.isLeaf()) {
                AttachedItem data = (AttachedItem) node.getUserObject();
                if (data.equals(attachedItem)) {
                    TreePath path = new TreePath(node.getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
            }
        }
    }

    public static void selectTreeLeaf(JTree tree, AttachedItem attachedItem) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            if (node.isLeaf()) {
                AttachedItem data = (AttachedItem) node.getUserObject();
                if (data.equals(attachedItem)) {
                    TreePath path = new TreePath(node.getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
            } else {
                //drill into category
                for (int j = 0; j < node.getChildCount(); j++) {
                    DefaultMutableTreeNode subnode = (DefaultMutableTreeNode) node.getChildAt(j);
                    if (subnode.isLeaf()) {
                        AttachedItem data = (AttachedItem) subnode.getUserObject();
                        if (data.equals(attachedItem)) {
                            TreePath path = new TreePath(subnode.getPath());
                            tree.setSelectionPath(path);
                            tree.scrollPathToVisible(path);
                        }
                    }
                }
            }
        }
    }

    public static File[] getFileDirOrder(File dir) {
        File files[] = dir.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(final File o1, final File o2) {
                if (o1.isDirectory() && o2.isDirectory()) {
                    return o1.compareTo(o2);
                } else {
                    if (o1.isFile() && o2.isFile()) {
                        return o1.compareTo(o2);
                    } else {
                        return (o1.isDirectory() ? -1 : 1);
                    }
                }
            }
        });
        return files;
    }
    /*
     * RULE:1.Chip is considered positioned BEFORE another if its location in the circuit is closer to Y axis.
     *      2.If Y's are equal then the one closer to X axis is BEFORE the other.
     *      3.If they overlap -> the first included is BEFORE.
     */
    public static boolean isBefore(Rectangle rect1, Rectangle rect2) {
        if (rect1.x < rect2.x) {
            return true;
        } else {
            if (rect1.x == rect2.x) {
                if (rect1.y <= rect2.y) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
//    public static boolean isSameNet(ClearanceSource source,Net target){
//        if(Objects.equals(source.getNetName(), target.getNetName())&&(!("".equals(target.getNetName())))&&(!(null==(target.getNetName())))){
//            return true;
//        }
//        return false;
//    }
    public static String trimCRLF(String value) {
        if (value == null || value.length() < 3)
            return value;
        else
            return value.replaceAll("[\\r\\n]", "");
    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public static void setUILookAndFeel() {
        //UIManager.put("nimbusBase", Color.GREEN);
        UIManager.put("control",new Color(212, 226, 247));
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {

        }
    }
}

