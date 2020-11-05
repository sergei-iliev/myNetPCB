package com.mynetpcb.circuit.unit;

import com.mynetpcb.circuit.shape.CircuitShapeFactory;
import com.mynetpcb.circuit.shape.SCHBus;
import com.mynetpcb.circuit.shape.SCHBusPin;
import com.mynetpcb.circuit.shape.SCHConnector;
import com.mynetpcb.circuit.shape.SCHJunction;
import com.mynetpcb.circuit.shape.SCHLabel;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.shape.SCHWire;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

import java.io.IOException;

import java.util.Collection;

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
        return null;
    }

    @Override
    protected StringBuffer format(Collection<Shape> collection) {
        // TODO Implement this method
        return null;
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
//
//        //***read free labels
//        nodelist = (NodeList) xpath.evaluate("./symbols/labels/*", node, XPathConstants.NODESET);
//        for (int i = 0; i < nodelist.getLength(); i++) {
//            item = nodelist.item(i);
//            SCHLabel label = new SCHLabel();
//            label.setSelected(selection);
//            //label.fromXML(item);
//            this.add(label);
//        }
        //***read connectors
        nodelist = (NodeList) xpath.evaluate("./symbols/connectors/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            item = nodelist.item(i);
            SCHConnector connector = new SCHConnector();
            connector.setSelected(selection);
            connector.fromXML(item);
            this.add(connector);
        }

//        //***read noconnectors
//        nodelist = (NodeList) xpath.evaluate("./symbols/noconnectors/*", node, XPathConstants.NODESET);
//        for (int i = 0; i < nodelist.getLength(); i++) {
//            item = nodelist.item(i);
//            SCHNoConnector connector = new SCHNoConnector();
//            connector.setSelected(selection);
//            connector.fromXML(item);
//            this.Add(connector);
//        }
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
    protected void parseClipboardSelection(String string) throws XPathExpressionException, ParserConfigurationException,
                                                                 SAXException, IOException {
        // TODO Implement this method

    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int i) throws PrinterException {
        // TODO Implement this method
        return 0;
    }
}
