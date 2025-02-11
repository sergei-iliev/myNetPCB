package com.mynetpcb.core.capi;

import com.mynetpcb.d2.shapes.Point;

import java.awt.Graphics2D;
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
  public default Point isControlRectClicked(double x, double y) {
	  throw new IllegalAccessError("TODO"); 
  }
  
  /**
   *  Check if shapes'control rect/spot is clicked in current zoom/scaled window
   *  It is needed for precise mouse click point over circle click
   * @param x
   * @param y
   * @param viewportWindow
   * @return
   */
  public default Point isControlRectClicked(double x, double y,ViewportWindow viewportWindow) {
	  throw new IllegalAccessError("TODO");
  }
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
   public void resize(double xOffset, double yOffset,Point clickedPoint);
  
   
   /**
    * Align resizing point to grid raster
    */
   public void alignResizingPointToGrid(Point targetPoint);
   
    /**
       * Draw control resizing points
       * @param g2
       * @param viewportWindow
       * @param scale
       */
    public default void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        
    }
    /**
     * Arc complient      
     */
    public default void resizeStartEndPoint(double xoffset,double yoffset,boolean isStartPoint) {
    	
    }
    /**
     * Arc complient      
     */
    public default void alignStartEndPointToGrid(boolean isStartPoint) {
    	
    }
}

