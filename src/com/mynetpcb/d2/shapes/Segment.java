package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;

public class Segment extends Shape {
    public Point ps,pe;
    
    public Segment(Point ps,Point pe){
        this.ps = ps;
        this.pe = pe;
    }
    
    @Override
    public Shape clone() {
        return new Segment(this.ps, this.pe);
    }
    public void set(double x1,double y1,double x2,double y2){
            this.ps.set(x1,y1);
            this.pe.set(x2,y2);
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
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        // TODO Implement this method

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
