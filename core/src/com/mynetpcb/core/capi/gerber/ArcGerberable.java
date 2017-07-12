package com.mynetpcb.core.capi.gerber;

import java.awt.Point;
import java.awt.geom.Point2D;

/*
 * Gerber needs following arc capabilities
 */
public interface ArcGerberable {
    
    public Point2D getStartPoint();
    
    //public void setStartPoint(Point startPoint);
    
    public Point2D getEndPoint();
    
    
    public Point getCenterPoint();
    
    public int getI();
    
    public int getJ();
    //public void setEndPoint(Point endPoint);

    //public Point getResizingPoint();
    
    //public void setResizingPoint(Point resizingPoint);
    
    public boolean isSingleQuadrant();

    public boolean isClockwise();
}
