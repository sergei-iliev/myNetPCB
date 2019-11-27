package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.Net;

/*
 * reduce dependency to board jar
 */
public abstract class ViaShape extends Shape implements ClearanceTarget,Externalizable,Net{
    public enum Type{
        BURIED, 
        BLIND,
        THROUGH_HOLE
    }    
    public ViaShape() {
        super((int)Grid.MM_TO_COORD(0.3),Layer.LAYER_ALL);
    }

}
