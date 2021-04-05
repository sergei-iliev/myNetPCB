package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Line;

public class PCBLine extends Line implements PCBShape{
    public PCBLine(int thickness,int layermaskId) {
        super(thickness,layermaskId);
    }
    
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }


    
    public static class Memento extends Line.Memento {
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }
    }           
}
