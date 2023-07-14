package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;


public abstract class GeometricFigure{

   public abstract GeometricFigure clone();
   
   public boolean contains(Point pt){return false;}   
   
   public boolean contains(double x,double y){return false;}  
   
   public abstract  void paint(Graphics2D g2,boolean fill); 
   
   public abstract void rotate(double angle,Point center);
   
   public abstract void rotate(double angle);
   
   public <G extends GeometricFigure> void assign(G drawing) {}
   
   /*
    * Test if point on shape contour
    */
   public abstract boolean isPointOn(Point pt,double diviation);
}
