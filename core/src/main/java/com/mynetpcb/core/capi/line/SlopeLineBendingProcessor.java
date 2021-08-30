package com.mynetpcb.core.capi.line;

import com.mynetpcb.d2.shapes.Point;


public class SlopeLineBendingProcessor extends LineSlopeBendingProcessor{
  
  @Override
    public void moveLinePoint(double x,double y) {
            if(getLine().getLinePoints().size()>1){
                Point lastPoint,lastlastPoint;
                if(getLine().getResumeState()==Trackable.ResumeState.ADD_AT_FRONT){
                    lastPoint=(Point)getLine().getLinePoints().get(0);  
                    lastlastPoint=(Point)getLine().getLinePoints().get(1);  
                }else{
                   lastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-1);  
                   lastlastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-2);  
                }
                
                if(this.isSlopeInterval(lastPoint, lastlastPoint)){
                   this.handleLine(x, y);
                }else{
                   this.handleSlope(x, y); 
                }
                
            }else{
                this.handleSlope(x, y);
            }   
    
    } 
}
