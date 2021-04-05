package com.mynetpcb.d2.shapes;


import java.awt.Graphics2D;

public class Point extends GeometricFigure{
    public double x;
    public double y;

    public Point() {
     
    }    
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
    public void translate(double x,double y) {       
           this.x += x;
           this.y += y;
    }
    
    public Point middleOf(Point other){
        return new Point((this.x+other.x)/2,(this.y+other.y)/2); 
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
    /**
    * Returns true if point is on a shape, false otherwise
    * @param {Shape} shape Shape of the one of supported types Point, Line, Circle, Segment, Arc, Polygon
    * @returns {boolean}
    */
    public boolean on(GeometricFigure shape) {
        if (shape instanceof Point) {
            return this.equals(shape);
        }

    //              if (shape instanceof Flatten.Line) {
    //                  return shape.contains(this);
    //              }
    //
    //              if (shape instanceof Flatten.Circle) {
    //                  return shape.contains(this);
    //              }
    //
    //              if (shape instanceof Flatten.Segment) {
    //                  return shape.contains(this);
    //              }

        if (shape instanceof Arc) {
            return shape.contains(this);
        }

        if (shape instanceof Polygon) {
            return shape.contains(this);
        }
        return false;
    }
    
    public void mirror(Line line){
      Point prj=line.projectionPoint(this);
      Vector v=new Vector(this,prj);
      prj.translate(v); 
      this.x=prj.x;
      this.y=prj.y;  
    }
    public double distanceTo(double x,double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        return Math.sqrt(dx*dx + dy*dy);      
    }
    public double distanceTo(GeometricFigure shape) {
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
        if (shape instanceof Line) {
            Point closest=((Line)shape).projectionPoint(this);
            return this.distanceTo(closest.x, closest.y);
        }
        throw new IllegalStateException("Unknown shape type - "+shape.getClass());
    }   
    @Override
    public boolean equals(Object obj) {
        if(this==obj){
          return true;  
        }
        if(!(obj instanceof Point)){
          return false;  
        }
        Point pt=(Point)obj;
        return Utils.EQ(this.x, pt.x) && Utils.EQ(this.y, pt.y);
    }
    
    @Override
    public int hashCode() {        
        return 31+Double.hashCode(x)+Double.hashCode(y);        
    }
    
    @Override
    public void paint(Graphics2D g2,boolean fill) {
        Utils.drawCrosshair(g2,4,this); 
    }
    
    @Override
    public String toString() {        
        return "["+x+","+y+"]";
    }
}
