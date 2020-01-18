package com.mynetpcb.core.capi.line;

import com.mynetpcb.d2.shapes.Point;


public class SlopeLineBendingProcessor extends LineSlopeBendingProcessor{
  
  @Override
    public void moveLinePoint(double x,double y) {
            if(getLine().getLinePoints().size()>1){
                Point lastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-1);  
                Point lastlastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-2);   
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
