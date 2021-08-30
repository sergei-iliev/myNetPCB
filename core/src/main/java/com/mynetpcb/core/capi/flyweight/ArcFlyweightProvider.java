package com.mynetpcb.core.capi.flyweight;

import java.awt.geom.Arc2D;

import java.util.ArrayList;

public class ArcFlyweightProvider extends FlyweightProvider<Arc2D>{
    
    public ArcFlyweightProvider() {
        pool=new ArrayList<Arc2D>(getMaxPoolSize());
        for(int i=0;i<getMaxPoolSize();i++){
           pool.add(new Arc2D.Double(0,0,0,0,0,0,Arc2D.OPEN)); 
        }
    }

}
