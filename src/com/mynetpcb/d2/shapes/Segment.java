package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Segment extends Shape {
    public Point ps,pe;
    private Line2D cache = new Line2D.Double();
    
    public Segment(){
      this(new Point(0,0),new Point(0,0));
    }
    public Segment(Point ps,Point pe){
        this.ps = ps;
        this.pe = pe;
    }
    
    @Override
    public Segment clone() {
        return new Segment(new Point(this.ps.x,this.ps.y),new Point(this.pe.x,this.pe.y));
    }
    public void set(double x1,double y1,double x2,double y2){
            this.ps.set(x1,y1);
            this.pe.set(x2,y2);
    }
    public void set(Point p1,Point p2){
            this.ps.set(p1.x,p1.y);
            this.pe.set(p2.x,p2.y);
    }
    public double length() {
        return this.ps.distanceTo(this.pe);
    } 
    public Box box() {
        return new Box(
            Math.min(this.ps.x, this.pe.x),
            Math.min(this.ps.y, this.pe.y),
            Math.max(this.ps.x, this.pe.x),
            Math.max(this.ps.y, this.pe.y)
        );
    }   
    public Point middle() {
        return new Point((this.ps.x + this.pe.x)/2, (this.ps.y + this.pe.y)/2);
    } 
    public void move(double offsetX,double offsetY){
        this.ps.move(offsetX,offsetY);
        this.pe.move(offsetX,offsetY);              
    }
    public void scale(double alpha){
            this.ps.scale(alpha);
            this.pe.scale(alpha);           
    }    
    public void mirror(Line line){
            this.ps.mirror(line);
            this.pe.mirror(line);
    }    
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        cache.setLine(ps.x, ps.y, pe.x, pe.y);
        g2.draw(cache);
    }

    @Override
    public void rotate(double angle, Point center) {
        this.ps.rotate(angle,center);
        this.pe.rotate(angle,center);
    }

    @Override
    public void rotate(double angle) {        
        this.rotate(angle, this.middle());
    }
}
