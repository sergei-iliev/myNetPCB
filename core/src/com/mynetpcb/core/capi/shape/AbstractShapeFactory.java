package com.mynetpcb.core.capi.shape;


import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.unit.Unit;

import org.w3c.dom.Node;

public interface AbstractShapeFactory<U extends Unit,S extends Shape> {
    
    public S createShape(Node node);
    
    public S  createShape(U unit,AbstractMemento memento); 
}
