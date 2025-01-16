package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Hole;

public class PCBHole extends Hole implements PCBShape{
 
    


    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }






    static class Memento extends Hole.Memento{              
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

   }      
}
