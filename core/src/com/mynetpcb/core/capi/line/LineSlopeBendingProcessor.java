package com.mynetpcb.core.capi.line;


import com.mynetpcb.core.utils.Utilities;

import java.awt.Point;

public class LineSlopeBendingProcessor extends LineBendingProcessor{
    
    

    
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
    
        if(line.getLinePoints().size()>1){
            Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);  
            Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
            if(isSlopeInterval(lastPoint, lastlastPoint)){
               this.handleLine(x, y);
            }else{
               this.handleSlope(x, y); 
            }
            
        }else{
            this.handleLine(x, y);
        }

        
    } 

    protected void handleSlope(int x, int y){
        Trackable line=getLine();
        line.getFloatingEndPoint().setLocation(x,y);
        Utilities.QUADRANT quadrant = Utilities.getQuadrantLocation(line.getFloatingStartPoint(),line.getFloatingEndPoint());
        
        int dx=Math.abs(line.getFloatingStartPoint().x-line.getFloatingEndPoint().x);
        int dy=Math.abs(line.getFloatingStartPoint().y-line.getFloatingEndPoint().y); 
        
        if(dx>=dy){ 
            switch(quadrant){
            case  FIRST:
                  line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x+dy,line.getFloatingEndPoint().y); 
                  break;            
            case  SECOND:
                  line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x-dy,line.getFloatingEndPoint().y); 
                  break;             
            case  THIRD:
                  line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x-dy,line.getFloatingEndPoint().y); 
                  break; 
            case  FORTH:
                  line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x+dy,line.getFloatingEndPoint().y);                          
                  break;
              
            }            
        }else{
            switch(quadrant){
            case  FIRST:
                  line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y-dx); 
                  break;            
            case  SECOND:
                  line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y-dx); 
                  break;             
            case  THIRD:
                  line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y+dx); 
                  break; 
            case  FORTH:
                   line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x,line.getFloatingStartPoint().y+dx);                          
                   break;
              
            }
           
        }        
    }
    
    protected void handleLine(int x, int y){
        Trackable line=getLine();
        line.getFloatingEndPoint().setLocation(x,y);
        Utilities.QUADRANT quadrant = Utilities.getQuadrantLocation(line.getFloatingStartPoint(),line.getFloatingEndPoint());
        int dx=Math.abs(line.getFloatingStartPoint().x-line.getFloatingEndPoint().x);
        int dy=Math.abs(line.getFloatingStartPoint().y-line.getFloatingEndPoint().y); 
        
        if(dx>=dy){ 
            switch(quadrant){
                case  FIRST:
                      line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x-dy,line.getFloatingStartPoint().y); 
                      break;            
                case  SECOND:
                      line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x+dy,line.getFloatingStartPoint().y);  
                      break;             
                case  THIRD:
                      line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x+dy,line.getFloatingStartPoint().y);   
                      break; 
                case  FORTH:
                      line.getFloatingMidPoint().setLocation(line.getFloatingEndPoint().x-dy,line.getFloatingStartPoint().y);                        
                      break;                
            }
        }else{
            switch(quadrant){
                case  FIRST:
                      line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x,line.getFloatingEndPoint().y+dx);                        
                      break;            
                case  SECOND:
                      line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x,line.getFloatingEndPoint().y+dx); 
                      break;             
                case  THIRD:
                      line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x,line.getFloatingEndPoint().y-dx); 
                      break; 
                case  FORTH:
                      line.getFloatingMidPoint().setLocation(line.getFloatingStartPoint().x,line.getFloatingEndPoint().y-dx);                        
                      break;                
            }            
        }
        
    }
    
}
