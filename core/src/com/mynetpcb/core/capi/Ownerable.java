package com.mynetpcb.core.capi;

import com.mynetpcb.core.capi.shape.Shape;

/*
 * Parent lives longer then child.
 */
@Deprecated
public interface Ownerable<S extends Shape> {

    public S getOwner();
    
    public void setOwner(S parent); 
}
