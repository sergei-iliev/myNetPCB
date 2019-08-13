package com.mynetpcb.core.capi.undo;


import com.mynetpcb.core.capi.shape.Shape;

import java.util.Collection;
import java.util.List;

public interface MementoContainer<M extends  AbstractMemento,S extends Shape> {
    
  public List<AbstractMemento> getMementoList();
  
  public M Add(Collection<S> shapes);

}
