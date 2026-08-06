package com.mynetpcb.core.capi.flyweight;


import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;


public abstract class ShapeFlyweightFactory {
    
    //private static final ArcFlyweightProvider arcProvider=new ArcFlyweightProvider();
    
    //private static final LineFlyweightProvider lineProvider=new LineFlyweightProvider();
    
    //private static final RectFlyweightProvider rectProvider=new RectFlyweightProvider();
        
    private static final EllipseFlyweightProvider ellipseProvider=new EllipseFlyweightProvider();

    //private static final GeneralPathFlyweightProvider pathProvider=new GeneralPathFlyweightProvider();
    
    private static final Map<Class, FlyweightProvider> providers=new HashMap<>(){{
    	  put(Line2D.class, new LineFlyweightProvider());
    	  put(Rectangle2D.class, new RectFlyweightProvider());
    	  put(Ellipse2D.class, new EllipseFlyweightProvider());
    	  put(GeneralPath.class, new GeneralPathFlyweightProvider());
    	  put(Arc2D.class, new  ArcFlyweightProvider());
    	  
    	  
    	  
    }};
    
    public static  FlyweightProvider getProvider(Class clazz){
//        if(clazz==Line2D.class){
//           return lineProvider; 
//        }
//        
//        if(clazz==Rectangle2D.class){
//          return rectProvider;  
//        }
    
//        if(clazz==Ellipse2D.class){
//          return ellipseProvider;  
//        }
//        if(clazz==GeneralPath.class){
//          return pathProvider;  
//        }
//        if(clazz==Arc2D.class){
//            return arcProvider;
//        }
    	
        return providers.getOrDefault(clazz, null);
    }
    
}

