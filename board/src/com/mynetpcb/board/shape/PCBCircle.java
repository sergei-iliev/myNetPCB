package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Circle;

public class PCBCircle extends Circle implements PCBShape{
    public PCBCircle(double x,double y,double r,int thickness,int layermaskId) {
        super(x,y,r,thickness,layermaskId);
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public static class Memento extends Circle.Memento {

        public Memento(MementoType mementoType) {
            super(mementoType);
        }

    }    
}
