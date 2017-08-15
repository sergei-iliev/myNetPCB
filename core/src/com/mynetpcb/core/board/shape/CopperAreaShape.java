package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.board.ClearanceSource;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.Net;

public abstract class CopperAreaShape extends Shape implements Trackable<LinePoint>,ClearanceSource,Resizeable,Externalizable,Net{
    
    public CopperAreaShape(int layermaskId) {
        super(0,0,0,0,0,layermaskId);
    }

}
