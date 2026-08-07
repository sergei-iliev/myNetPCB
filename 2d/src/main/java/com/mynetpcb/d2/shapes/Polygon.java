package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;

import java.awt.geom.GeneralPath;

import java.util.ArrayList;
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
    @Override
    public void assign(GeometricFigure drawing) {
        Polygon src = (Polygon) drawing;
        if (this.points.size() == src.points.size()) {
            for (int i = 0; i < src.points.size(); i++) {
                this.points.get(i).set(src.points.get(i));
            }
        } else {
            this.points.clear();
            src.points.forEach(point -> this.points.add(point.clone()));
        }
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
        int n = points.size();
        if (n < 3) {
            return false;
        }

        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = points.get(i).x;
            double yi = points.get(i).y;
            double xj = points.get(j).x;
            double yj = points.get(j).y;

            if (((yi > yy) != (yj > yy))
                    && (xx < ((xj - xi) * ((yy - yi) / (yj - yi))) + xi)) {
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
