package com.mynetpcb.core.capi.line;


import com.mynetpcb.core.capi.shape.Shape;

import java.awt.Point;

import java.lang.ref.WeakReference;


/**
 *State strategy with method factory
 * @author Sergey Iliev
 */
public abstract class LineBendingProcessor {   
    
    //***class does not own line   
    private WeakReference<Trackable> weakLineRef;
    
    protected boolean isGridAlignable;
    
    protected boolean isNew;
    public void setGridAlignable(boolean isGridAlignable){
       this.isGridAlignable=isGridAlignable; 
    }
    
    public void Initialize(Trackable line){
        
      if(this.weakLineRef!=null){
          if(line==this.weakLineRef.get()){
             return;
          }
          this.weakLineRef.clear();  
      }
      this.weakLineRef=new WeakReference<Trackable>(line);  
      this.isNew=true; 
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
    //public abstract String getActionCommand();
    
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
    public boolean isOverlappedPoint(Point pointToAdd){
        if(getLine().getLinePoints().size()>0){
          Point lastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-1); 
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
    public boolean isPointOnLine(Point pointToAdd){
         if(getLine().getLinePoints().size()>=2){
              Point lastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-1);  
              Point lastlastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-2); 
            //***check if point to add overlaps last last point
            if(lastlastPoint.equals(pointToAdd)){
              getLine().deleteLastPoint();
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

