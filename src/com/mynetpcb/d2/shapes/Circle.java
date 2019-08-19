package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;

public class Circle extends Shape{
    private Point pc;
    private double r;
    
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
    public boolean contains(Point pt){
        return d2.utils.LE(pt.distanceTo(this), this.r);        
    }
    @Override
    public void paint(Graphics2D g2) {
        
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
