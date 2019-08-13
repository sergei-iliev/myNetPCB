package com.mynetpcb.core.capi.line;


import java.awt.Point;

public class TopLineBendingProcessor extends LineBendingProcessor{
    
    
    public String getActionCommand(){
      return "topbend";    
    }
    
    @Override
    public boolean addLinePoint(Point point) { 
       if(getLine().getLinePoints().size()==0){
            getLine().Reset(point);
       }
       boolean result=false;
       if(!isOverlappedPoint(point)){
           if(!isPointOnLine(point)) {
               Point midP,endP;
              
               if(this.isGridAlignable){
                 midP=getLine().getOwningUnit().getGrid().positionOnGrid(getLine().getFloatingMidPoint().x,getLine().getFloatingMidPoint().y);
                 endP=getLine().getOwningUnit().getGrid().positionOnGrid(getLine().getFloatingEndPoint().x,getLine().getFloatingEndPoint().y);
               }else{
                 midP=new Point(getLine().getFloatingMidPoint().x,getLine().getFloatingMidPoint().y);
                 endP=new Point(getLine().getFloatingEndPoint().x,getLine().getFloatingEndPoint().y);
                 
               }
               if(isOverlappedPoint( midP)){
                 getLine().addPoint(endP);
                 result=true;  
               }else if(!isPointOnLine(midP)){
                 getLine().addPoint(midP);
                 result=true;
               } 
           }  
       }  
       
       getLine().shiftFloatingPoints(); 
       return result;
    }

    @Override
    public void moveLinePoint(int x, int y) {
        Trackable line=getLine();
        line.getFloatingEndPoint().setLocation(x,y);
  
                    if(line.getLinePoints().size()>1){
                        Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);  
                        Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
                        if(lastPoint.x==lastlastPoint.x)
                          line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y); 
                        else
                          line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x,line.getFloatingEndPoint().y);
                    }else{
                        line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x,line.getFloatingEndPoint().y);
                    }                
    }
}
