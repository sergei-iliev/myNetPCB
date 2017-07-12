package com.mynetpcb.core.capi.shape;


/**
 * Each drawable item must implement this to speed drawing
 * DRAW ONLY SHAPES IN VISIBLE RECTANGLE
 */
public interface BoundsCacheable {
   /**
     * Calculate bounding shape
     * @return  The bounding rectangle of the shape.
     */
   public java.awt.Shape calculateShape();
   
   /**
     * It is a helper method to turn off cacheing when a track point is dragged for example.
     * @param enable  or disable temporary the cacheing.
     */
   public void enableCache(boolean enable);
   
   /**
    * clear cache to update the shape
    */
   public void clearCache();
}
