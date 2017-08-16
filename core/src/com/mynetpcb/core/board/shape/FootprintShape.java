package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.shape.Container;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.pad.shape.PadShape;

/*
 * reduce dependency to board jar
 */
public abstract class FootprintShape<P extends PadShape> extends Shape implements Container,ClearanceTarget,Textable,Pinaware<P>,Externalizable{
    public FootprintShape(int layermask) {
        super(0,0,0,0,0,layermask);
    }

    
}
