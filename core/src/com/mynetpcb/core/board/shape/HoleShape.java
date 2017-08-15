package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.Layer;

public abstract class HoleShape extends Shape implements ClearanceTarget,Externalizable{
    public HoleShape() {
        super(0,0,0,0,0,Layer.LAYER_ALL);
    }

}
