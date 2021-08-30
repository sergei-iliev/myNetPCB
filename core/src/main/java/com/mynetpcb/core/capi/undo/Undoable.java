package com.mynetpcb.core.capi.undo;


public interface Undoable {
    
  public boolean redo();
    
  public boolean undo(UndoCallback undocallback);
  
  public  void registerMemento(AbstractMemento memento);
  
}