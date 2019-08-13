package com.mynetpcb.core.capi;

import java.awt.geom.Point2D;


/*
 * Arc shape needs to be changed at mouse pointer dragging
 */
public interface Reshapeable {
    public static final int ARC_START_POINT=0x1;
    
    public static final int ARC_END_POINT=0x2;
/**
     *
     * @param x mouse coord
     * @param y mouse coord
     * @param targetid shape control point identifier
     */
  public void Reshape(int x,int y,int targetid); 
/**
     *
     * @param x mouse coord
     * @param y mouse coord
     * @return which shape control point identifier
     */
  public int getReshapeRectID(int x,int y);
/**
     *
     * @param x  mouse coord
     * @param y  mouse coord
     * @return shape control point
     */
  public Point2D isReshapeRectClicked(int x,int y);
}
