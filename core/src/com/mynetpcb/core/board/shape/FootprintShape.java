package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.pin.CompositePinable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.CompositeTextable;


public abstract class FootprintShape extends Shape implements ClearanceTarget,CompositeTextable,CompositePinable,Externalizable{
    public FootprintShape(int layermask) {
        super(0,layermask);
    }


    
}

