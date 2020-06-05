package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SymbolShapeFactory  implements AbstractShapeFactory{

    @Override
    public Shape createShape(Node node) {
        Element element=(Element)node; 
        if (element.getTagName().equals("label")) {
            FontLabel label = new FontLabel();
            label.fromXML(node);
            return label;
        }
        if (element.getTagName().equals("rectangle")) {
            RoundRect rect = new RoundRect(1);
            rect.fromXML(node);
            return rect;
        }
        if (element.getTagName().equals("arrow")) {
            ArrowLine arrow = new ArrowLine(1);
            arrow.fromXML(node);
            return arrow;
        }        
        if (element.getTagName().equals("arc")) {
            Arc arc = new Arc(1);
            arc.fromXML(node);
            return arc;
        }  
        if (element.getTagName().equals("ellipse")) {
            Ellipse ellipse = new Ellipse(1);
            ellipse.fromXML(node);
            return ellipse;
        }         
        if (element.getTagName().equals("line")) {
            Line line = new Line(1);
            line.fromXML(node);
            return line;
        }
        if (element.getTagName().equals("triangle")) {
            Triangle triangle = new Triangle(1);
            triangle.fromXML(node);
            return triangle;
        }         
        return null;
    }

    @Override
    public Shape createShape(AbstractMemento memento) {
        if(memento instanceof ArrowLine.Memento){
           ArrowLine arrow=new ArrowLine(1);
           arrow.setState(memento);
           return arrow;
        }        
        
        if(memento instanceof Line.Memento){
           Line line=new Line(1);
           line.setState(memento);
           return line;
        }
        
//        if(memento instanceof Pin.Memento){
//          Pin pin=new Pin();          
//          pin.setState(memento);
//          return pin;          
//        }
        
        if(memento instanceof FontLabel.Memento){
            FontLabel label=new FontLabel();          
            label.setState(memento);  
            return label;             
        }      
        if(memento instanceof Ellipse.Memento){
            Ellipse ellipse=new Ellipse(1);
            ellipse.setState(memento);
            return ellipse;
        }
        if(memento instanceof Triangle.Memento){
            Triangle triangle=new Triangle(1);
            triangle.setState(memento);
            return triangle;
        }         
        if(memento instanceof RoundRect.Memento){
            RoundRect rect=new RoundRect(1);
            rect.setState(memento);
            return rect;
        }
        if(memento instanceof Arc.Memento){
            Arc arc=new Arc(1);
            arc.setState(memento);
            return arc;
        }        
        throw new IllegalStateException("Unknown memento type: "+memento.getClass().getCanonicalName());
    }
}
