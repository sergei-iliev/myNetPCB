package com.mynetpcb.core.capi.line;


import com.mynetpcb.core.capi.shape.Shape;

import java.awt.Point;

import java.lang.ref.WeakReference;


/**
 *State strategy with method factory
 * @author Sergey Iliev
 */
public abstract class LineBendingProcessor {   
    public enum StartLineType{
        START_SLOPE,
        START_LINE;
    }
    
    //***class does not own line   
    private WeakReference<Trackable> weakLineRef;
    
    protected boolean isGridAlignable;
    
    public void setGridAlignable(boolean isGridAlignable){
       this.isGridAlignable=isGridAlignable; 
    }
    
    public void Initialize(Trackable line){
      if(this.weakLineRef!=null){
            this.weakLineRef.clear();  
      }
      this.weakLineRef=new WeakReference<Trackable>(line);  
    }
    
    /*
     * Default handling for all Processors on Add
     * First point must be aligned with floating points
     * @return - if point is indeed added
     */    
    public abstract boolean addLinePoint(Point point);   
    
    public abstract void moveLinePoint(int x,int y);
    
    /**
     *Used for popup UI check box selected/uselected
     * @return
     */
    public abstract String getActionCommand();
    
    public void Release(){
        //***end the line by puting floating into one point   
        getLine().Reset(); 
        if(getLine().getLinePoints().size()<2&&getLine().getOwningUnit()!=null){
            getLine().getOwningUnit().delete(((Shape)getLine()).getUUID());
        }
        weakLineRef.clear();
    }
    
    public Trackable getLine(){        
      return weakLineRef.get();  
    }
   
    /*
     * Wiring rule-> discard point if overlaps with last line point
     */
    public boolean isOverlappedPoint(Trackable line,Point pointToAdd){
        if(line.getLinePoints().size()>0){
          Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1); 
            //***is this the same point as last one?   
          if(pointToAdd.equals(lastPoint))
            return true;    
        }
        return false;
    }
    /*
     * Wiring rule -> if the point is on a line with previous,shift the previous the the new one,
     *                 without adding the point
     */
    public boolean isPointOnLine(Trackable line,Point pointToAdd){
         if(line.getLinePoints().size()>=2){
              Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);  
              Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
            //***check if point to add overlaps last last point
            if(lastlastPoint.equals(pointToAdd)){
              line.deleteLastPoint();
              lastPoint.setLocation(pointToAdd);  
              return true;
            }
            if((lastPoint.getX()==pointToAdd.getX()&&lastlastPoint.getX()==pointToAdd.getX())||(lastPoint.getY()==pointToAdd.getY()&&lastlastPoint.getY()==pointToAdd.getY())){                  
              lastPoint.setLocation(pointToAdd);                           
              return true;
            }                    
         }
         return false;
    }
    
    public boolean isSlopeInterval(Point p1,Point p2){
        return (p1.x!=p2.x&&p1.y!=p2.y);
    }
}

