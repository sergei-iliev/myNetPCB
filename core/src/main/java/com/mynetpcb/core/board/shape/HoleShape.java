package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.d2.shapes.Circle;

/*
 * reduce dependency to board jar
 */
public abstract class HoleShape extends Shape implements ClearanceTarget,Externalizable{
    public HoleShape() {
        super(0,Layer.LAYER_ALL);
    }

    public abstract Circle getInner();
}
