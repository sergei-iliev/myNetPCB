package com.mynetpcb.core.capi.gerber;

import com.mynetpcb.d2.shapes.Arc;
import com.mynetpcb.d2.shapes.Point;

public class ArcGerberableAdaptor implements ArcGerberable{
    private final Arc arc;
    
    public ArcGerberableAdaptor(Arc arc) {
        this.arc=arc;
    }

    @Override
    public Point getStartPoint() {        
        return arc.getStart();
    }

    @Override
    public Point getEndPoint() {
        return this.arc.getEnd();  
    }

    @Override
    public Point getCenter() {
        return arc.pc;
    }

    @Override
    public double getI() {
        double i=0;
        //loss of pressiosion!!!!!!!!!!!!!!!
        //Utilities.QUADRANT quadrant= Utilities.getQuadrantLocation(arc.pc,getStartPoint());
        
        //    switch(quadrant){
        //     case SECOND:case THIRD:
                i=arc.pc.x-getStartPoint().x;
       //         break;
       //      case FIRST:case FORTH:
                //convert to -
       //         i=arc.pc.x-getStartPoint().x;
       //      break;
       //     }        
        return i;
    }

    @Override
    public double getJ() {
        double j=0;
        //Utilities.QUADRANT quadrant= Utilities.getQuadrantLocation(arc.pc,getStartPoint());
        
        //    switch(quadrant){
        //     case FIRST:case SECOND:
                j=arc.pc.y-getStartPoint().y;
        //        break;
        //     case THIRD:case FORTH:
                //convert to -
        //        j=arc.pc.y-getStartPoint().y;
        //     break;
        //    }        
        return j;
    }

    @Override
    public boolean isSingleQuadrant() {
        return Math.abs(arc.endAngle)<=90;
    }

    @Override
    public boolean isClockwise() {
        return arc.endAngle <0;
    }
}
