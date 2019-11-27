package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.Net;
import com.mynetpcb.d2.shapes.Point;

/*
 * reduce dependency to board jar
 */
public abstract class CopperAreaShape extends Shape implements Trackable<Point>,ClearanceSource,Resizeable,Externalizable,Net{
    
    public CopperAreaShape(int layermaskId) {
        super(0,layermaskId);
    }

}
