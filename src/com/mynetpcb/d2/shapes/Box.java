package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import java.util.Collection;

public class Box extends Shape {
    private Rectangle rect=new Rectangle();
    public Point min,max;
    
    public Box(){
       this(0,0,0,0); 
    }
    public Box(Collection<Point> points){
            double x=Integer.MAX_VALUE,y=Integer.MAX_VALUE;
            for(Point point:points){
               x=Math.min(x,point.x);
               y=Math.min(y,point.y);
            }  
            this.min=new Point(x,y);
    
            x=Integer.MIN_VALUE;
            y=Integer.MIN_VALUE;
                for(Point point:points){
               x=Math.max(x,point.x);
               y=Math.max(y,point.y);
            }  
            this.max=new Point(x,y);       
        } 
//    public Box(Point[] points){
//        double x=Integer.MAX_VALUE,y=Integer.MAX_VALUE;
//        for(int i = 0; i < points.length ; ++ i){
//           x=Math.min(x,points[i].x);
//           y=Math.min(y,points[i].y);
//        }  
//        this.min=new Point(x,y);
//
//        x=Integer.MIN_VALUE;
//        y=Integer.MIN_VALUE;
//        for(int i = 0; i < points.length; ++ i){
//           x=Math.max(x,points[i].x);
//           y=Math.max(y,points[i].y);
//        }  
//        this.max=new Point(x,y);       
//    }    
    public Box(double x1,double y1,double x2,double y2) {
        this.min = new Point(x1,y1);
        this.max = new Point(x2,y2);
    }

    public static Box fromRect(double x,double y,double width,double height){
                  Box box=new Box(x,y,x+width,y+height);
                  
                  return box;
    }
    public void setRect(double x,double y,double width,double height){
        min.set(x,y);
        max.set(x+width,y+height);
    }
    @Override
    public Box clone() {
        return new Box(this.min.x,this.min.y,this.max.x,this.max.y);
    }
    public void grow(double offset){
        this.min.x-=offset;
        this.min.y-=offset;
        
        this.max.x+=offset;
        this.max.y+=offset;

    }  
    public Point getCenter() {
        return new Point( (this.min.x + this.max.x)/2, (this.min.y + this.max.y)/2 );
    }
    public double getX(){
       return this.min.x; 
    }
    public double getY(){
        return this.min.y; 
    }
    public double getWidth(){
        return this.max.x-this.min.x;
    }
    
    public double getHeight(){
        return this.max.y-this.min.y;
    }
    public void scale(double alpha){
      this.min.scale(alpha);
      this.max.scale(alpha);
    }
    public boolean contains(int x,int y){
      
        if(this.min.x<=x&&x<=this.max.x){
          if(this.min.y<=y&&y<=this.max.y)
                return true;
        }
        return false;
       
    }    
    public boolean contains(Point point){
      
        if(this.min.x<=point.x&&point.x<=this.max.x){
          if(this.min.y<=point.y&&point.y<=this.max.y)
                return true;
        }
        return false;
       
    }
    public boolean not_intersects(Box other) {
        return (
            this.max.x < other.min.x ||
            this.min.x > other.max.x ||
            this.max.y < other.min.y ||
            this.min.y > other.max.y
        );
    }
    public void move(double offsetX,double offsetY){
        this.min.move(offsetX,offsetY);
        this.max.move(offsetX,offsetY);
    }
    public boolean intersects(Box other) {        
        return !this.not_intersects(other);              
    }
    
    public Point[] getVertices() {
        return new Point[]{this.min,new Point(this.max.x,this.min.y),this.max,new Point(this.min.x,this.max.y)};    
    }
    
    @Override
    public void paint(Graphics2D g2, boolean fill) {
       rect.setRect(getX(),getY(), getWidth(), getHeight());
       if(fill){
         g2.fill(rect);   
       }else{
         g2.draw(rect);
       }
    }

    @Override
    public void rotate(double angle, Point center) {
        // TODO Implement this method

    }

    @Override
    public void rotate(double angle) {
        // TODO Implement this method
    }
}
