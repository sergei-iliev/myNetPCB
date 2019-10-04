package com.mynetpcb.core.capi.gerber;

import com.mynetpcb.d2.shapes.Point;


/*
 * Gerber needs following arc capabilities
 */
public interface ArcGerberable {
    
    public Point getStartPoint();
    
    public Point getEndPoint();
    
    
    public Point getCenter();
    
    public int getI();
    
    public int getJ();
    
    public boolean isSingleQuadrant();

    public boolean isClockwise();
}
