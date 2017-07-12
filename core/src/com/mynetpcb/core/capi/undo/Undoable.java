package com.mynetpcb.core.capi.undo;


public interface Undoable {
    
  public boolean Redo();
    
  public boolean Undo(UndoCallback undocallback);
  
  public  void registerMemento(AbstractMemento memento);
  
}