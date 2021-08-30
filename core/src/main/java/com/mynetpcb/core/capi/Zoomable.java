package com.mynetpcb.core.capi;

import java.awt.Point;

/**
 *Defines zoom capabilities around a point
 * @author Sergey Iliev
 */
public interface Zoomable {
    
 public boolean zoomIn(Point scaleOriginePoint);
    
 public boolean zoomOut(Point scaleOriginePoint);
 
}
