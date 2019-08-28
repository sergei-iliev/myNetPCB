package com.mynetpcb.core.capi.line;

import java.awt.Point;

public class LinePoint extends Point{
    
    private boolean selection;

    public LinePoint(int x,int y){
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
