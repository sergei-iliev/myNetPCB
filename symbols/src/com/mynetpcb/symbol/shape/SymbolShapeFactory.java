package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;

import org.w3c.dom.Node;

public class SymbolShapeFactory  implements AbstractShapeFactory{

    @Override
    public Shape createShape(Node node) {
        // TODO Implement this method
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
//        if(memento instanceof Arc.Memento){
//            Arc arc=new Arc();
//            arc.setState(memento);
//            return arc;
//        }
//        if(memento instanceof Triangle.Memento){
//            Triangle triangle=new Triangle();
//            triangle.setState(memento);
//            return triangle;
//        }         
        if(memento instanceof RoundRect.Memento){
            RoundRect rect=new RoundRect(1);
            rect.setState(memento);
            return rect;
        }       
        throw new IllegalStateException("Unknown memento type: "+memento.getClass().getCanonicalName());
    }
}
