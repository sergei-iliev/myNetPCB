package com.mynetpcb.core.capi.layer;

import com.mynetpcb.core.capi.shape.Shape;

import java.util.LinkedList;

public class DefaultOrderedList <S extends Shape> extends LinkedList<S> implements OrderedList<S>{

    @Override
    public OrderedList<S> clone(){     
        return new DefaultOrderedList<>();
    }
    
    @Override
    public void reorder() {
        
    }
}
