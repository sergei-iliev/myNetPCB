package com.mynetpcb.core.capi.line;


import com.mynetpcb.core.capi.Drawable;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.capi.unit.Unitable;

import java.awt.Point;

import java.util.List;


/**
 *Define bendable line capabilities
 * @author Sergey Iliev
 */
public interface Trackable<LinePoint> extends Drawable,Unitable<Unit>{
    public enum JoinType{
        JOIN_MITER,
        JOIN_ROUND,
        JOIN_BEVEL
    }
    
    public enum EndType{
        CAP_BUTT,
        CAP_ROUND,
        CAP_SQUARE
    }
    
     /**
     * The basic building points of a track
     * @return the points the track consists of
     */
     public List<LinePoint> getLinePoints();  
    
/**
     * Add new point to the track
     * @param point
     */
     public void addPoint(Point point); 

     public void add(int x,int y); 

/**
     *Insert point
     * @param x
     * @param y
     */
    public void insertPoint(int x,int y);
/**
     *Check if given coordinate overlaps with a bending track point
     * @param x
     * @param y
     * @return
     * Use isControlPointClicked instead
     */
     public Point isBendingPointClicked(int x,int y);
     
    /**
     *Equalize the initial state of the drawing point of the subline
     * @param point to equalize to
     */
    public void Reset(Point point);
    
    
    /**
     *Equalize the initial state of the drawing point of the subline
     * @param point to equalize to
     */
    public void Reset(int x,int y);
    
    /**
     * Equalize the initial state of the drawing point of the subline by the end line point
     */
    public void Reset();
    
    public Point getFloatingStartPoint();
    
    public Point getFloatingMidPoint();
    
    public Point getFloatingEndPoint();
    
    
    public void shiftFloatingPoints();
    
    public void deleteLastPoint();
    
    /*
     * revers wire points ordering
     * if point is null -> reverse unconditionally
     * otherwise make this a last point
     */
    public void Reverse(int x,int y);
    /*
     * remove point
     */
    public void removePoint(int x,int y);
     /*
      * is this point overlaps with end wire point ->first or last 
      */   
    public boolean isEndPoint(int x,int y);
/**
     *Verify that the line is being drawn
     * @return 
     */
    public boolean isFloating();
}


