package com.mynetpcb.core.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Pinable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.pad.Net;

public abstract class PadShape extends Shape implements Pinable, Net, Textable, Externalizable{
    
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
    
    public PadShape(int x, int y, int width, int height) {
        super(x, y, width, height, -1, Layer.LAYER_BACK);
    }
    
    public abstract PadShape.Shape getShape();
}
