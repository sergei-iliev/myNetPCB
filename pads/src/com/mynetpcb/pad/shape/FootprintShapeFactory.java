package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FootprintShapeFactory implements AbstractShapeFactory{

    @Override
    public Shape createShape(Node node) {
            Element element=(Element)node;
            if(element.getTagName().equals("pad")){
                Pad pad = new Pad(0,0);
                pad.fromXML(node);
                return pad;   
            }
            if(element.getTagName().equals("line")){
                Line line = new Line(0, 0);
                line.fromXML(node);
                return line;   
            }
            if(element.getTagName().equals("rectangle")){
                RoundRect roundRect = new RoundRect();
                roundRect.fromXML(node);
                return roundRect;   
            }
            if(element.getTagName().equals("ellipse")){
                Circle circle = new Circle(0,0,0,0,0);
                circle.fromXML(node);
                return circle;   
            }
            if(element.getTagName().equals("circle")){
                Circle circle = new Circle(0,0,0,0,0);
                circle.fromXML(node);
                return circle;   
            }            
            if(element.getTagName().equals("arc")){
                Arc arc = new Arc(0,0,0,0,0,0,0);
                arc.fromXML(node);
                return arc;   
            }
            if(element.getTagName().equals("solidregion")){
                SolidRegion region = new SolidRegion(0);
                region.fromXML(node);
                return region;   
            }
            if(element.getTagName().equals("label")){
                GlyphLabel label = new GlyphLabel();
                label.fromXML(node);
                return label;   
            }
            throw new IllegalStateException("Unkown node "+element.getTagName());
        }

    @Override
    public Shape createShape(AbstractMemento memento) {   
            if(memento instanceof Arc.Memento){
                Arc arc=new Arc(0,0,0,0,0,0,0);  
                arc.setState(memento);
                return arc;
            }
            if(memento instanceof Circle.Memento){
                Circle circle=new Circle(0,0,0,0,0); 
                circle.setState(memento);
                return circle;   
            }
            if(memento instanceof GlyphLabel.Memento){
                GlyphLabel label=new GlyphLabel();  
                label.setState(memento);
                return label;             
            }
            if(memento instanceof Line.Memento){
                Line line=new Line(0,0);  
                line.setState(memento);
                return line;    
            }
            if(memento instanceof Pad.Memento){
                Pad pad=new Pad(0,0);  
                pad.setState(memento);
                return pad;               
            }
            if(memento instanceof SolidRegion.Memento){
                SolidRegion region = new SolidRegion(0);
                region.setState(memento);
                return region;   
            }
            if(memento instanceof RoundRect.Memento){
                RoundRect roundRect=new RoundRect();  
                roundRect.setState(memento);
                return roundRect;             
            }
            throw new IllegalStateException("Unknown memento type: "+memento.getClass().getCanonicalName()); 
        
    }
}


