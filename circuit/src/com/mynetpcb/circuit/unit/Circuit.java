package com.mynetpcb.circuit.unit;

import com.mynetpcb.circuit.shape.CircuitShapeFactory;
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
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

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
        // TODO Implement this method

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
