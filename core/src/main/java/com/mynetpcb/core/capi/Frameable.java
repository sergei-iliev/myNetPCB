package com.mynetpcb.core.capi;


/**
 *Represent the frame in each unit(symbol,footprint,circuit,board)
 * @author Sergey Iliev
 */
public interface Frameable extends Drawable{
  /**
     *Resize the frame
     * @param width
     * @param height
     */
  public void setSize(int width,int height);
  
  /**
   * Set offset from outer border
   * @param offset
   */
  public void setOffset(int offset);
  
  public int getOffset();
}
