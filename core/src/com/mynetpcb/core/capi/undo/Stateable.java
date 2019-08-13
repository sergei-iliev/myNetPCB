package com.mynetpcb.core.capi.undo;

public interface Stateable<M extends AbstractMemento> {
    
    public M getState(MementoType operationType);
    
    public void setState(M memento);

}
