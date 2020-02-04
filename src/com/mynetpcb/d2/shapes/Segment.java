package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Segment extends GeometricFigure {
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
    public Point projectionPoint(Point pt) {
        Vector v1 = new Vector(this.ps, pt);
        Vector v2 = new Vector(this.ps, this.pe);

        Vector v = v1.projectionOn(v2);
        //translate point
        double x = this.ps.x + v.x;
        double y = this.ps.y + v.y;
        return new Point(x, y);
    }

    public boolean intersect(Circle circle){
        
        Point projectionPoint = this.projectionPoint(circle.pc);

        double a = (projectionPoint.x - this.ps.x) / ((this.pe.x - this.ps.x) == 0 ? 1 : this.pe.x - this.ps.x);
        double b = (projectionPoint.y - this.ps.y) / ((this.pe.y - this.ps.y) == 0 ? 1 : this.pe.y - this.ps.y);

        double dist = projectionPoint.distanceTo(circle.pc);
        
        if (0 <= a && a <= 1 && 0 <= b && b <= 1) { //is projection between start and end point
            if (!Utils.GT(dist,circle.r)) {
                return true;
            }
        }
        //end points in circle?
        if (Utils.LE(this.ps.distanceTo(circle.pc), circle.r)) {
            return true;
        }
        if (Utils.LE(this.pe.distanceTo(circle.pc), circle.r)) {
            return true;
        }        
        return false;
    }
    /**
     * Liang-Barsky function by Daniel White 
     * 
     * @link http://www.skytopia.com/project/articles/compsci/clipping.html
     */
    public boolean intersect(Box box){
        double x0=ps.x,y0=ps.y,x1=pe.x,y1=pe.y;
        double xmin=box.min.x, xmax=box.max.x, ymin=box.min.y, ymax=box.max.y;
        double t0 = 0, t1 = 1;
        double dx = pe.x - ps.x, dy = pe.y - ps.y;
        double p=0, q=0, r=0;

          for(int edge = 0; edge < 4; edge++) {   // Traverse through left, right, bottom, top edges.
            if (edge == 0) { 
                p = -dx; 
                q = -(xmin - x0); 
            }
            if (edge == 1) { 
                p =  dx; 
                q =  (xmax - x0); 
            }
            if (edge == 2) { 
                p = -dy; 
                q = -(ymin - y0); 
            }
            if (edge == 3) { 
                p =  dy; 
                q =  (ymax - y0); 
            }

            r = q / p;

            if (p == 0 && q < 0) 
                return false;   // Don't draw line at all. (parallel line outside)

            if(p < 0) {
              if (r > t1)
                return false;     // Don't draw line at all.
              else if (r > t0) 
                  t0 = r;     // Line is clipped!
            } else if (p > 0) {
              if(r < t0) 
                  return false;      // Don't draw line at all.
              else if (r < t1) 
                  t1 = r;     // Line is clipped!
            }
          }

          return true;
        
        
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
