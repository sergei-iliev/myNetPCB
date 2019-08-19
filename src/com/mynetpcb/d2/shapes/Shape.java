package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;

public abstract class Shape{

   public abstract Shape clone();
   
   public abstract  void paint(Graphics2D g2); 
   
   public abstract void rotate(double angle,Point center);
   
   public abstract void rotate(double angle);
}
