package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;

import java.awt.geom.GeneralPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Polygon extends GeometricFigure{
    public List<Point> points=new LinkedList<>();
    public GeneralPath polygon=new GeneralPath();

    @Override
    public Polygon clone() {        
        Polygon copy=new Polygon();
        this.points.forEach(point->{
            copy.points.add(point.clone());
        });  
        return copy;
    }
    
    public Box box(){
      return new Box(this.points);       
    }
    
    public double area(){
        
          int l = points.size();
          long det = 0;
          List<Point> local=new ArrayList<>(points);
          local.add(points.get(0));

                    
          for (int i = 0; i < l; i++){
            det += local.get(i).x * local.get(i + 1).y
              - local.get(i).y * local.get(i + 1).x;
          }
          return Math.abs(det/ 2);                
    }
    
    public boolean contains(Point pt){    
       return this.contains(pt.x,pt.y);                     
    }
    
    public boolean contains(double xx,double yy){
      double x=xx;
      double y=yy;

          
      boolean inside = false;
      // use some raycasting to test hits
      // https://github.com/substack/point-in-polygon/blob/master/index.js
      
      //flat out points
      List<Double> p = new ArrayList<>();

      for (int i = 0, il = this.points.size(); i < il; i++)
      {
          p.add(this.points.get(i).x);
          p.add(this.points.get(i).y);
      }

      
        int length = (p.size() / 2);

        for (int i = 0, j = length - 1; i < length; j = i++)
        {
            double xi = p.get(i * 2);
            double yi = p.get((i * 2) + 1);
            double xj = p.get(j * 2);
            double yj = p.get((j * 2) + 1);
            
            boolean intersect = ((yi > y) != (yj > y)) && (x < ((xj - xi) * ((y - yi) / (yj - yi))) + xi);

            if (intersect)
            {
                inside = !inside;
            }
        }
      

      return inside;           
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
   
    public List<Point> vertices() {
        return this.points;
    } 
    @Override
    public boolean isPointOn(Point pt,double diviation){    	       
  	   var segment=new Segment(0,0,0,0);	   
       Point prevPoint = this.points.get(0);        
       for(Point point:this.points){    	        	  
            if(prevPoint.equals(point)){    	            	  
          	  prevPoint = point;
                continue;
            }    	              
            segment.set(prevPoint.x,prevPoint.y,point.x,point.y);
            if(segment.isPointOn(pt,diviation)){
                return true;
            }
            prevPoint = point;
        }		
        //close polygon	
        segment.set(prevPoint.x,prevPoint.y,this.points.get(0).x,this.points.get(0).y);
        if(segment.isPointOn(pt,diviation)){
            return true;
        }
        
        return false;
    } 	    
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        polygon.reset();
        
        polygon.moveTo(this.points.get(0).x,this.points.get(0).y);

        for (int i = 1; i < this.points.size(); i++) {
           polygon.lineTo(this.points.get(i).x, this.points.get(i).y);
        }
        polygon.closePath();
        
        if(fill){
            g2.fill(polygon);    
        }else{
            g2.draw(polygon);
        }
    }

    @Override
    public void rotate(double angle, Point center) {
        this.points.forEach(point->{
            point.rotate(angle,center);
        });

    }

    @Override
    public void rotate(double angle) {
        this.rotate(angle, new Point(0,0));
    }
}
