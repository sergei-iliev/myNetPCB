package com.mynetpcb.core.capi.undo;

/**
 * track what is happening at undo/redo 
 */
public interface UndoCallback {

    public void onUndo();
}
