package com.mynetpcb.d2.shapes;


import java.awt.Graphics2D;

public class Point extends Shape{
    public double x;
    public double y;
    
    public Point(double x,double y) {
       this.x=x;
       this.y=y;
    }

    @Override
    public Point clone() {
        return new Point(x,y);           
    }
    public void translate(Vector vec) {       
           this.x += vec.x;
           this.y += vec.y;
    }
    
    public void scale(double alpha){
           this.x *=alpha;
           this.y *=alpha;                          
    }
    public void rotate(double angle,Point center){
        double a=-1 * Utils.radians(angle);                       
        
        double x_rot = center.x + (this.x - center.x) * Math.cos(a) - (this.y - center.y) * Math.sin(a);
        double y_rot = center.y + (this.x - center.x) * Math.sin(a) + (this.y - center.y) * Math.cos(a);
        
        this.x=x_rot;
        this.y=y_rot;        
    }

    public void rotate(double angle){
        double a=-1 * Utils.radians(angle);                       
        
        double x_rot = (this.x ) * Math.cos(a) - (this.y ) * Math.sin(a);
        double y_rot = (this.x ) * Math.sin(a) + (this.y ) * Math.cos(a);
        
        this.x=x_rot;
        this.y=y_rot;        
    }
    
    public void set(double x,double y){
        this.x=x;
        this.y=y;
    }

    public void set(Point pt){
        this.x=pt.x;
        this.y=pt.y;
    }

    
    public void move(double offsetX,double offsetY){
        this.x+=offsetX;
        this.y+=offsetY;        
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
    public void paint(Graphics2D g2,boolean fill) {
        Utils.drawCrosshair(g2,10,this); 
    }
}
