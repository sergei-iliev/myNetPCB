package com.mynetpcb.symbol.unit;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.Typeable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.symbol.shape.SymbolShapeFactory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.lang.ref.WeakReference;

import java.util.Collection;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class Symbol extends Unit<Shape> implements Typeable{
    private boolean isTextLayoutVisible;

    private Typeable.Type type;


    public Symbol(int width, int height) {
        super(width, height);
        this.shapeFactory = new SymbolShapeFactory();
        grid.setGridUnits(8, Grid.Units.PIXEL);
        this.grid.setPointsColor(Color.BLACK);
        this.frame.setFillColor(Color.BLACK);
        this.type = Typeable.Type.SYMBOL;
        scalableTransformation.reset(1.2, 2, 0, ScalableTransformation.DEFAULT_MAX_SCALE_FACTOR);  
        
    }


    public Symbol clone() throws CloneNotSupportedException {
        Symbol copy = (Symbol) super.clone();
        copy.shapeFactory = new SymbolShapeFactory();
        return copy;
    }

    @Override
    public void add(Shape shape) {
        super.add(shape);
//        if (shape instanceof Textable) {
//            ((Textable) shape).getChipText().setTextLayoutVisible(isTextLayoutVisible);
//        }
    }

    private WeakReference<PrintContext> context;
    
    
    @Override
    public void prepare(PrintContext context) {        
        this.context = new WeakReference<>(context);     
    }
    
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        Graphics2D g2 = (Graphics2D) graphics;

        //*** Validate the page number, we only print the first page
        if (pageIndex == 0) {
            //--- Translate the origin to be (0,0)
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            //WORKS perfect
            //            double xscale = 1.0 / 16; // Set 1152 logical units for an inch
            //            double yscale = 1.0 / 16; // as the standard resolution is 72
            //            double zoom = 5.76;
            //
            //
            //            int persentage =
            //                Integer.parseInt(this.getParameter("print.scale", String.class).substring(0, this.getParameter("print.scale",
            //                                                                                                               String.class).length() -
            //                                                                                          1));
            //            zoom = (persentage * 16) / 100;
            //
            //            g2.scale(xscale, yscale);

            /*
    double xscale = 1.0/16; // Set 1152 logical units for an inch
            double yscale = 1.0/16; // as the standard resolution is 72
            double zoom = 5.76;     // act in a 1152 dpi resolution as 1:1



    This might be explained as follows:
    1 - The Java printing system normally works with an internal resolution which
    is 72 dpi (probably inspired by Postscript).
    2 - To have a sufficient resolution, this is increased by 16 times, by using
    the scale method of the graphic object associated to the printer. This gives a
    72 dpi *16=1152 dpi resolution.
    3 - The 0.127 mm pitch used in FidoCadJ corresponds to a 200 dpi resolution.
    Calculating 1152 dpi / 200 dpi gives the 5.76 constant
    */

            //***draw figures
            for (Shape shape : getShapes()) {
                shape.print(g2, context.get(), 0);
            }
            return (PAGE_EXISTS);
        } else
            return (NO_SUCH_PAGE);
    }

    public boolean getTextLayoutVisibility() {
        return isTextLayoutVisible;
    }

    public void setTextLayoutVisibility(boolean isTextLayoutVisible) {
        this.isTextLayoutVisible = isTextLayoutVisible;
//        for (Shape textable : this.<Shape>getShapes(Textable.class)) {
//            ((Textable) textable).getChipText().setTextLayoutVisible(isTextLayoutVisible);
//        }
    }

    public StringBuffer format() {
        StringBuffer xml = new StringBuffer();
//        xml.append("<module width=\"" + this.getWidth() + "\" height=\"" + this.getHeight() + "\">\r\n");
//        xml.append("<footprint library=\"" +
//                   (packaging.getFootprintLibrary() == null ? "" : packaging.getFootprintLibrary()) + "\" category=\"" +
//                   (packaging.getFootprintCategory() == null ? "" : packaging.getFootprintCategory()) +
//                   "\"  filename=\"" +
//                   (packaging.getFootprintFileName() == null ? "" : packaging.getFootprintFileName()) + "\" name=\"" +
//                   (packaging.getFootprintName() == null ? "" : packaging.getFootprintName()) + "\"/>\r\n");
//        xml.append("<name>" + this.unitName + "</name>\r\n");
//        //***reference
//        FontLabel text = (FontLabel)SymbolMgr.getInstance().getLabelByTag(this,"reference");
//        if (text != null) {
//            xml.append("<reference>");
//            xml.append(text.toXML());
//            xml.append("</reference>\r\n");
//        }
//        //unit
//        text =(FontLabel)SymbolMgr.getInstance().getLabelByTag(this,"unit");
//        if (text != null) {
//            xml.append("<unit>");
//            xml.append(text.toXML());
//            xml.append("</unit>\r\n");
//        }
//
//        //exclude ref and value tags
//        List shapes=getShapes().stream().filter(s->{
//            if(s instanceof Label){
//                if(((Label)s).getTexture().getTag().equals("reference")||((Label)s).getTexture().getTag().equals("unit")){
//                   return false; 
//                }else{
//                   return true; 
//                }                
//            }else{
//                return true;
//            }
//        }).collect(Collectors.toList());
//        xml.append(Format(shapes));
//
//        xml.append("</module>");
        return xml;
    }

    @Override
    protected StringBuffer format(Collection<Shape> shapes) {
        StringBuffer xml = new StringBuffer();
        xml.append("<elements>\r\n");
        for (Shape e : shapes) {
            xml.append(((Externalizable) e).toXML());
        }
        xml.append("</elements>\r\n");
        return xml;
    }


    public void parse(Node node) throws XPathExpressionException, ParserConfigurationException {
//        Element e = (Element) node;
//        this.setSize(e.hasAttribute("width") ?
//                     (Integer.parseInt(e.getAttribute("width")) != 1 ? Integer.parseInt(e.getAttribute("width")) :
//                      500) : 500,
//                     e.hasAttribute("height") ?
//                     (Integer.parseInt(e.getAttribute("height")) != 1 ? Integer.parseInt(e.getAttribute("height")) :
//                      500) : 500);
//        NodeList nlist = ((Element) node).getElementsByTagName("name");
//        this.unitName = nlist.item(0).getTextContent();
//
//        nlist = ((Element) node).getElementsByTagName("footprint");
//        if (nlist.item(0) != null) {
//            e = (Element) nlist.item(0);
//            packaging.setFootprintLibrary(e.getAttribute("library"));
//            packaging.setFootprintCategory(e.getAttribute("category"));
//            packaging.setFootprintFileName(e.getAttribute("filename"));
//            packaging.setFootprintName(e.getAttribute("name"));
//        }
//        NodeList nodelist = ((Element) node).getElementsByTagName("reference");
//        Node n = nodelist.item(0);
//        if (n != null && !n.getTextContent().equals("")) {
//            Element ref=(Element)n;  
//            NodeList refList=ref.getElementsByTagName("label");
//            
//            FontLabel label = new FontLabel();
//            label.getTexture().setTag("reference");
//            if(refList.getLength()==0){
//               label.fromXML(n);                //old schema
//            }else{
//               label.fromXML(refList.item(0));    //new schema 
//            }
//            Add(label);
//        }
//        nodelist = ((Element) node).getElementsByTagName("unit");
//        n = nodelist.item(0);
//        if (n != null && !n.getTextContent().equals("")) {
//            Element unit=(Element)n;  
//            NodeList unitList=unit.getElementsByTagName("label");
//
//            FontLabel label = new FontLabel();
//            label.getTexture().setTag("unit");
//            if(unitList.getLength()==0){
//               label.fromXML(n);                //old schema
//            }else{
//               label.fromXML(unitList.item(0));    //new schema 
//            }
//            Add(label);
//        }
//        parseSelection(node, false);
    }

    private void parseSelection(Node node, boolean selection) throws XPathExpressionException,
                                                                     ParserConfigurationException {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        NodeList nodelist = (NodeList) xpath.evaluate("./elements/*", node, XPathConstants.NODESET);

        for (int i = 0; i < nodelist.getLength(); i++) {
            Node n = nodelist.item(i);
            Shape shape = shapeFactory.createShape(n);
            shape.setSelected(selection);
            this.add(shape);
        }

    }

    protected void parseClipboardSelection(String xml) throws XPathExpressionException, ParserConfigurationException,
                                                              SAXException, IOException {
        Node node =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement();
        parseSelection(node, true);
        setTextLayoutVisibility(isTextLayoutVisible);

    }

    @Override
    public void setType(Typeable.Type type) {
        this.type = type;
    }

    @Override
    public Typeable.Type getType() {
        return type;
    }

//    @Override
//    public Packaging getPackaging() {
//        return packaging;
//    }
//
//    @Override
//    public String toString() {
//        return "symbol";
//    }


    }


