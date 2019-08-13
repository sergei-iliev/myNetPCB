package com.mynetpcb.pad.shape;


import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.pad.unit.Footprint;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FootprintShapeFactory implements AbstractShapeFactory<Footprint,Shape>{

    @Override
    public Shape createShape(Node node) {
            Element element=(Element)node;
            if(element.getTagName().equals("pad")){
                Pad pad = new Pad();
                pad.fromXML(node);
                return pad;   
            }
            if(element.getTagName().equals("line")){
                Line line = new Line();
                line.fromXML(node);
                return line;   
            }
            if(element.getTagName().equals("rectangle")){
                RoundRect roundRect = new RoundRect();
                roundRect.fromXML(node);
                return roundRect;   
            }
            if(element.getTagName().equals("ellipse")){
                Circle circle = new Circle();
                circle.fromXML(node);
                return circle;   
            }
            if(element.getTagName().equals("arc")){
                Arc arc = new Arc();
                arc.fromXML(node);
                return arc;   
            }
            if(element.getTagName().equals("label")){
                GlyphLabel label = new GlyphLabel();
                label.fromXML(node);
                return label;   
            }
            throw new IllegalStateException("Unkown node "+element.getTagName());
        }

    @Override
    public Shape createShape(Footprint unit, AbstractMemento memento) {   
            if(memento instanceof Arc.Memento){
                Arc arc=new Arc();  
                arc.setState(memento);
                return arc;
            }
            if(memento instanceof Circle.Memento){
                Circle circle=new Circle();  
                circle.setState(memento);
                return circle;   
            }
            if(memento instanceof GlyphLabel.Memento){
                GlyphLabel label=new GlyphLabel();  
                label.setState(memento);
                return label;             
            }
            if(memento instanceof Line.Memento){
                Line line=new Line();  
                line.setState(memento);
                return line;    
            }
            if(memento instanceof Pad.Memento){
                Pad pad=new Pad();  
                pad.setState(memento);
                return pad;               
            }
            if(memento instanceof RoundRect.Memento){
                RoundRect roundRect=new RoundRect();  
                roundRect.setState(memento);
                return roundRect;             
            }
            throw new IllegalStateException("Unknown memento type: "+memento.getClass().getCanonicalName()); 
        
    }
}
