package com.mynetpcb.core.capi.line;

import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;

public class HorizontalToVerticalBendingProcessor extends LineBendingProcessor{


    @Override
    public boolean addLinePoint(Point point) {
        if(this.getLine().getLinePoints().size()==0){
            this.getLine().reset(point);
        }
        boolean result=false;
        if(!this.isOverlappedPoint(point)){
           if(!this.isPointOnLine(point)) {
               Point midP,endP;
              
               if(this.isGridAlignable){
                 midP=this.getLine().getOwningUnit().getGrid().positionOnGrid(this.getLine().getFloatingMidPoint().x,this.getLine().getFloatingMidPoint().y);
                 endP=this.getLine().getOwningUnit().getGrid().positionOnGrid(this.getLine().getFloatingEndPoint().x,this.getLine().getFloatingEndPoint().y);
               }else{
                 midP=new Point(this.getLine().getFloatingMidPoint().x,this.getLine().getFloatingMidPoint().y);
                 endP=new Point(this.getLine().getFloatingMidPoint().x,this.getLine().getFloatingMidPoint().y);
                 
               }
               if(this.isOverlappedPoint(midP)){
                  this.getLine().add(endP);
                  result=true;  
               }else if(!this.isPointOnLine(midP)){
                  this.getLine().add(midP);
                  result=true;
               } 
           }  
        }
        
        this.getLine().shiftFloatingPoints();
        return result;
    }

    @Override
    public void moveLinePoint(double x, double y) {
        if(this.getLine().getLinePoints().size()>1){
            //line is resumed if line end is not slope then go on from previous segment
            Point lastPoint=(Point)this.getLine().getLinePoints().get(this.getLine().getLinePoints().size()-1);  
            Point lastlastPoint=(Point)this.getLine().getLinePoints().get(this.getLine().getLinePoints().size()-2); 
            if(this.isHorizontalInterval(lastPoint, lastlastPoint)){
               this.handleVertical(x, y);
            }else{
               this.handleHorizontal(x, y); 
            }
            
        }else{
            this.handleHorizontal(x, y);
        }   

    }
    
    protected void handleVertical(double x,double  y){
            this.getLine().getFloatingEndPoint().set(x,y);
            this.getLine().getFloatingMidPoint().set(this.getLine().getFloatingStartPoint().x,this.getLine().getFloatingEndPoint().y); 
    }
    protected void handleHorizontal(double x,double  y){        
        this.getLine().getFloatingEndPoint().set(x,y);
        this.getLine().getFloatingMidPoint().set(this.getLine().getFloatingEndPoint().x,this.getLine().getFloatingStartPoint().y); 
                          
    }     
    protected boolean isHorizontalInterval(Point p1,Point p2){
                  if(Utils.EQ(p1.x,p2.x)){
                          return false;
                  }               
                  return true;    
          }     
}
