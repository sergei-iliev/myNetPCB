package com.mynetpcb.core.capi.pin;

import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;

import java.util.Collection;

public interface CompositePinable {
    
    public Collection<Point> getPinPoints();
    
    public default Box getPinsRect(){
        return null;
    }
}
