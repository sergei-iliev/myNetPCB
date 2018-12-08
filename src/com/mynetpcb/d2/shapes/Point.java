package com.mynetpcb.d2.shapes;

import com.mynetpcb.d2.Utilities;

import java.awt.Graphics2D;

public class Point extends Shape{
    private double x;
    private double y;
    
    public Point(double x,double y) {
       this.x=x;
       this.y=y;
    }

    @Override
    public Point clone() {
        return new Point(x,y);           
    }
    
    public void rotate(double angle,Point center){
        double a=-1*Utilities.radians(angle);                       
        
        double x_rot = center.x + (this.x - center.x) * Math.cos(a) - (this.y - center.y) * Math.sin(a);
        double y_rot = center.y + (this.x - center.x) * Math.sin(a) + (this.y - center.y) * Math.cos(a);
        
        this.x=x_rot;
        this.y=y_rot;        
    }
    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    @Override
    public void paint(Graphics2D g2) {
        Utilities.drawCrosshair(g2,10,this); 
    }
}
