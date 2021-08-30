package com.mynetpcb.core.capi.flyweight;


import java.awt.geom.Line2D;

import java.util.ArrayList;


public class LineFlyweightProvider extends FlyweightProvider<Line2D> { 
    
    public LineFlyweightProvider() {
        pool=new ArrayList<Line2D>(getMaxPoolSize());
        for(int i=0;i<getMaxPoolSize();i++){
           pool.add(new Line2D.Double()); 
        }
    }

    @Override
    protected byte getMaxPoolSize() {
        return 6;
    }
}
