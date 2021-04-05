package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.RoundRect;

public class PCBRoundRect extends RoundRect implements PCBShape{
    public PCBRoundRect(double x,double y,double width,double height,int arc,int thickness,int layermaskid) {
       super(x,y,width,height,arc,thickness, layermaskid);   
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }


    
    public static class Memento extends RoundRect.Memento {
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }
    }       
}
