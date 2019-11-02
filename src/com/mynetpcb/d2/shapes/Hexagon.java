package com.mynetpcb.d2.shapes;


public class Hexagon extends Polygon {
    public Point pc;
    public double width;
    
    public Hexagon(double x,double y,double width) {    
        this.pc=new Point(x,y);
        this.width=width;               
        this.reset();
    }
    public void reset(){
        this.points.clear();
            
        double da = (2 * Math.PI) / 6;
        double lim = (2 * Math.PI) - (da / 2);

        double r=this.width/2;
        Point point=new Point(r * Math.cos(0), r * Math.sin(0));
        point.move(this.pc.x,this.pc.y);            
        
        this.points.add(point);
        for (double a = da; a < lim; a += da) {
            point=new Point(r * Math.cos(a),r * Math.sin(a));
            point.move(this.pc.x,this.pc.y);
            this.points.add(point);
        }               
    }
    @Override
    public Hexagon clone() {
        
        Hexagon copy=new Hexagon(this.pc.x,this.pc.y,this.width);               
        copy.points.clear();
        this.points.forEach(point->{
           copy.points.add(point.clone());
        });
        return copy;
    }
    
    public void scale(double alpha){
            this.pc.scale(alpha);
            this.width*=alpha;  
            super.scale(alpha);                               
    }       
    public void setWidth(double width){
            this.width=width;
            this.reset();                     
    }
    public void move(double offsetX,double offsetY){
        this.pc.move(offsetX,offsetY);
        super.move(offsetX, offsetY);  
    }
    
    public void rotate(double angle,Point center){
        this.pc.rotate(angle,center);
        super.rotate(angle,center);
    } 
    
    public void rotate(double angle){
        this.pc.rotate(angle,new Point(0,0));
        super.rotate(angle,new Point(0,0));
    } 
    
    
    
}
