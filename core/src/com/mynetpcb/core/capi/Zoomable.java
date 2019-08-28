package com.mynetpcb.core.capi;

import java.awt.Point;

/**
 *Defines zoom capabilities around a point
 * @author Sergey Iliev
 */
public interface Zoomable {
    
 public boolean ZoomIn(Point scaleOriginePoint);
    
 public boolean ZoomOut(Point scaleOriginePoint);
 
}
