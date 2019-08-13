package com.mynetpcb.core.capi.flyweight;


import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;


public abstract class ShapeFlyweightFactory {
    
    private static final ArcFlyweightProvider arcProvider=new ArcFlyweightProvider();
    
    private static final LineFlyweightProvider lineProvider=new LineFlyweightProvider();
    
    private static final RectFlyweightProvider rectProvider=new RectFlyweightProvider();
        
    private static final EllipseFlyweightProvider ellipseProvider=new EllipseFlyweightProvider();

    private static final GeneralPathFlyweightProvider pathProvider=new GeneralPathFlyweightProvider();
    
    public static  FlyweightProvider getProvider(Class clazz){
        if(clazz==Line2D.class){
           return lineProvider; 
        }
        
        if(clazz==Rectangle2D.class){
          return rectProvider;  
        }
    
        if(clazz==Ellipse2D.class){
          return ellipseProvider;  
        }
        if(clazz==GeneralPath.class){
          return pathProvider;  
        }
        if(clazz==Arc2D.class){
            return arcProvider;
        }
        return null;
    }
    
}

