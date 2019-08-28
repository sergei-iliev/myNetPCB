package com.mynetpcb.core.capi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 *Interface to describe everything that is drawable.
 * @author Sergey Iliev
 */
public interface Drawable {
    
    /**
     *
     * @param g2 the canvas to draw upon
     * @param viewportWindow visible rectangle of the pcb board.
     * @param scale current zoom.
     * @param everything is drawn over a layer
     */
    public void Paint(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale,int layermask);
    
    /**
     *
     * @param x coord of mouse click
     * @param y coord of mouse click
     * @return if shape is clicked upon
     */
    public boolean isClicked(int x,int y);
    
    /**
     * Check if shape falls within rectangle
     * @param r to check against
     * @return 
     */
    public boolean isInRect(Rectangle r);
    
    /**
     * Represents outline shape of the figure as drawn by the rendering engine.
     * @return shape's bounding rectangle. Must be the same as cacheable rectangle 
     */
    public Shape getBoundingShape();
    
    /**
     *
     * @param isSelected flag to mark shape as un/selected
     */
    public void setSelected(boolean isSelected);
    
    
    public boolean isSelected();
    
    public Color getFillColor();
    
    public void setFillColor(Color color);

}
