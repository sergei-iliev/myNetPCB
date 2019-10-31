package com.mynetpcb.pad.unit;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Label;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.pad.shape.FootprintShapeFactory;
import com.mynetpcb.pad.shape.GlyphLabel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.lang.ref.WeakReference;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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


public class Footprint extends Unit<Shape> {

    public Footprint(int width, int height) {
        super(width, height);
        this.shapeFactory = new FootprintShapeFactory();
        grid.setGridUnits(0.8, Grid.Units.MM);
        this.grid.setPointsColor(Color.WHITE);
        this.frame.setFillColor(Color.WHITE);
        scalableTransformation.Reset(0.5, 10, 4, 13);
        scalableTransformation.setScaleFactor(10);
        this.getCoordinateSystem().setSelectionRectWidth(3000);
    }

    public Footprint clone() throws CloneNotSupportedException {
        Footprint copy = (Footprint) super.clone();
        //copy.shapeFactory = new FootprintShapeFactory();
        return copy;
    }

    private WeakReference<PrintContext> context;

    @Override
    public void prepare(PrintContext context) {
        this.context = new WeakReference<>(context);
    }

    @Override
    public int getNumberOfPages() {
        return 1;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        AffineTransform oldTransform = g2d.getTransform();
        g2d.scale((72d / 254000d), (72d / 254000d));
        for (Shape shape : getShapes()) {
            shape.print(g2d, context.get(), context.get().getLayermaskId());
        }

        g2d.setTransform(oldTransform);
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    @Override
    public void finish() {
        context.clear();
        context = null;
    }

    public StringBuffer format() {
        StringBuffer xml = new StringBuffer();
        xml.append("<footprint width=\"" + this.getWidth() + "\" height=\"" + this.getHeight() + "\">\r\n");
        xml.append("<name>" + this.unitName + "</name>\r\n");
        //***reference
        GlyphLabel text = (GlyphLabel) FootprintMgr.getInstance().getLabelByTag(this, "reference");
        if (text != null) {
            xml.append("<reference layer=\"" + text.getCopper().getName() + "\" >");
            xml.append(text.getTexture().toXML());
            xml.append("</reference>\r\n");
        }
        //value
        text = (GlyphLabel) FootprintMgr.getInstance().getLabelByTag(this, "value");
        if (text != null) {
            xml.append("<value layer=\"" + text.getCopper().getName() + "\" >");
            xml.append(text.getTexture().toXML());
            xml.append("</value>\r\n");
        }
        xml.append("<units raster=\"" + this.getGrid().getGridValue() + "\">" + this.getGrid().getGridUnits() +
                   "</units>\r\n");
        
        //exclude ref and value tags
        List shapes=getShapes().stream().filter(s->{
            if(s instanceof Label){
                if(((Label)s).getTexture().getTag().equals("reference")||((Label)s).getTexture().getTag().equals("value")){
                   return false; 
                }else{
                   return true; 
                }                
            }else{
                return true;
            }
        }).collect(Collectors.toList());
        
        xml.append(format(shapes));
        xml.append("</footprint>");
        return xml;
    }

    protected StringBuffer format(Collection<Shape> shapes) {
        StringBuffer xml = new StringBuffer();

        xml.append("<shapes>\r\n");
        for (Shape e : shapes) {
            if (e instanceof Externalizable)
                xml.append(((Externalizable) e).toXML());
        }
        xml.append("</shapes>\r\n");

        return xml;
    }


    public void parse(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element e = (Element) node;
        this.setSize(e.hasAttribute("width") ?
                     (Integer.parseInt(e.getAttribute("width")) != 1 ? Integer.parseInt(e.getAttribute("width")) :
                      500) : 500,
                     e.hasAttribute("height") ?
                     (Integer.parseInt(e.getAttribute("height")) != 1 ? Integer.parseInt(e.getAttribute("height")) :
                      500) : 500);
        NodeList nlist = ((Element) node).getElementsByTagName("name");
        this.unitName = nlist.item(0).getTextContent();

        nlist = ((Element) node).getElementsByTagName("units");
        this.getGrid().setGridUnits(Double.parseDouble(((Element) nlist.item(0)).getAttribute("raster")),
                                    Grid.Units.MM);

        nlist = ((Element) node).getElementsByTagName("reference");
        Node n = nlist.item(0);
        if (n != null && !n.getTextContent().equals("")) {
            GlyphLabel label = new GlyphLabel();
            label.fromXML(n);
            label.getTexture().setTag("reference");
            add(label);
        }
        nlist = ((Element) node).getElementsByTagName("value");
        n = nlist.item(0);
        if (n != null && !n.getTextContent().equals("")) {
            GlyphLabel label = new GlyphLabel();
            label.fromXML(n);
            label.getTexture().setTag("value");
            add(label);
        }

        parseSelection(node, false);
    }

    public void parseSelection(Node node, boolean selection) throws XPathExpressionException,
                                                                    ParserConfigurationException {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NodeList nodelist = (NodeList) xpath.evaluate("./shapes/*", node, XPathConstants.NODESET);

        for (int i = 0; i < nodelist.getLength(); i++) {
            Shape shape = this.shapeFactory.createShape(nodelist.item(i));
            shape.setSelected(selection);
            this.add(shape);
        }
    }

    protected void parseClipboardSelection(String xml) throws XPathExpressionException, ParserConfigurationException,
                                                              SAXException, IOException {
        Node node =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement();
        parseSelection(node, true);

    }


    @Override
    public String toString() {
        return "footprint";
    }

}

