package com.mynetpcb.core.capi.line;

import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.core.utils.Utilities.QUADRANT;
import com.mynetpcb.d2.shapes.Point;

public class LineSlopeBendingProcessor extends LineBendingProcessor{


    @Override
    public boolean addLinePoint(Point point) {
        if(getLine().getLinePoints().size()==0){
             getLine().reset(point);
        }               
        boolean result=false;
        if(!this.isOverlappedPoint(point)){
            if(!this.isPointOnLine(point)) {
                Point midP,endP;
               
                if(this.isGridAlignable){
                  midP=getLine().getOwningUnit().getGrid().positionOnGrid(getLine().getFloatingMidPoint().x,getLine().getFloatingMidPoint().y);
                  endP=getLine().getOwningUnit().getGrid().positionOnGrid(getLine().getFloatingEndPoint().x,getLine().getFloatingEndPoint().y);
                }else{
                  midP=new Point(getLine().getFloatingMidPoint().x,getLine().getFloatingMidPoint().y);
                  endP=new Point(getLine().getFloatingEndPoint().x,getLine().getFloatingEndPoint().y);
                  
                }
                if(this.isOverlappedPoint(midP)){
                   getLine().add(endP);
                   result=true;  
                }else if(!this.isPointOnLine(midP)){
                   getLine().add(midP);
                   result=true;
                } 
            }  
        }  
        
        getLine().shiftFloatingPoints(); 
        return result;
    }

    @Override
    public void moveLinePoint(double x, double y) {
        if(getLine().getLinePoints().size()>1){
            Point lastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-1);  
            Point lastlastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-2);  
            if(this.isSlopeInterval(lastPoint, lastlastPoint)){
               this.handleLine(x, y);
            }else{
               this.handleSlope(x, y); 
            }
            
        }else{
            this.handleLine(x, y);
        }

    }
    
    protected void handleSlope(double x,double y){    
        getLine().getFloatingEndPoint().set(x,y);
        QUADRANT quadrant = Utilities.getQuadrantLocation(getLine().getFloatingStartPoint(),getLine().getFloatingEndPoint());
        double dx=Math.abs(getLine().getFloatingStartPoint().x-getLine().getFloatingEndPoint().x);
        double dy=Math.abs(getLine().getFloatingStartPoint().y-getLine().getFloatingEndPoint().y); 
        
        
        if(dx>=dy){ 
            switch(quadrant){
                case  FIRST:
                      getLine().getFloatingMidPoint().set(getLine().getFloatingStartPoint().x+dy,getLine().getFloatingEndPoint().y); 
                      break;            
                case  SECOND:
                      getLine().getFloatingMidPoint().set(getLine().getFloatingStartPoint().x-dy,getLine().getFloatingEndPoint().y);  
                      break;             
                case  THIRD:
                      getLine().getFloatingMidPoint().set(getLine().getFloatingStartPoint().x-dy,getLine().getFloatingEndPoint().y);   
                      break; 
                case  FORTH:
                      getLine().getFloatingMidPoint().set(getLine().getFloatingStartPoint().x+dy,getLine().getFloatingEndPoint().y);                        
                      break;                
            }
        }else{
            switch(quadrant){
                case  FIRST:
                      getLine().getFloatingMidPoint().set(getLine().getFloatingEndPoint().x,getLine().getFloatingStartPoint().y-dx);                        
                      break;            
                case  SECOND:
                      getLine().getFloatingMidPoint().set(getLine().getFloatingEndPoint().x,getLine().getFloatingStartPoint().y-dx); 
                      break;             
                case  THIRD:
                      getLine().getFloatingMidPoint().set(getLine().getFloatingEndPoint().x,getLine().getFloatingStartPoint().y+dx); 
                      break; 
                case  FORTH:
                      getLine().getFloatingMidPoint().set(getLine().getFloatingEndPoint().x,getLine().getFloatingStartPoint().y+dx);                        
                      break;                
            }            
        }
           
    } 
protected void  handleLine(double x,double  y){        
            getLine().getFloatingEndPoint().set(x,y);
            QUADRANT quadrant = Utilities.getQuadrantLocation(getLine().getFloatingStartPoint(),getLine().getFloatingEndPoint());
            double dx=Math.abs(getLine().getFloatingStartPoint().x-getLine().getFloatingEndPoint().x);
            double dy=Math.abs(getLine().getFloatingStartPoint().y-getLine().getFloatingEndPoint().y); 
            
            if(dx>=dy){ 
                switch(quadrant){
                    case  FIRST:
                          getLine().getFloatingMidPoint().set(getLine().getFloatingEndPoint().x-dy,getLine().getFloatingStartPoint().y); 
                          break;            
                    case  SECOND:
                          getLine().getFloatingMidPoint().set(getLine().getFloatingEndPoint().x+dy,getLine().getFloatingStartPoint().y);  
                          break;             
                    case  THIRD:
                          getLine().getFloatingMidPoint().set(getLine().getFloatingEndPoint().x+dy,getLine().getFloatingStartPoint().y);   
                          break; 
                    case  FORTH:
                          getLine().getFloatingMidPoint().set(getLine().getFloatingEndPoint().x-dy,getLine().getFloatingStartPoint().y);                        
                          break;                
                }
            }else{
                    switch(quadrant){
                    case  FIRST:
                          getLine().getFloatingMidPoint().set(getLine().getFloatingStartPoint().x,getLine().getFloatingEndPoint().y+dx);                        
                          break;            
                    case  SECOND:
                          getLine().getFloatingMidPoint().set(getLine().getFloatingStartPoint().x,getLine().getFloatingEndPoint().y+dx); 
                          break;             
                    case  THIRD:
                          getLine().getFloatingMidPoint().set(getLine().getFloatingStartPoint().x,getLine().getFloatingEndPoint().y-dx); 
                          break; 
                    case  FORTH:
                          getLine().getFloatingMidPoint().set(getLine().getFloatingStartPoint().x,getLine().getFloatingEndPoint().y-dx);                        
                          break;                
                }            
            }
            
        }       
}
