package com.mynetpcb.d2.shapes;

public class Hexagon extends Polygon {
    public Point center;
    public double width;
    
    public Hexagon(Point center,double width) {    
        this.center=center;
        this.width=width;               
        this.reset();
    }
    public void reset(){
        this.points.clear();
            
        double da = (2 * Math.PI) / 6;
        double lim = (2 * Math.PI) - (da / 2);

        double r=this.width/2;
        Point point=new Point(r * Math.cos(0), r * Math.sin(0));
        point.move(this.center.x,this.center.y);            
        
        this.points.add(point);
        for (double a = da; a < lim; a += da) {
            point=new Point(r * Math.cos(a),r * Math.sin(a));
            point.move(this.center.x,this.center.y);
            this.points.add(point);
        }               
    }
    @Override
    public Hexagon clone() {
        
        Hexagon copy=new Hexagon(this.center.clone(),this.width);               
        copy.points.clear();
        this.points.forEach(point->{
           copy.points.add(point.clone());
        });
        return copy;
    }
    
    public void scale(double alpha){
            this.center.scale(alpha);
            this.width*=alpha;  
            super.scale(alpha);                               
    }       
    public void setWidth(double width){
            this.width=width;
            this.reset();                     
    }
    public void move(int offsetX,int offsetY){
        this.center.move(offsetX,offsetY);
        super.move(offsetX, offsetY);  
    }
    
    public void rotate(double angle,Point center){
        this.center.rotate(angle,center);
        super.rotate(angle,center);
    } 
    
    public void rotate(double angle){
        this.center.rotate(angle,new Point(0,0));
        super.rotate(angle,new Point(0,0));
    } 
    
    
    
}
