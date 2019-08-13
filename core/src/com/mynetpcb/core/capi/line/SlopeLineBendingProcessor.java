package com.mynetpcb.core.capi.line;


import java.awt.Point;


/**
 * Start bending with slope first -> board wireing
 */
public class SlopeLineBendingProcessor extends LineSlopeBendingProcessor{

        
    @Override
    public void moveLinePoint(int x, int y) {
        
        
        Trackable line=getLine();
//        if(this.isNew){
//            this.isNew=false;
//            if(line.getLinePoints().size()>1){
//                Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);  
//                Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
//                
//                if(isSlopeInterval(lastPoint, lastlastPoint)){
//                  
//                }
//            }
//        }
   
        if(line.getLinePoints().size()>1){
            Point lastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-1);  
            Point lastlastPoint=(Point)line.getLinePoints().get(line.getLinePoints().size()-2); 
            if(isSlopeInterval(lastPoint, lastlastPoint)){
               this.handleLine(x, y);
            }else{
               this.handleSlope(x, y); 
            }
            
        }else{
            this.handleSlope(x, y);
        }

        
    } 
    
}
