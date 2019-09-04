package com.mynetpcb.core.capi.line;

import com.mynetpcb.d2.shapes.Point;

public class LinePoint extends Point {
    private boolean selection;

     public LinePoint(double x,double y){
         super(x,y);
     }
     
     public LinePoint(Point point){
         super(point.x,point.y);
     }
     
     public void setSelected(boolean selection) {
        this.selection=selection;
     }

     public boolean isSelected() {
        return selection;
     }
}
