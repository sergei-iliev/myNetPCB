package com.mynetpcb.core.capi;

import com.mynetpcb.d2.shapes.Box;

import java.awt.Color;
import java.awt.Graphics2D;
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
    public void paint(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale,int layermask);
    
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
    public boolean isInRect(Box r);
    
    /**
     * Represents outline shape of the figure as drawn by the rendering engine.
     * @return shape's bounding rectangle. Must be the same as cacheable rectangle 
     */
    public Box getBoundingShape();
    
    /**
     *
     * @param isSelected flag to mark shape as un/selected
     */
    public void setSelected(boolean isSelected);
    
    
    public boolean isSelected();
    
    public Color getFillColor();
    
    public void setFillColor(Color color);

}
