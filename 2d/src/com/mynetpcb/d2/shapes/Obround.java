package com.mynetpcb.d2.shapes;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

/**
 * Obround is regarded as a line with variable thickness
 * @input pt - center
 * @input width - relative,  line width + 2  arcs at both ends
 * this.width=ps to pe + 2 rcs radius
 * @input height - relative but still height
 * @warning obround may change its width and height - it should recalculate its size
 */
public class Obround extends GeometricFigure {
    public Point pc;
    public double width;
    public double height;
    public Point ps, pe;
    private Line2D cache = new Line2D.Double();
    
    public Obround(Point pt, double width, double height) {
        this.pc = pt;
        this.width = width;
        this.height = height;
        this.reset();
    }
    public Obround(double x,double y, double width, double height) {
        this(new Point(x,y),width,height);
    }
    @Override
    public Obround clone() {
        Obround copy = new Obround(new Point(this.pc.x,this.pc.y), this.width, this.height);
        copy.ps.x = this.ps.x;
        copy.ps.y = this.ps.y;

        copy.pe.x = this.pe.x;
        copy.pe.y = this.pe.y;
        return copy;
    }
    public Box box(){             
             
        double r=this.getDiameter()/2;
        //first point
        Vector v=new Vector(this.pe,this.ps);
        Vector n=v.normalize();
        double a=this.ps.x +r*n.x;
        double b=this.ps.y +r*n.y;                         
                                        
        v.rotate90CW();
        Vector norm=v.normalize();
        
        double x=a +r*norm.x;
        double y=b +r*norm.y;                      
        Point pa=new Point(x,y);
        
        norm.invert();
        x=a +r*norm.x;
        y=b +r*norm.y;                  
        Point pb=new Point(x,y);
        //second point
        v=new Vector(this.ps,this.pe);
        n=v.normalize();
        double c=this.pe.x +r*n.x;
        double d=this.pe.y +r*n.y;                         
        
        v.rotate90CW();
        norm=v.normalize();
        
        x=c +r*norm.x;
        y=d +r*norm.y;                  
        Point pc=new Point(x,y);
        
        norm.invert();
        x=c +r*norm.x;
        y=d +r*norm.y;                  
        Point pd=new Point(x,y);
        
        return new Box(pa,pb,pc,pd);                                
    }
    
    private void reset() {
        double w = 0, h = 0;
        if (this.width > this.height) { //horizontal
            w = this.width;
            h = this.height;
            double d = (w - h); //always positive
            this.ps = new Point(this.pc.x - (d / 2), this.pc.y);
            this.pe = new Point(this.pc.x + (d / 2), this.pc.y);
        } else { //vertical
            w = this.height;
            h = this.width;
            double d = (w - h); //always positive
            this.ps = new Point(this.pc.x, this.pc.y - (d / 2));
            this.pe = new Point(this.pc.x, this.pc.y + (d / 2));
        }
    }

    public void setSize(double width,double height){
      this.height = height;
      this.width=width;
      this.reset();                         
    }
    /**
    if (x-x1)/(x2-x1) = (y-y1)/(y2-y1) = alpha (a constant), then the point C(x,y) will lie on the line between pts 1 & 2.
    If alpha < 0.0, then C is exterior to point 1.
    If alpha > 1.0, then C is exterior to point 2.
    Finally if alpha = [0,1.0], then C is interior to 1 & 2.
     */
    public boolean contains(Point pt) {
        Line l = new Line(this.ps, this.pe);
        Point projectionPoint = l.projectionPoint(pt);

        double a = (projectionPoint.x - this.ps.x) / ((this.pe.x - this.ps.x) == 0 ? 1 : this.pe.x - this.ps.x);
        double b = (projectionPoint.y - this.ps.y) / ((this.pe.y - this.ps.y) == 0 ? 1 : this.pe.y - this.ps.y);

        double dist = projectionPoint.distanceTo(pt);
        //arc diameter
        double r = (this.width > this.height ? this.height : this.width);

        if (0 <= a && a <= 1 && 0 <= b && b <= 1) { //is projection between start and end point
            if (dist <= (r / 2)) {
                return true;
            }

        }

        //check the 2 circles
        if (Utils.LE(this.ps.distanceTo(pt), r / 2)) {
            return true;
        }
        if (Utils.LE(this.pe.distanceTo(pt), r / 2)) {
            return true;
        }
        return false;

    }

    public Point getCenter() {
        return this.pc;
    }

    public void mirror(Line line){
        this.pc.mirror(line);
        this.ps.mirror(line);
        this.pe.mirror(line);        
    }
    @Override
    public void rotate(double angle, Point center) {
        this.pc.rotate(angle, center);
        this.ps.rotate(angle, center);
        this.pe.rotate(angle, center);
    }

    @Override
    public void rotate(double angle) {
        this.rotate(angle, this.pc);
    }

    public void scale(double alpha) {
        this.pc.scale(alpha);
        this.ps.scale(alpha);
        this.pe.scale(alpha);
        this.width *= alpha;
        this.height *= alpha;

    }

    public void move(double offsetX, double offsetY) {
        this.pc.move(offsetX, offsetY);
        this.ps.move(offsetX, offsetY);
        this.pe.move(offsetX, offsetY);
    }

    public void grow(double offset) {
        if(Utils.GE(width,height)){
            this.height +=  2*offset;
        } else {
            this.width +=  2*offset;
        }
    }
    public double getDiameter(){
        if(Utils.GE(width,height))
          return this.height;
        else
          return this.width;        
    }
    @Override
    public boolean isPointOn(Point pt, double diviation) {
    
    	return false;
    }
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        Stroke s=g2.getStroke();
        
        double lineWidth=this.getDiameter();
        cache.setLine(this.ps.x, this.ps.y,this.pe.x, this.pe.y);       
        g2.setStroke(new BasicStroke((float) lineWidth,BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
   
        g2.draw(cache);                
        
        
        g2.setStroke(s);
        
    }


}
