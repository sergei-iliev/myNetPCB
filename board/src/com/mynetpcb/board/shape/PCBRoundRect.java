package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.shape.ResizableShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.pad.shape.RoundRect;

public class PCBRoundRect extends RoundRect implements PCBShape{

    public PCBRoundRect(int x,int y,int width,int height,int arc,int thickness,int layermaskid) {
       super(x,y,width,height,arc,thickness, layermaskid);   
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
    
    public static class Memento extends ResizableShape.Memento {
        private int arc;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(ResizableShape shape) {
            super.loadStateTo(shape);
            ((PCBRoundRect)shape).arc=arc;
        }
        
        @Override
        public void saveStateFrom(ResizableShape shape) {
            super.saveStateFrom(shape);
            this.arc=((PCBRoundRect)shape).arc;
        }
        
        @Override
        public boolean equals(Object obj){
            if(this==obj){
              return true;  
            }
            if(!(obj instanceof Memento)){
              return false;  
            }
            Memento other=(Memento)obj;
            return super.equals(obj)&&
                    this.arc==other.arc;
        }
        
        @Override
        public int hashCode(){
           int hash=1; 
           hash=super.hashCode()+arc;
           return hash;  
        }
        @Override
        public boolean isSameState(Unit unit) {
            boolean flag= super.isSameState(unit);
            PCBRoundRect other=(PCBRoundRect)unit.getShape(this.getUUID());
            return other.arc==this.arc&&flag;
        }
    }    
}
