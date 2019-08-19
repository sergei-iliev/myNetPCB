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
//    public void translate(vec) {       
//           this.x += vec.x;
//           this.y += vec.y;
//        }
    
    public void scale(double alpha){
           this.x *=alpha;
           this.y *=alpha;                          
    }
    public void rotate(double angle,Point center){
        double a=-1*Utilities.radians(angle);                       
        
        double x_rot = center.x + (this.x - center.x) * Math.cos(a) - (this.y - center.y) * Math.sin(a);
        double y_rot = center.y + (this.x - center.x) * Math.sin(a) + (this.y - center.y) * Math.cos(a);
        
        this.x=x_rot;
        this.y=y_rot;        
    }

    public void rotate(double angle){
        double a=-1*Utilities.radians(angle);                       
        
        double x_rot = (this.x ) * Math.cos(a) - (this.y ) * Math.sin(a);
        double y_rot = (this.x ) * Math.sin(a) + (this.y ) * Math.cos(a);
        
        this.x=x_rot;
        this.y=y_rot;        
    }
    
    public void set(double x,double y){
        this.x=x;
        this.y=y;
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
    public double distanceTo(Shape shape) {
        if (shape instanceof Point) {
                double dx = ((Point)shape).x - this.x;
                double dy = ((Point)shape).y - this.y;
                return Math.sqrt(dx*dx + dy*dy);
        }       
        if (shape instanceof Circle) {
                double dx = ((Circle)shape).getCenter().x - this.x;
                double dy = ((Circle)shape).getCenter().y - this.y;
                return Math.sqrt(dx*dx + dy*dy);                   
        }
        throw new IllegalStateException("Unknown shape type - "+shape.getClass());
    }
    
    @Override
    public void paint(Graphics2D g2) {
        Utilities.drawCrosshair(g2,10,this); 
    }
}
