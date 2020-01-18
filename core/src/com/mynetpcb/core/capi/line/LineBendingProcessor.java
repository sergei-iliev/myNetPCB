package com.mynetpcb.core.capi.line;

import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;

import java.lang.ref.WeakReference;

/**
 *State strategy with method factory
 * @author Sergey Iliev
 */
public abstract class LineBendingProcessor {   
    
    //***class does not own line   
    private WeakReference<Trackable> weakLineRef;
    
    protected boolean isGridAlignable;
    
    public void setGridAlignable(boolean isGridAlignable){
       this.isGridAlignable=isGridAlignable; 
    }
    
    public void initialize(Trackable line){
        
      if(this.weakLineRef!=null){
          if(line==this.weakLineRef.get()){
             return;
          }
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
    
    public abstract void moveLinePoint(double x,double y);
    
    /**
     *Used for popup UI check box selected/uselected
     * @return
     */
    //public abstract String getActionCommand();
    
    public void release(){
        //***end the line by puting floating into one point   
        getLine().reset(); 
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
          if(Utils.EQ(pointToAdd.x,lastPoint.x)&&Utils.EQ(pointToAdd.y,lastPoint.y))
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
              lastPoint.set(pointToAdd);  
              return true;
            }
            if((Utils.EQ(lastPoint.x,pointToAdd.x)&&Utils.EQ(lastlastPoint.x,pointToAdd.x))||(Utils.EQ(lastPoint.y,pointToAdd.y)&&Utils.EQ(lastlastPoint.y,pointToAdd.y))){                  
              lastPoint.set(pointToAdd);                           
              return true;
            }                    
         }
         return false;
         
    }
    
    public boolean isSlopeInterval(Point p1,Point p2){
        return (!Utils.EQ(p1.x,p2.x)&&!Utils.EQ(p1.y,p2.y));
    }
    
}

