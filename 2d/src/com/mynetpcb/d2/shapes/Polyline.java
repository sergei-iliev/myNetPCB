package com.mynetpcb.d2.shapes;


import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Polyline<P extends Point> extends GeometricFigure{
    public List<P> points=new LinkedList<>();
    public GeneralPath polyline=new GeneralPath();    
    public Polyline() {
        super();
    }

    @Override
    public Polyline clone() {        
        Polyline copy=new Polyline();
        
        this.points.forEach(point->{
            copy.points.add(point.clone());
        });  
        return copy;
    }
    public void remove(double x,double y){
        Point item=new Point(x,y);
        points.removeIf(p->p.equals(item));       
    }
    public void add(P point){
       this.points.add(point);              
    }
    public Box box(){
      return new Box((Collection<Point>)this.points);        
    }
    public boolean intersect(GeometricFigure shape) {
      Segment segment=new Segment();
      if (shape instanceof Circle) {
          Point prevPoint = this.points.get(0);        
          for(Point point:this.points){
              if(prevPoint.equals(point)){
                  prevPoint = point;
                  continue;
              }
              
              segment.set(prevPoint,point);
              if(segment.intersect((Circle)shape)){
                  return true;
              }
              prevPoint = point;
          }
          
      }
      if(shape instanceof Box){
        Point prevPoint = this.points.get(0);        
        for(Point point:this.points){
            if(prevPoint.equals(point)){
                prevPoint = point;
                continue;
            }
            
            segment.set(prevPoint,point);
            
            if(segment.intersect((Box)shape)){
                return true;
            }
            prevPoint = point;
        }
      }
      
      
      return false;
    }
    public void move(double offsetX,double offsetY){
        this.points.forEach(point->{
             point.move(offsetX,offsetY);
        });  
    }
    public void mirror(Line line){
     this.points.forEach(point->{
             point.mirror(line);
        });          
    }
    public void scale(double alpha){
        this.points.forEach(point->{
             point.scale(alpha);
        });          
    }
    public void rotate(double angle,Point center){
        this.points.forEach(point->{
             point.rotate(angle,center);
        });
    }    
    
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        polyline.reset();
        
        polyline.moveTo(this.points.get(0).x,this.points.get(0).y);

        for (int i = 1; i < this.points.size(); i++) {
           polyline.lineTo(this.points.get(i).x, this.points.get(i).y);
        }
      
        g2.draw(polyline);
        
    }

    @Override
    public void rotate(double angle) {
        this.rotate(angle,new Point());
    }
}
