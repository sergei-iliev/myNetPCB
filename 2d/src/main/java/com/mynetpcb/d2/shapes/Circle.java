package com.mynetpcb.d2.shapes;


import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Circle extends GeometricFigure{
    public Point pc;
    public double r;
    private Ellipse2D cache=new Ellipse2D.Double();
    protected Point vert[]={new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
    public Circle(Point pc,double r) {
        this.pc=pc;
        this.r=r;
    }

    @Override
    public Circle clone() {    
        return new Circle(this.pc.clone(), this.r);         
    }
    @Override
    public void assign(GeometricFigure drawing) {
    	this.pc.set(((Circle)drawing).pc);
    	this.r=((Circle)drawing).r;
    }
    public Box box(){
        return new Box(
            this.pc.x - this.r,
            this.pc.y - this.r,
            this.pc.x + this.r,
            this.pc.y + this.r
        );
    }
    public double area(){
        return ( Math.PI * this.r*this.r ); 
    }
    public Point getCenter(){
        return pc;
    }
    public Point[] vertices() {
        vert[0].x=this.pc.x-this.r;
        vert[0].y=this.pc.y;
        vert[1].x=this.pc.x;
        vert[1].y=this.pc.y-this.r;
        vert[2].x=this.pc.x+this.r;
        vert[2].y=this.pc.y;
        vert[3].x=this.pc.x;
        vert[3].y=this.pc.y+this.r;
        return vert;
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
    public void mirror(Line line){
        this.pc.mirror(line);
    }
    public Point resize(double xoffset, double yoffset, Point point) {
        double radius=this.r;

        if(Utils.EQ(point.x,this.pc.x)){
          if(point.y>this.pc.y){
                  radius+=yoffset;
          }else{
                  radius-=yoffset;  
          }     
        }
        if(Utils.EQ(point.y,this.pc.y)){
            if(point.x>this.pc.x){
                  radius+=xoffset;
            }else{
                  radius-=xoffset;  
            }   
        }
        if(radius>0){ 
          this.r=radius;
        }       
        for(Point p:this.vertices()){
        	if(point==p) {
        		return p;
        	}
        }
        return null;
    }    
    public void scale(double alpha){
        this.pc.scale(alpha);
        this.r*=alpha;
    }
    public void grow(double offset){
       this.r+=offset; 
    }
    @Override
    public boolean isPointOn(Point pt, double diviation) {    
		//test distance
		double dist=this.pc.distanceTo(pt);
		return ((this.r-diviation)<dist&&(this.r+diviation)>dist);
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
