package com.mynetpcb.core.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Pinable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.CompositeTextable;
import com.mynetpcb.core.pad.Net;

public abstract class PadShape extends Shape implements Pinable, Net, CompositeTextable, Externalizable{
    
    public enum Shape {
        RECTANGULAR,
        CIRCULAR,
        OVAL,
        POLYGON
    }

    public enum Type {
        THROUGH_HOLE,
        SMD,
        CONNECTOR
    }
    
    public enum PadConnection{
       DIRECT,
       THERMAL
    }
    
    public PadShape() {
        super(-1, Layer.LAYER_BACK);
    }
    
    public abstract PadShape.Shape getShape();
    
    public abstract PadShape.Type getType();
}
