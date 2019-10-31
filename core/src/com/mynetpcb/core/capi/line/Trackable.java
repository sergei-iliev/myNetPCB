package com.mynetpcb.core.capi.line;


import com.mynetpcb.core.capi.Drawable;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.capi.unit.Unitable;


import com.mynetpcb.d2.shapes.Point;

import java.util.List;


/**
 *Define bendable line capabilities
 * @author Sergey Iliev
 */
public interface Trackable<P extends Point> extends Drawable,Unitable<Unit>{
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
     public List<P> getLinePoints();  
    
/**
     * Add new point to the track
     * @param point
     */
     public void add(Point point); 

     public void add(double x,double y); 

/**
     *Insert point
     * @param x
     * @param y
     */
    public void insertPoint(double x,double y);
/**
     *Check if given coordinate overlaps with a bending track point
     * @param x
     * @param y
     * @return
     * Use isControlPointClicked instead
     */
     public Point isBendingPointClicked(double x,double y);
     
    /**
     *Equalize the initial state of the drawing point of the subline
     * @param point to equalize to
     */
    public void reset(Point point);
    
    
    /**
     *Equalize the initial state of the drawing point of the subline
     * @param point to equalize to
     */
    public void reset(double x,double y);
    
    /**
     * Equalize the initial state of the drawing point of the subline by the end line point
     */
    public void reset();
    
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
    public void reverse(double x,double y);
    /*
     * remove point
     */
    public void removePoint(double x,double y);
     /*
      * is this point overlaps with end wire point ->first or last 
      */   
    public boolean isEndPoint(double x,double y);
    /*
     * get end point in regard to first or last point of the wire
     */
    public default Point getEndPoint(double x,double y){
        return null;
    }
/**
     *Verify that the line is being drawn
     * @return 
     */
    public boolean isFloating();
}


