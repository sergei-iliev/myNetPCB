package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Line extends GeometricFigure {
    public Point p1, p2;

    private Line2D cache = new Line2D.Double();

    public Line(double x1,double y1,double x2,double y2) {
        this.p1 = new Point(x1,y1);
        this.p2 = new Point(x2,y2);
    }

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
    public void setLine(double x1,double y1,double x2,double y2){
        this.p1.set(x1, y1);
        this.p2.set(x2,y2);
    }    
    public void setLine(Point p1, Point p2){
        this.p1=p1;
        this.p2=p2;
    }
    
    @Override
    public Line clone() {
        return new Line(p1.clone(), p2.clone());
    }
    /*
     * Find point belonging to line, which the pt projects on.
     */
    public Point projectionPoint(Point pt) {
        Vector v1 = new Vector(this.p1, pt);
        Vector v2 = new Vector(this.p1, this.p2);

        Vector v = v1.projectionOn(v2);
        //translate point
        double x = this.p1.x + v.x;
        double y = this.p1.y + v.y;
        return new Point(x, y);
    }

    public boolean isHorizontal() {
        Vector v = new Vector(this.p1, this.p2);
        Vector oy = new Vector(1, 0);
        //are they colinear?
        return Utils.EQ(v.cross(oy), 0);
    }

    public boolean isVertical() {
        Vector v = new Vector(this.p1, this.p2);
        Vector oy = new Vector(0, 1);
        //are they colinear?
        return Utils.EQ(v.cross(oy), 0);
    }
    public void move(double offsetX,double offsetY){
        this.p1.move(offsetX,offsetY);
        this.p2.move(offsetX,offsetY);              
    }    
    public void scale(double alpha){
            this.p1.scale(alpha);
            this.p2.scale(alpha);           
    } 
    @Override
    public void rotate(double angle, Point center) {
        this.p1.rotate(angle, center);
        this.p2.rotate(angle, center);
    }

    @Override
    public void rotate(double angle) {
        this.rotate(angle, new Point(0, 0));
    }

    public Point[] getVertices() {
        return new Point[] { this.p1.clone(), this.p2.clone() };
    }
    @Override
    public boolean isPointOn(Point pt, double diviation) {
    
    	return false;
    }

    @Override
    public void paint(Graphics2D g2, boolean fill) {
        cache.setLine(p1.x, p1.y, p2.x, p2.y);
        g2.draw(cache);
    }


}
