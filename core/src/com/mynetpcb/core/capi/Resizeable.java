package com.mynetpcb.core.capi;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

/**
 *In the context of Symbol/Pad construction, shapes can change their size/shape by dragging control points.
 * Like line,rect,ellipse
 * @author Sergey Iliev
 */
public interface Resizeable {
  
  /**
     *Check if shapes'control rect/spot is clicked
     * @return the clicked point
     */
  public Point isControlRectClicked(int x, int y);
  
  /**
     * There is always a point under the mouse pointer,which is being moved/dragged
     * @return the ppoint which is directly controled by the mouse pointer
     */
  public Point getResizingPoint();
  /**
     *set the point which is being resized and part of the shape
     * @param point
     */
  public void setResizingPoint(Point point);
  /**
     * Dragging the mouse pointer causes the shape to resize
     * @param x offset
     * @param y offset
     * @param clickedPoint dragged point
     */
   public void Resize(int xOffset, int yOffset,Point clickedPoint);
  
  /**
     * Draw control resizing points
     * @param g2
     * @param viewportWindow
     * @param scale
     */
   public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale);
  
   /**
    * Align resizing point to grid raster
    */
   public void alignResizingPointToGrid(Point targetPoint);
}

