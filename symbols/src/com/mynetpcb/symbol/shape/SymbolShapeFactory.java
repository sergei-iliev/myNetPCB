package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.symbol.unit.Symbol;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SymbolShapeFactory implements AbstractShapeFactory<Symbol,Shape>{

    @Override
    public Shape createShape(Node node) {
            Element element=(Element)node; 
            if (element.getTagName().equals("rectangle")) {
                RoundRect rect = new RoundRect();
                rect.fromXML(node);
                return rect;
            }

            if (element.getTagName().equals("label")) {
                FontLabel label = new FontLabel();
                label.fromXML(node);
                return label;
            }

            if (element.getTagName().equals("triangle")) {
                Triangle triangle = new Triangle();
                triangle.fromXML(node);
                return triangle;
            }
            if (element.getTagName().equals("arc")) {
                Arc arc = new Arc();
                arc.fromXML(node);
                return arc;
            }
            if (element.getTagName().equals("arrow")) {
                Arrow arrow = new Arrow();
                arrow.fromXML(node);
                return arrow;
            }
            if (element.getTagName().equals("ellipse")) {
                Ellipse ellipse = new Ellipse();
                ellipse.fromXML(node);
                return ellipse;
            }
            if (element.getTagName().equals("line")) {
                Line line = new Line();
                line.fromXML(node);
                return line;
            }
            if (element.getTagName().equals("pin")) {
                Pin pin = new Pin();
                pin.fromXML(node);
                return pin;
            }
            return null;       
    }

    @Override
    public Shape createShape(Symbol symbol, AbstractMemento memento) {
        if(memento instanceof Arrow.Memento){
           Arrow arrow=new Arrow();
           arrow.setState(memento);
           return arrow;
        }        
        
        if(memento instanceof Line.Memento){
           Line line=new Line();
           line.setState(memento);
           return line;
        }
        
        if(memento instanceof Pin.Memento){
          Pin pin=new Pin();          
          pin.setState(memento);
          return pin;          
        }
        
        if(memento instanceof FontLabel.Memento){
            FontLabel label=new FontLabel();          
            label.setState(memento);  
            return label;             
        }      
        if(memento instanceof Ellipse.Memento){
            Ellipse ellipse=new Ellipse();
            ellipse.setState(memento);
            return ellipse;
        }
        if(memento instanceof Arc.Memento){
            Arc arc=new Arc();
            arc.setState(memento);
            return arc;
        }
        if(memento instanceof Triangle.Memento){
            Triangle triangle=new Triangle();
            triangle.setState(memento);
            return triangle;
        }         
        if(memento instanceof RoundRect.Memento){
            RoundRect rect=new RoundRect();
            rect.setState(memento);
            return rect;
        }       
        throw new IllegalStateException("Unknown memento type: "+memento.getClass().getCanonicalName());
    }
      
}
