package com.mynetpcb.core.capi.layer;

import java.util.List;


public interface OrderedList<S> extends Cloneable,List<S>{
   
    public OrderedList<S> clone();
        
    /**
     * Reorder list as a result of shape' s layer change
     */
    public void reorder();
}
