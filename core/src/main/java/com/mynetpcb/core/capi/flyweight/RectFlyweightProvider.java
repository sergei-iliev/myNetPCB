package com.mynetpcb.core.capi.flyweight;

import java.awt.geom.Rectangle2D;

import java.util.ArrayList;


public class RectFlyweightProvider extends FlyweightProvider<Rectangle2D>{
     
    
    public RectFlyweightProvider() {
        pool=new ArrayList<Rectangle2D>(getMaxPoolSize());
        for(int i=0;i<getMaxPoolSize();i++){
           pool.add(new Rectangle2D.Double()); 
        }
    }
}
