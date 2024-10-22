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
public class Arcellipse extends Ellipse{    
    public double startAngle,endAngle;
    private Arc2D cache=new Arc2D.Double();

    
    public Arcellipse(double x,double y,double width,double height) {
    	super(x,y,width,height);
        this.vert=new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
        this.startAngle = 45;
        this.endAngle = 100;
    }

    @Override
    public Arcellipse clone() {
        Arcellipse copy= new Arcellipse(this.pc.x,this.pc.y,this.width,this.height);                
        copy.startAngle=this.startAngle;
        copy.endAngle=this.endAngle;
        copy.rotate=this.rotate;
        return copy;        
        
    }
    public double getSweep(){
        return Math.abs(this.endAngle);
}
    public Point getStart() {
        double x=this.pc.x+(this.width*Math.cos(-1*Utils.radians(startAngle)));
        double y=this.pc.y+(this.height*Math.sin(-1*Utils.radians(startAngle)));
        
        Point p=new Point(x,y);
        p.rotate(this.rotate,this.pc);
        return  p;
    }
    public Point getMiddle() {
        double angle = this.endAngle>0 ? this.startAngle + this.getSweep()/2 : this.startAngle - this.getSweep()/2;
        
        double x=this.pc.x+(this.width*Math.cos(-1*Utils.radians(angle)));
        double y=this.pc.y+(this.height*Math.sin(-1*Utils.radians(angle)));
        
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
    /*
     * 
     */
    @Override
    public boolean contains(double x, double y) {    
    	var c=super.contains(x, y);
    	if(!c) {
    		return c;
    	}
    	
        Line l=new Line(this.getStart(),this.getEnd());
        boolean result=l.isLeftOrTop(this.getMiddle());
        //are they on the same line side?
        return (l.isLeftOrTop(new Point(x,y))==result);    	    	
    }
    @Override
    public boolean isPointOn(Point pt, double diviation) {
    	//same as ellipse
        double alpha=-1*Utils.radians(this.rotate);
        double cos = Math.cos(alpha),
        sin = Math.sin(alpha);
        double dx  = (pt.x - this.pc.x),
        dy  = (pt.y - this.pc.y);
        double tdx = cos * dx + sin * dy,
        tdy = sin * dx - cos * dy;

       
        double pos= (tdx * tdx) / (this.width * this.width) + (tdy * tdy) / (this.height * this.height);
        
        
        Vector v=new Vector(this.pc,pt);
	    Vector norm=v.normalize();			  
		//1.in
	    if(pos<1){
		    double xx=pt.x +diviation*norm.x;
			double yy=pt.y +diviation*norm.y;
			//check if new point is out
			if(super.contains(xx,yy)){
				return false;
			}
	    }else{  //2.out
		    double xx=pt.x - diviation*norm.x;
			double yy=pt.y - diviation*norm.y;
			//check if new point is in
			if(!this.contains(xx,yy)){
				return false;
			}		    	
	    }    	
        //narrow down to start and end point/angle
        	double start=new Vector(this.pc,this.getStart()).slope();
        	double end=new Vector(this.pc,this.getEnd()).slope();        	        	        	        	
        	double clickedAngle =new Vector(this.pc,pt).slope();
        	
        	if(this.endAngle>0){
        	  if(start>end){
        		  return (start>=clickedAngle)&&(clickedAngle>=end);	
        	  }else{
        		  return !((start<=clickedAngle)&&(clickedAngle<=end));        		  
        	  }
        	}else{
        	 if(start>end){
    			return !((start>=clickedAngle)&&(clickedAngle>=end));
    		 }else{        			
    			return (start<=clickedAngle)&&(clickedAngle<=end);
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
