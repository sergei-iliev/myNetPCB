package com.mynetpcb.core.capi.line;

import com.mynetpcb.d2.shapes.Point;

public class DefaultLineBendingProcessor extends LineBendingProcessor {
    @Override    
    public boolean addLinePoint(Point point) {      
       boolean result=false;
       if(!isOverlappedPoint(point)){
           if(!isPointOnLine(point)){
               getLine().addPoint(point);   
               result=true;
           }               
       }         
       getLine().reset(point); 
       return result;
    }

    public void moveLinePoint(int x, int y) {
      getLine().getFloatingEndPoint().set(x,y); 
      getLine().getFloatingMidPoint().set(x,y);
    }

}
