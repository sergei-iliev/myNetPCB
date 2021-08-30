package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Sublineable;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.AbstractLine;
import com.mynetpcb.core.board.Net;

/*
 * reduce dependency to board jar
 */
public abstract class TrackShape extends AbstractLine implements Trackable<LinePoint>,Resizeable,ClearanceTarget,Sublineable,Externalizable,Net{
    
    public TrackShape(int thickness,int layermaskId){
        super(thickness,layermaskId);
    }


}
