package com.mynetpcb.core.capi.event;

import java.util.EventListener;

public interface ShapeListener extends EventListener{

    public void selectShapeEvent(ShapeEvent e);

    public void deleteShapeEvent(ShapeEvent e);

    public void renameShapeEvent(ShapeEvent e);

    public void addShapeEvent(ShapeEvent e);

    public void propertyChangeEvent(ShapeEvent e);
}
