package com.mynetpcb.core.capi.line;


import com.mynetpcb.core.capi.Drawable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.capi.unit.Unitable;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 *Define bendable line capabilities
 * @author Sergey Iliev
 */
public interface Trackable<P extends Point> extends Drawable,Unitable<Unit>{
    public enum ResumeState{
        ADD_AT_FRONT,ADD_AT_END
    }
    
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
      * When deleting point by point there is no need to keep the shape when less the 2 points for a line or 
      * 3 points for region 
      * 
      * @return
      */
     public default boolean isShapeDeletable() {
    	 return getLinePoints().size()==2;  //line must hold more then 2 points
     }
    
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
     * @param viewportWindow
     * @return
     * Use isControlPointClicked instead
     */
     //public Point isBendingPointClicked(double x,double y,ViewportWindow viewportWindow);
    public default Point getBendingPointClicked(double x,double y,int distance){
        Box rect = Box.fromRect(x
                        - distance / 2, y - distance
                        / 2, distance, distance);

        
        Optional<P> opt= this.getLinePoints().stream().filter(( wirePoint)->rect.contains(wirePoint)).findFirst();                  
                  
        
        return opt.orElse(null);
    }
    
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
    
    public default ResumeState getResumeState(){return null;}
    /*
     * revers wire points ordering
     * if point is null -> reverse unconditionally
     * otherwise make this a last point
     */
    //@Deprecated
    //public void reverse(double x,double y);
    
    /*
     * Resume drawing - could be at the front or end of the existing line
     */
    public default void resumeLine(double x,double y){};
    /*
     * remove point
     */
    public default void removePoint(double x,double y,int distance) {
      Point point=getBendingPointClicked(x, y,distance);
      if(point!=null){
        this.getLinePoints().remove(point);
        point = null;
      }     	
    }
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


