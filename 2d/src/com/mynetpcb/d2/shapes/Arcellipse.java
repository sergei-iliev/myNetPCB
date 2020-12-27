package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

/**
 *
 * @param {Point} pc - arc center
 * @param {number} w - horizontal radius
 * @param {number} w - vertical radius
 * @param {number} startAngle - start angle in degrees from 0 to 360
 * @param {number} endAngle - end angle in degrees from -360 to 360
 */
public class Arcellipse extends GeometricFigure{
    public double width,height;
    public Point pc;
    public double rotate;
    private Point vert[]={new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
    
    public double startAngle,endAngle;
    private Arc2D cache=new Arc2D.Double();
    
    public Arcellipse(double x,double y,double width,double height) {
        this.pc=new Point(x,y);
        this.width=width;
        this.height=height;    
        this.startAngle = 90;
        this.rotate=0;
        this.endAngle = 236;
    }

    @Override
    public Arcellipse clone() {
        Arcellipse copy= new Arcellipse(this.pc.x,this.pc.y,this.width,this.height);                
        copy.startAngle=this.startAngle;
        copy.endAngle=this.endAngle;
        copy.rotate=this.rotate;
        return copy;        
        
    }
    public Point getStart() {
        double x=this.pc.x+(this.width*Math.cos(-1*Utils.radians(startAngle)));
        double y=this.pc.y+(this.height*Math.sin(-1*Utils.radians(startAngle)));
        
        Point p=new Point(x,y);
        p.rotate(this.rotate,this.pc);
        return  p;
    }
    
    public Point[] vertices(){         
        this.vert[0].set(this.pc.x-this.width,this.pc.y);
        this.vert[1].set(this.pc.x,this.pc.y-this.height);
        this.vert[2].set(this.pc.x+this.width,this.pc.y);
        this.vert[3].set(this.pc.x,this.pc.y+this.height);                               
        Point s=this.getStart();
        Point e=this.getEnd();
        this.vert[4].set(s.x,s.y);
        this.vert[5].set(e.x,e.y);   
        return this.vert;
    }
    
    public Point getEnd() {
        //let angles=this._convert(this.startAngle,this.endAngle);
        double x=this.pc.x+(this.width*Math.cos(Utils.radians(convertExtend(startAngle,endAngle))));
        double y=this.pc.y+(this.height*Math.sin(Utils.radians(convertExtend(startAngle,endAngle))));
        
        Point p=new Point(x,y);
        p.rotate(this.rotate,this.pc);
        return  p;
    }    
    private double convertExtend(double start,double extend){
        double s = 360 - start;
        double e=0;
        if(extend>0){
            e = 360 - (start+extend); 
        }else{
         if(start>Math.abs(extend)){    
            e = s+Math.abs(extend); 
         }else{
            e = Math.abs(extend+start);
         }               
        }
        return  e;        
    }
    public Box box(){
        Point topleft=this.pc.clone();
        topleft.move(-this.width,-this.height);
        Rectangle rect=new Rectangle(topleft.x,topleft.y,2*this.width,2*this.height);
        rect.rotate(this.rotate,this.pc);
        return rect.box();
    }    
    public void scale(double alpha){
       this.pc.scale(alpha);
       this.width*=alpha;
       this.height*=alpha;
    }    
    public void move(double offsetX,double offsetY){
        this.pc.move(offsetX,offsetY);              
    }
    public boolean contains(double x,double y) {            
            double alpha=-1*Utils.radians(this.rotate);
            double cos = Math.cos(alpha),
            sin = Math.sin(alpha);
            double dx  = (x - this.pc.x),
            dy  = (y - this.pc.y);
            double tdx = cos * dx + sin * dy,
            tdy = sin * dx - cos * dy;

        return (tdx * tdx) / (this.width * this.width) + (tdy * tdy) / (this.height * this.height) <= 1;
    } 
    
    @Override
    public void rotate(double angle, Point center) {
        this.pc.rotate((angle-this.rotate),center);
        this.rotate=angle;
    }

    @Override
    public void rotate(double angle) {
       this.rotate(angle,this.pc);
    }   
    public void mirror(Line line){
        this.pc.mirror(line);
        this.endAngle=-1*this.endAngle;
        if(line.isVertical()){
                if(this.startAngle>=0&&this.startAngle<=180){
                  this.startAngle=180-this.startAngle;  
                }else{
                  this.startAngle=180+(360-this.startAngle);            
                }
        }else{
                this.startAngle=360-this.startAngle; 
        }                        
    }    
    public void resize(double offX,double offY,Point pt){      
      if(pt.equals(vert[0])){
                    Point point=vert[0];
                    point.move(offX,offY);

                    Vector v1=new Vector(pt,point);
                    Vector v2=new Vector(this.pc,pt);
    
                    Vector v=v1.projectionOn(v2);
    //translate point
                    double x=pt.x +v.x;                    
                    if(this.pc.x>x){
                      this.width=this.pc.x-x;
                    }
      }else if(pt.equals(vert[1])){
                    Point point=vert[1];
                    point.move(offX,offY);

                    Vector v1=new Vector(pt,point);
                    Vector v2=new Vector(this.pc,pt);
    
                    Vector v=v1.projectionOn(v2);
    //translate point
                    //let x=pt.x +v.x;
                    double y=pt.y + v.y;
                    if(this.pc.y>y){
                      this.height=this.pc.y-y;
                    }
      }else if(pt.equals(vert[2])){
                    Point point=vert[2];
                    point.move(offX,offY);

                    Vector v1=new Vector(pt,point);
                    Vector v2=new Vector(this.pc,pt);
    
                    Vector v=v1.projectionOn(v2);
    //translate point
                    double x=pt.x +v.x;
                    //let y=pt.y + v.y;
                    if(x>this.pc.x){
                       this.width=x-this.pc.x;
                    }
      }else{
                    Point point=vert[3];
                    point.move(offX,offY);

                    Vector v1=new Vector(pt,point);
                    Vector v2=new Vector(this.pc,pt);
    
                    Vector v=v1.projectionOn(v2);
    //translate point
                    
                    double y=pt.y + v.y;
                    if(y>this.pc.y){
                       this.height=y-this.pc.y;
                    }
      }
    }
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        cache.setArc(this.pc.x-width, this.pc.y-height, 2*width, 2*height,startAngle,endAngle,Arc2D.OPEN);
        AffineTransform old = g2.getTransform();

        AffineTransform rotate =
            AffineTransform.getRotateInstance(-1*Utils.radians(this.rotate), this.pc.x,this.pc.y);
        g2.transform(rotate);
        
        if(fill){
           g2.fill(cache);    
        }else{
           g2.draw(cache);
        } 

        g2.setTransform(old);

    }

}
