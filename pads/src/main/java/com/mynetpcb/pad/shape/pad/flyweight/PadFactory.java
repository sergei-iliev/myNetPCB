package com.mynetpcb.pad.shape.pad.flyweight;

import java.util.HashMap;
import java.util.Map;


import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.GeometricFigure;
import com.mynetpcb.d2.shapes.Hexagon;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Rectangle;


public class PadFactory {
	 private static Map<Class<? extends GeometricFigure>,GeometricFigure> cache=new HashMap<>();
	 static {
		 cache.put(Circle.class,new Circle(new Point(), 0));
		 cache.put(Rectangle.class,new Rectangle(0, 0, 0, 0));
		 cache.put(Obround.class,new Obround(0, 0, 0, 0));
		 cache.put(Hexagon.class,new Hexagon(0, 0, 10));
	 }
	 public static  GeometricFigure acquire(Class<? extends GeometricFigure> clazz) {
		 var pad=cache.get(clazz);
		 if(pad==null) {
			 throw new IllegalStateException("Unknown figure class: "+clazz.getName());
		 }else {
		     cache.put(clazz, null);
			 return pad;
		 }
	 }
	 
	 public static void release(GeometricFigure figure) {
		  cache.put(figure.getClass(), figure);
	 }
}
