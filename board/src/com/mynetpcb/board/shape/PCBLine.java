package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.pad.shape.Line;

public class PCBLine extends Line implements PCBShape{
    public PCBLine(int thickness,int layermaskId) {
        super(thickness,layermaskId);
    }
}
