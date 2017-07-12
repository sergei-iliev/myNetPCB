package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Circle;

public class PCBCircle extends Circle implements PCBShape {

    public PCBCircle( int x, int y, int r, int thickness, int layermaskid) {
        super( x, y, r, thickness, layermaskid);
    }

    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        ((Memento) memento).loadStateTo(this);
    }

    public static class Memento extends Circle.Memento {

        public Memento(MementoType mementoType) {
            super(mementoType);
        }

    }
}
