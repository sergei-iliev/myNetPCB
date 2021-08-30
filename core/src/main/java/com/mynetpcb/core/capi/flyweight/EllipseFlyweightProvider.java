package com.mynetpcb.core.capi.flyweight;

import java.awt.geom.Ellipse2D;

import java.util.ArrayList;


public class EllipseFlyweightProvider  extends FlyweightProvider<Ellipse2D>{
      
    public EllipseFlyweightProvider() {
        pool=new ArrayList<Ellipse2D>(getMaxPoolSize());
        for(int i=0;i<getMaxPoolSize();i++){
           pool.add(new Ellipse2D.Double()); 
        }
    }
}
