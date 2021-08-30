package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.SolidRegion;

public class PCBSolidRegion extends SolidRegion implements PCBShape{
    public PCBSolidRegion(int layermaskId) {
        super(layermaskId);
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public static class Memento extends SolidRegion.Memento {

        public Memento(MementoType mementoType) {
            super(mementoType);
        }

    }        
}
