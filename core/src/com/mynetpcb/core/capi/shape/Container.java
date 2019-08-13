package com.mynetpcb.core.capi.shape;

import com.mynetpcb.core.capi.Drawable;

import java.util.List;

public interface Container{
    
    public default <M extends Drawable> List<M> getShapes(Class<?> clazz,int layermask) {
       return null;   
    }
    
    
    public default <M extends Drawable> List<M> getShapes(Class<?> clazz){
        return null;
    }
    
    public List<? extends Shape> getShapes();
}
