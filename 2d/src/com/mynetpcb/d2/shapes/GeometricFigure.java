package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;

public abstract class GeometricFigure{

   public abstract GeometricFigure clone();
   
   public boolean contains(Point pt){return false;}
        
   public abstract  void paint(Graphics2D g2,boolean fill); 
   
   public abstract void rotate(double angle,Point center);
   
   public abstract void rotate(double angle);
}
