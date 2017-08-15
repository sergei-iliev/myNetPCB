package com.mynetpcb.gerber.capi;

import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;

public interface Processor {
    
    public void process(Unit<? extends Shape> board,int layermask);
}
