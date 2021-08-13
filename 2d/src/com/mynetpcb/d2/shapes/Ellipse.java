package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class Ellipse extends GeometricFigure {
    public double width,height;
    public Point pc;
    public double rotate;
    private Point vert[]={new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};

    private Ellipse2D cache=new Ellipse2D.Double();
    
    public Ellipse(double x,double y,double width,double height){
       this.pc=new Point(x,y);
       this.width=width;
       this.height=height;       
       
    }
    @Override
    public Ellipse clone() {
        Ellipse copy= new Ellipse(this.pc.x,this.pc.y,this.width,this.height);                
        copy.rotate=this.rotate;
        return copy;
    }
    @Override
    public boolean contains(Point pt) {            
      return this.contains(pt.x, pt.y);
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
    public Box box(){
        Point topleft=this.pc.clone();
        topleft.move(-this.width,-this.height);
        Rectangle rect=new Rectangle(topleft.x,topleft.y,2*this.width,2*this.height);
        rect.rotate(this.rotate,this.pc);
        return rect.box();
    }      
    
    public void move(double offsetX,double offsetY){
        this.pc.move(offsetX,offsetY);              
    }
    public void mirror(Line line){
        this.pc.mirror(line);              
    }
    
    public void scale(double alpha){
       this.pc.scale(alpha);
       this.width*=alpha;
       this.height*=alpha;
    }
    
    @Override
    public boolean isPointOn(Point pt,double diviation){
        double alpha=-1*Utils.radians(this.rotate);
        double cos = Math.cos(alpha),
        sin = Math.sin(alpha);
        double dx  = (pt.x - this.pc.x),
        dy  = (pt.y - this.pc.y);
        double tdx = cos * dx + sin * dy,
        tdy = sin * dx - cos * dy;

       
        double pos= (tdx * tdx) / (this.width * this.width) + (tdy * tdy) / (this.height * this.height);
        //is pt on shape
        if(Utils.EQ(pos,1)){
        	return true;
        }
        
        Vector v=new Vector(this.pc,pt);
	    Vector norm=v.normalize();			  
		//1.in
	    if(pos<1){
		    double xx=pt.x +diviation*norm.x;
			double yy=pt.y +diviation*norm.y;
			//check if new point is out
			if(!this.contains(xx,yy)){
				return true;
			}
	    }else{  //2.out
		    double xx=pt.x - diviation*norm.x;
			double yy=pt.y - diviation*norm.y;
			//check if new point is in
			if(this.contains(xx,yy)){
				return true;
			}		    	
	    }

      	return false;
    }      
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        cache.setFrame(this.pc.x-width, this.pc.y-height,2*width,2*height);        
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
    public Point[] vertices() {
        this.vert[0].set(this.pc.x-this.width,this.pc.y);
        this.vert[1].set(this.pc.x,this.pc.y-this.height);
        this.vert[2].set(this.pc.x+this.width,this.pc.y);
        this.vert[3].set(this.pc.x,this.pc.y+this.height);                         
        return this.vert;       
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
    public void rotate(double angle, Point center) {
        this.pc.rotate((angle-this.rotate),center);
        this.rotate=angle;
    }

    @Override
    public void rotate(double angle) {
       this.rotate(angle,this.pc);
       //this.rotate=angle;
    }
}
