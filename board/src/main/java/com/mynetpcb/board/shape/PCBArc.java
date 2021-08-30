package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Arc;

public class PCBArc extends Arc implements PCBShape{
    public PCBArc(double x,double y,double r,double startAngle,double endAngle,int thickness,int layermaskid){ 
        super(x,y,r,startAngle,endAngle,thickness,layermaskid);
    }

    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }
    
    public static class Memento extends Arc.Memento{
        public Memento(MementoType mementoType) {
           super(mementoType);            
        }
    }
}
