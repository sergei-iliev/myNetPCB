package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Box extends Shape {
    private Rectangle rect;
    public Point min,max;
    
    public Box(){
       this(0,0,0,0); 
    }
    
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
       g2.draw(rect);
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
