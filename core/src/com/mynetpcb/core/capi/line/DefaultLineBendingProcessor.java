package com.mynetpcb.core.capi.line;


import java.awt.Point;

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
       getLine().Reset(point); 
       return result;
    }

    public void moveLinePoint(int x, int y) {
      getLine().getFloatingEndPoint().setLocation(x,y); 
      getLine().getFloatingMidPoint().setLocation(x,y);
    }

}

