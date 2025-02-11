package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import java.awt.geom.Arc2D;

import java.util.ArrayList;
import java.util.List;

public class Arc  extends GeometricFigure{
    public Point pc;
    public double r ;
    public double startAngle ;
    public double endAngle; 
    private Point vert[]={new Point(0,0),new Point(0,0),new Point(0,0)};//start,middle,end
    
    private Arc2D cache=new Arc2D.Double();
    
    public Arc(){
        
    }
    public Arc(Point pc,double  r,double startAngle,double endAngle) {
      this.pc = pc;
      this.r = r;
      this.startAngle = startAngle;
      this.endAngle = endAngle; 
    }

    @Override
    public Arc clone() {        
        return new Arc(this.pc.clone(),this.r,this.startAngle,this.endAngle); 
    }
    public Box box(){
        List<Point> points=new ArrayList<>();        
        Point p1=this.pc.clone();p1.translate(this.r, 0);
        if(p1.on(this)){
           points.add(p1);  
        }
        Point p2=this.pc.clone();p2.translate(0,this.r);
        if(p2.on(this)){
           points.add(p2);  
        }
        Point p3=this.pc.clone();p3.translate(-this.r,0);
        if(p3.on(this)){
           points.add(p3);  
        }
        Point p4=this.pc.clone();p4.translate(0,-this.r);
        if(p4.on(this)){
           points.add(p4);  
        }
      points.add(this.getStart());   points.add(this.getEnd());  
      return new Box(points);         
    }
   
    public double area(){
       return  ( Math.PI * this.r*this.r ) * ( this.getSweep() / 360 );   
    }
    public Point getCenter(){
            return this.pc;
    }
    public Point  getStart() {
    	vert[0].x=this.pc.x + this.r;
    	vert[0].y=this.pc.y;        
        vert[0].rotate(this.startAngle, this.pc);
        return vert[0];
    }
            
    public Point getMiddle() {
        double angle = this.endAngle>0 ? this.startAngle + this.getSweep()/2 : this.startAngle - this.getSweep()/2;
        vert[1].x=this.pc.x + this.r;
        vert[1].y=this.pc.y;        
        vert[1].rotate(angle, this.pc);
        return vert[1];
    }
    
    public Point getEnd() {
        vert[2].x=this.pc.x + this.r;
        vert[2].y=this.pc.y;    	
        vert[2].rotate((this.startAngle+this.endAngle), this.pc);
        return vert[2];
    }
    
    public double getSweep(){
            return Math.abs(this.endAngle);
    }

    public Point[] vertices() {    	
    	return vert;        
    }
    @Override
    public boolean contains(double x,double y){
    	return this.contains(new Point(x,y)); 
    }
    public boolean contains(Point pt){
                
            //is on circle
            if (!Utils.EQ(this.pc.distanceTo(pt), this.r)){
                //is outside of the circle
                if (Utils.GE(this.pc.distanceTo(pt), this.r)){
                    return false;
                }                
            }
            Line l=new Line(this.pc,this.getMiddle());
            Point projectionPoint=l.projectionPoint(pt);
            
       
            Point mid=new Point((this.getStart().x+this.getEnd().x)/2,(this.getStart().y+this.getEnd().y)/2);  
            
            double dist1=this.getMiddle().distanceTo(mid);
            double dist2=this.getMiddle().distanceTo(projectionPoint);
            
            return Utils.GE(dist1,dist2);

    }
    
    @Override
    public boolean isPointOn(Point pt,double diviation){        
    		boolean isInside=false;
        	double clickedAngle =new Vector(this.pc,pt).slope();    		            		
    		double angle = 360 - clickedAngle;		
    		//test angle		
    	    if(this.endAngle>0){ //counter clockwise    	    	
    	    	if(angle-this.startAngle>0){
    	    	  angle=(angle-this.startAngle);
    	    	}else{
    	    	  angle=((360-this.startAngle)+angle);	
    	    	}
    	    	isInside=(angle<this.endAngle);
    	    }else{ //clockwise    	    	
    	    	if((angle-this.startAngle)>0){
    	    	  angle=((angle-360)-this.startAngle);	
    	    	}else{
    	    	  angle=angle-this.startAngle;
    	    	}
    	    	isInside=(Math.abs(angle)<Math.abs(this.endAngle));
    	    }    		
    		if(!isInside){
    			return false;
    		}
    		//test distance
    		double dist=this.pc.distanceTo(pt);

    		return ((this.r-diviation)<dist&&(this.r+diviation)>dist);
    }
    
    public void move(double offsetX,double offsetY){
      this.pc.move(offsetX,offsetY);        
    }
    @Override    
    public void rotate(double angle,Point center){
             this.pc.rotate(angle,center);
             this.startAngle+=angle;
             if(this.startAngle>=360){
                     this.startAngle-=360;
             }
             if(this.startAngle<0){
                     this.startAngle+=360; 
             }
    }
    @Override
    public void rotate(double angle) {
        this.rotate(angle,new Point(0,0));
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
    public void scale(double alpha){
       this.pc.scale(alpha);
       this.r*=alpha;
    }

    @Override
    public void paint(Graphics2D g2, boolean fill) {
   
        cache.setArc(this.pc.x-r, this.pc.y-r, 2*r, 2*r,startAngle,endAngle,(fill?Arc2D.CHORD:Arc2D.OPEN));
        if(fill){
          g2.fill(cache);    
        }else{
          g2.draw(cache);
        }

    }

}
