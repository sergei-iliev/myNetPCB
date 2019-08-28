package com.mynetpcb.d2.shapes;


import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Circle extends Shape{
    private Point pc;
    private double r;
    private Ellipse2D cache=new Ellipse2D.Double();
    
    public Circle(Point pc,double r) {
        this.pc=pc;
        this.r=r;
    }

    @Override
    public Circle clone() {    
        return new Circle(this.pc.clone(), this.r);         
    }
    public Point getCenter() {
        return this.pc;
    }
    public double getRadius(){
        return r;
    }
    public Point[] getVertices() {
        return new Point[]{new Point(this.pc.x-this.r,this.pc.y),new Point(this.pc.x,this.pc.y-this.r),new Point(this.pc.x+this.r,this.pc.y),new Point(this.pc.x,this.pc.y+this.r)};
    }
    public boolean contains(Point pt){
        return Utils.LE(pt.distanceTo(this), this.r);        
    }
    @Override
    public void rotate(double angle, Point center) {
        this.pc.rotate(angle,center); 

    }

    @Override
    public void rotate(double angle) {
        this.pc.rotate(angle); 
    }
    
    public void move(double offsetX,double offsetY){
       this.pc.move(offsetX,offsetY); 
    }
    public void scale(double alpha){
        this.pc.scale(alpha);
        this.r*=alpha;
    }
    public void grow(double offset){
       this.r+=offset; 
    }
    
    @Override
    public void paint(Graphics2D g2,boolean fill) {        
        cache.setFrame(this.pc.x-r, this.pc.y-r, 2*r, 2*r);        
        if(fill){
           g2.fill(cache);    
        }else{
           g2.draw(cache);
        }        
    }

}
