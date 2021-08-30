package com.mynetpcb.core.capi.undo;


import com.mynetpcb.core.capi.shape.Shape;

import com.mynetpcb.core.capi.unit.Unit;

import java.util.Collection;
import java.util.List;

public interface MementoContainer<M extends  AbstractMemento,S extends Shape> {
    
  public List<AbstractMemento> getMementoList();
  
  public M add(Collection<S> shapes);

  public void loadStateTo(Unit unit); 

}
