package com.mynetpcb.core.capi.shape;


import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.unit.Unit;

import org.w3c.dom.Node;

public interface AbstractShapeFactory {
    
    public Shape createShape(Node node);
    
    public Shape  createShape(AbstractMemento memento); 
}
