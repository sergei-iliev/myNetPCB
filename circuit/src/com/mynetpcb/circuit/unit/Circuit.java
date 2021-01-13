package com.mynetpcb.circuit.unit;

import com.mynetpcb.circuit.shape.CircuitShapeFactory;
import com.mynetpcb.circuit.shape.SCHBus;
import com.mynetpcb.circuit.shape.SCHBusPin;
import com.mynetpcb.circuit.shape.SCHConnector;
import com.mynetpcb.circuit.shape.SCHJunction;
import com.mynetpcb.circuit.shape.SCHLabel;
import com.mynetpcb.circuit.shape.SCHNoConnector;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.shape.SCHWire;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.lang.ref.WeakReference;

import java.util.Collection;

import javax.imageio.ImageIO;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class Circuit extends Unit<Shape>{
    public Circuit(int width,int height) {
        super(width, height);
        this.grid.setGridUnits(8, Grid.Units.PIXEL);
        this.grid.setPointsColor(Color.BLACK);
        this.frame.setOffset(8);
        this.shapeFactory = new CircuitShapeFactory();
    }
    @Override
    public Circuit clone() throws CloneNotSupportedException {
        Circuit copy = (Circuit) super.clone();
        this.shapeFactory = new CircuitShapeFactory();   
        return copy; 
    }
    @Override
    public StringBuffer format() {        
        StringBuffer xml = new StringBuffer();
        xml.append("<circuit width=\"" + this.getWidth() + "\" height=\"" + this.getHeight() + "\">\r\n");
        xml.append("<name>" + this.getUnitName() + "</name>\r\n");
        xml.append(format(getShapes()));        
        xml.append("</circuit>\r\n");
        return xml;
    }

    @Override
    protected StringBuffer format(Collection<Shape> shapes) {
        StringBuffer xml = new StringBuffer();

        xml.append("<symbols>\r\n");
        //***   Chip symbols
        xml.append("<chips>\r\n");
        for (Shape shape : shapes) {
            if (shape instanceof SCHSymbol)
                xml.append(((Externalizable) shape).toXML());
        }
        xml.append("</chips>\r\n");

        //***   Bus symbols
        xml.append("<busses>\r\n");
        for (Shape shape : shapes) {
            if (shape instanceof SCHBus)
                xml.append(((Externalizable) shape).toXML());
        }
        xml.append("</busses>\r\n");

        //***  BusPins
        xml.append("<buspins>\r\n");
        for (Shape shape : shapes) {
            if (shape instanceof SCHBusPin)
                xml.append(((Externalizable) shape).toXML());
        }
        xml.append("</buspins>\r\n");

        //***   Wire symbols
        xml.append("<wires>\r\n");
        for (Shape shape : shapes) {
            if ((shape instanceof SCHWire) && !(shape instanceof SCHBus) && !(shape instanceof SCHBusPin))
                xml.append(((Externalizable) shape).toXML());
        }
        xml.append("</wires>\r\n");

        //***   Junction symbols
        xml.append("<junctions>\r\n");
        for (Shape shape : shapes) {
            if (shape instanceof SCHJunction)
                xml.append(((Externalizable) shape).toXML());
        }
        xml.append("</junctions>\r\n");

        //***  Labels without parent
        xml.append("<labels>\r\n");
        for (Shape shape : shapes) {
            if (shape instanceof SCHLabel) {
                xml.append(((Externalizable) shape).toXML());
            }
        }
        xml.append("</labels>\r\n");

        //***  Connections without parent
        xml.append("<connectors>\r\n");
        for (Shape shape : shapes) {
            if (shape instanceof SCHConnector) {
                xml.append(((Externalizable) shape).toXML());
            }
        }
        xml.append("</connectors>\r\n");

        //noconnectors ending
        xml.append("<noconnectors>\r\n");
        for (Shape shape : shapes) {
            if (shape instanceof SCHNoConnector) {
                xml.append(((Externalizable) shape).toXML());
            }
        }
        xml.append("</noconnectors>\r\n");
        
//        xml.append("<netlabels>\r\n");
//        for (Shape shape : shapes) {
//            if (shape instanceof SCHNetLabel) {
//                xml.append(((Externalizable) shape).toXML());
//            }
//        }
//        xml.append("</netlabels>\r\n");
        
        xml.append("</symbols>\r\n");
        return xml;
    }

    @Override
    public void parse(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element e = (Element) node;
        this.setSize(Integer.parseInt(e.getAttribute("width")), Integer.parseInt(e.getAttribute("height")));
        NodeList nlist = ((org.w3c.dom.Element) node).getElementsByTagName("name");
        this.unitName = nlist.item(0).getTextContent();
        parseSelection(node, false);

    }
    private void parseSelection(Node node, boolean selection) throws XPathExpressionException,
                                                                     ParserConfigurationException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        NodeList nodelist, subnodelist;
        Node item, subitem;

        nodelist = (NodeList) xpath.evaluate("./symbols/chips/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHSymbol chip = new SCHSymbol();
            chip.setSelected(selection);
            chip.fromXML(item);
            this.add(chip);

            //***read children labels if any
            subnodelist = (NodeList) xpath.evaluate("./children/*", item, XPathConstants.NODESET);
            for (int j = 0; j < subnodelist.getLength(); j++) {
                subitem = subnodelist.item(j);

                if (((Element) subitem).getTagName().equals("label")) {
                    SCHLabel label = new SCHLabel();
                    label.setSelected(selection);
                    label.fromXML(subitem);                    
                    this.add(label);
                }

                if (((Element) subitem).getTagName().equals("connector")) {
                    SCHConnector connector = new SCHConnector();
                    connector.setSelected(selection);
                   // connector.fromXML(subitem);
                    this.add(connector);
                }
            }
        }

        //***read busses
        nodelist = (NodeList) xpath.evaluate("./symbols/busses/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHBus buss = new SCHBus();
            buss.setSelected(selection);
            buss.fromXML(item);
            this.add(buss);
        }
        //***read buspins
        nodelist = (NodeList) xpath.evaluate("./symbols/buspins/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHBusPin busPin = new SCHBusPin();
            busPin.setSelected(selection);
            busPin.fromXML(item);
            this.add(busPin);
        }
        //***read wires
        nodelist = (NodeList) xpath.evaluate("./symbols/wires/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHWire wire = new SCHWire();
            wire.setSelected(selection);
            wire.fromXML(item);
            this.add(wire);
        }

        //***read junctions
        nodelist = (NodeList) xpath.evaluate("./symbols/junctions/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHJunction junction = new SCHJunction();
            junction.setSelected(selection);
            junction.fromXML(item);
            this.add(junction);
        }

        //***read free labels
        nodelist = (NodeList) xpath.evaluate("./symbols/labels/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHLabel label = new SCHLabel();
            label.setSelected(selection);
            label.fromXML(item);
            this.add(label);
        }
        //***read connectors
        nodelist = (NodeList) xpath.evaluate("./symbols/connectors/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHConnector connector = new SCHConnector();
            connector.setSelected(selection);
            connector.fromXML(item);
            this.add(connector);
        }

        //***read noconnectors
        nodelist = (NodeList) xpath.evaluate("./symbols/noconnectors/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHNoConnector connector = new SCHNoConnector();
            connector.setSelected(selection);
            connector.fromXML(item);
            this.add(connector);
        }
//
//        //***read noconnectors
//        nodelist = (NodeList) xpath.evaluate("./symbols/netlabels/*", node, XPathConstants.NODESET);
//        for (int i = 0; i < nodelist.getLength(); i++) {
//            item = nodelist.item(i);
//            SCHNetLabel netlabel = new SCHNetLabel();
//            netlabel.setSelected(selection);
//            netlabel.fromXML(item);
//            this.Add(netlabel);
//        }
    }

    @Override
    protected void parseClipboardSelection(String xml) throws XPathExpressionException, ParserConfigurationException,
                                                                 SAXException, IOException {
    
        Node node =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement();
        parseSelection(node, true);
        
    }

    private WeakReference<PrintContext> context;
    private Circuit printcircuit;
    
    @Override
    public void prepare(PrintContext context) {        
        printcircuit=new Circuit(0,0);
        this.context = new WeakReference<>(context);
        for (Shape shape : shapes) {
            try {
                Shape copy = shape.clone();
                copy.setSelected(false);
                printcircuit.add(copy);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
    }
    @Override
    public void export(String fileName,PrintContext context)throws IOException{
                  double scale=(context.getCustomSizeRatio());
                  int width = (int)(this.getWidth() *scale);
                  int height = (int)(this.getHeight() * scale);            
                  BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    
                    
                  Graphics2D g2 = (Graphics2D)bi.getGraphics();
                 
                  g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                  g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                  g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                  g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                  g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                  g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
                  
                  g2.setColor(Color.WHITE);
                  g2.fillRect(0, 0, width, height);
                  
                  g2.scale(scale, scale);
                  for (Shape shape : printcircuit.getShapes()) {
                        shape.print(g2,context,context.getLayermaskId());
                  }
                  String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                  ImageIO.write(bi,ext,new File(fileName));                        
    }
    @Override
    public int print(Graphics g, PageFormat pf, int page) {

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


        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());
        AffineTransform oldTransform = g2.getTransform();
        
        double scale=1;
        if(context.get().getCustomSizeRatio()==-1){//fit to page
          scale = Math.min(pf.getImageableWidth() / this.getWidth(),
                                pf.getImageableHeight() / this.getHeight());
        }else if(context.get().getCustomSizeRatio()!=1){ //custom ratio
            scale=context.get().getCustomSizeRatio();  
        }
        
        if(scale<1){
            g2.scale(scale, scale);   
        }
        
        for (Shape shape : printcircuit.getShapes()) {
            shape.print(g2,context.get(),Layer.LAYER_ALL);
        }
        g2.setTransform(oldTransform);
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
    
    @Override
    public void finish() {
        context.clear();
        context = null; 
        printcircuit.clear();
        printcircuit = null;
    }

}
