package com.mynetpcb.board.shape;

import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.List;

/*
 * Bind points to polygon
 */
public class Polygonal extends Polygon implements Cloneable{
    
    private  List<LinePoint> points;
    
    public Polygonal() {
        this.points = new ArrayList();
    }
    
    @Override
    public Polygonal clone() throws CloneNotSupportedException {
         Polygonal copy=new Polygonal();
         for(Point point:points){
            copy.addPoint(new Point(point.x,point.y));
         }         
         return copy;
    }
    public void alignPoint(Point oldPoint,Point newPoint) {        
        for(int i=0;i<this.npoints;i++){
            if((this.xpoints[i]==oldPoint.x)&&(this.ypoints[i]==oldPoint.y)){
                this.xpoints[i]=newPoint.x;
                this.ypoints[i]=newPoint.y;                  
                this.invalidate();
            }
        }  
        
    }
    @Override
    public void reset() {        
        super.reset();
        points.clear();
    }
    
    public void Resize(int xoffset, int yoffset, Point clickedPoint) {
        for(int i=0;i<this.npoints;i++){
            if((this.xpoints[i]==clickedPoint.x)&&(this.ypoints[i]==clickedPoint.y)){
                this.xpoints[i]+=xoffset;
                this.ypoints[i]+=yoffset;
                clickedPoint.setLocation(clickedPoint.x+xoffset, clickedPoint.y+yoffset);             
                this.invalidate();
            }
        }
    }
    
    public void Rotate(AffineTransform rotation) {
        int i=0;
        for(Point wirePoint:points){
            rotation.transform(wirePoint, wirePoint);
            this.xpoints[i]=wirePoint.x;
            this.ypoints[i++]=wirePoint.y;
        }
        this.invalidate();
    }
    
    
    public void Mirror(Point A,Point B) {
        int i=0;
        for (Point wirePoint : points) {
            wirePoint.setLocation(Utilities.mirrorPoint(A,B, wirePoint));
            this.xpoints[i]=wirePoint.x;
            this.ypoints[i++]=wirePoint.y;
        }
        this.invalidate();
    }
    
    public void Move(int xoffset,int yoffset){
        for(Point point:points){
            point.setLocation(point.x + xoffset,
                                  point.y + yoffset);            
        } 
        this.translate(xoffset, yoffset);
    }
    
    
    public List<LinePoint> getLinePoints() {
       return this.points;
    }    
    
    
    public void addPoint(Point point) {
        points.add(new LinePoint(point));
        super.addPoint(point.x, point.y);
    }

    public void addPoint(int index,Point point) {
        points.add(index,new LinePoint(point));
        super.reset();
        for(LinePoint p:points){
           super.addPoint(p.x, p.y);  
        }        
    }
    
    public void removePoint(Point point){
        if(points.remove(point)){
           super.reset();    
           for(LinePoint p:points){
              super.addPoint(p.x, p.y);  
           }
        }
    }
    
    @Override
    public void addPoint(int x, int y) {
        throw new UnsupportedOperationException("Never use add");
    }
}
