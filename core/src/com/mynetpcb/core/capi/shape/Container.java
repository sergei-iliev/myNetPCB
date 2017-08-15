package com.mynetpcb.core.capi.shape;

import com.mynetpcb.core.capi.Drawable;

import java.util.List;

public interface Container extends Drawable{
    
    public List<Shape> getShapes();
    
}
