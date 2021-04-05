package com.mynetpcb.core.capi.line;

import com.mynetpcb.d2.shapes.Point;

public class VerticalToHorizontalBendingProcessor extends HorizontalToVerticalBendingProcessor{


   public void moveLinePoint(double x,double y){
        if(this.getLine().getLinePoints().size()>1){
            //line is resumed if line end is not slope then go on from previous segment
            //Point lastPoint=(Point)this.getLine().getLinePoints().get(this.getLine().getLinePoints().size()-1);  
            //Point lastlastPoint=(Point)this.getLine().getLinePoints().get(this.getLine().getLinePoints().size()-2); 
            Point lastPoint,lastlastPoint;
            if(getLine().getResumeState()==Trackable.ResumeState.ADD_AT_FRONT){
                   lastPoint=(Point)getLine().getLinePoints().get(0);  
                   lastlastPoint=(Point)getLine().getLinePoints().get(1);  
            }else{
                   lastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-1);  
                   lastlastPoint=(Point)getLine().getLinePoints().get(getLine().getLinePoints().size()-2);  
            }            
            if(this.isHorizontalInterval(lastPoint, lastlastPoint)){
               this.handleVertical(x, y);
            }else{
               this.handleHorizontal(x, y); 
            }
            
        }else{
            this.handleVertical(x, y);
        }           
            }
}
