package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Arc;

public class PCBArc extends Arc implements PCBShape{
    public PCBArc(int x,int y,int width,int thickness,int layermaskid) {
        super(x,y,width, thickness,layermaskid);
    }

    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);  
    }
    
    public static class Memento extends Arc.Memento{
        public Memento(MementoType mementoType) {
           super(mementoType);            
        }
    }
}
