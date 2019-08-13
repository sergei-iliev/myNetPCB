package com.mynetpcb.core.capi.event;

import java.awt.event.ItemListener;


public interface ShapeEventDispatcher {
    
public void fireShapeEvent(ShapeEvent e);

public void addShapeListener(ShapeListener listener);

public void removeShapeListener(ShapeListener listener);

}

