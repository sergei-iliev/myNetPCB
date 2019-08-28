package com.mynetpcb.core.capi.shape;

import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Trackable;

import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class AbstractLine extends Shape implements Trackable<LinePoint>, Resizeable{
    protected Point floatingStartPoint; //***the last wire point

    protected Point floatingMidPoint; //***mid 90 degree forming

    protected Point floatingEndPoint;

    protected List<LinePoint> points;

    protected Point resizingPoint;    
    
    public AbstractLine(int thickness,int layermaskId) {
        super(0,0,0,0,thickness,layermaskId);
        this.points = new LinkedList<LinePoint>();
        this.floatingStartPoint = new Point();
        this.floatingMidPoint = new Point();
        this.floatingEndPoint = new Point();
        this.selectionRectWidth = 3000; 
    }
    @Override
    public void Clear() {
      points.clear();
    }
    
    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        getOwningUnit().getGrid().snapToGrid(targetPoint); 
    }
    
    @Override
    public List<LinePoint> getLinePoints() {    
        return this.points;
    }

    @Override
    public void addPoint(Point point) {
        points.add(new LinePoint(point));
    }

    @Override
    public void add(int x, int y) {
        points.add(new LinePoint(x,y));
    }
    @Override
    public Rectangle calculateShape(){
        int x1=Integer.MAX_VALUE,y1=Integer.MAX_VALUE,x2=Integer.MIN_VALUE,y2=Integer.MIN_VALUE;        
        
        for (Point point : points) {
            x1 = Math.min(x1, point.x);
            y1 = Math.min(y1, point.y);
            x2 = Math.max(x2, point.x);
            y2 = Math.max(y2, point.y);
        } 
        //add bending points
        return new Rectangle(x1, y1, (x2 - x1)==0?1:x2 - x1, y2 - y1==0?1:y2 - y1); 
    }
    @Override
    public void insertPoint(int x, int y) {
        int count=-1,index=-1;
        //build testing rect
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(selectionRectWidth/2), y-(selectionRectWidth/2),selectionRectWidth, selectionRectWidth);
        //inspect line by line
        FlyweightProvider lineProvider=ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line=(Line2D)lineProvider.getShape();
        
        //***make lines and iterate one by one
        Point prevPoint = this.points.get(0);
        Iterator<LinePoint> i = points.iterator();
        while (i.hasNext()) {
            count++;
            Point nextPoint = i.next();
            line.setLine(prevPoint, nextPoint);
            if (line.intersects(rect)){
                index=count;
                break;
            }    
            prevPoint = nextPoint;
        }
        
        lineProvider.reset();
        rectProvider.reset();
        if(count!=-1){
           points.add(index, new LinePoint(x,y)); 
        }
         
    }
    @Override
    public Point getEndPoint(int x, int y) {        
        if (points.size() ==0) {
            return null;
        }
        Point point=isBendingPointClicked(x, y);
        if(point==null){
            return null;
        }
        //***head point
        if (points.get(0).x==point.x&&points.get(0).y==point.y) {
            return points.get(0);
        }
        //***tail point
        if ((points.get(points.size() - 1)).x==point.x&& (points.get(points.size() - 1)).y==point.y) {
            return (points.get(points.size() - 1));
        }
        
        return null;
    }
    @Override
    public boolean isEndPoint(int x, int y) {
        if (points.size() < 2) {
            return false;
        }
        
        Point point=isBendingPointClicked(x, y);
        if(point==null){
            return false;
        }
        //***head point
        if (points.get(0).x==point.x&&points.get(0).y==point.y) {
            return true;
        }
        //***tail point
        if ((points.get(points.size() - 1)).x==point.x&& (points.get(points.size() - 1)).y==point.y) {
            return true;
        }
        return false;
    }
    @Override
    public boolean isInRect(Rectangle r) {
        for(Point wirePoint:points){
            if (!r.contains(wirePoint))
                return false;            
        }
        return true;
    }
    @Override
    public void Reverse(int x,int y) {
        Point p=isBendingPointClicked(x, y);
        if (points.get(0).x == p.x &&
            points.get(0).y == p.y) {
            Collections.reverse(points);
        }       
    }

    @Override
    public void removePoint(int x, int y) {
        Point point=isBendingPointClicked(x, y);
        if(point!=null){
          points.remove(point);
          point = null;
        }    
    }    
    @Override
    public boolean isClicked(int x, int y) {
        boolean result=false;
        //build testing rect
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(thickness/2), y-(thickness/2),thickness, thickness);
        //inspect line by line
        FlyweightProvider lineProvider=ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line=(Line2D)lineProvider.getShape();
        
        //***make lines and iterate one by one
        Point prevPoint = points.iterator().next();
        Iterator<LinePoint> i = points.iterator();
        while (i.hasNext()) {
            Point nextPoint = i.next();
            line.setLine(prevPoint, nextPoint);
            if (line.intersects(rect)){
                result= true;
                break;
            }    
            prevPoint = nextPoint;
        }
        
        lineProvider.reset();
        rectProvider.reset();        
        return result;
    }
    @Override
    public Point isBendingPointClicked(int x,int y){
        return isControlRectClicked(x, y);
    }

//    public Point isControlRectClicked(int x, int y) {                
//        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
//        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
//        rect.setFrame(x-(thickness/2), y-(thickness/2),thickness, thickness);
//        
//        Point point=null;
//        Point click=new Point(x,y);
//        int distance=Integer.MAX_VALUE;
//        
//        for (Point wirePoint : points) {
//            if(rect.contains(wirePoint)){ 
//                int min=(int)click.distance(wirePoint);
//                if(distance>min){
//                    distance=min;  
//                    point= wirePoint;                
//                }
//            }
//        }
//        
//        rectProvider.reset();
//        return point;
//    } 
    @Override
    public Point isControlRectClicked(int x, int y) {
            FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
            Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
            rect.setFrame(x-(selectionRectWidth/2), y-(selectionRectWidth/2),selectionRectWidth, selectionRectWidth);
            
            Point point=null;
            Point click=new Point(x,y);
            int distance=Integer.MAX_VALUE;
                    
            for (Point wirePoint : points) {
                if(rect.contains(wirePoint)){ 
                    int min=(int)click.distance(wirePoint);
                        if(distance>min){
                                distance=min;  
                                point= wirePoint;                
                            }
                        }
            }            
            
            rectProvider.reset();
            return point;
        }
    
    @Override
    public void Reset(Point point) {
        this.Reset(point.x, point.y);
    }

    @Override
    public void Reset() {
        this.Reset(floatingStartPoint);
    }

    @Override
    public void Reset(int x, int y) {
        Point p = isBendingPointClicked(x, y);
        floatingStartPoint.setLocation(p == null ? x : p.x, p == null ? y : p.y);
        floatingMidPoint.setLocation(p == null ? x : p.x, p == null ? y : p.y);
        floatingEndPoint.setLocation(p == null ? x : p.x, p == null ? y : p.y);
    }

    @Override
    public Point getFloatingStartPoint() {
        return floatingStartPoint;
    }

    @Override
    public Point getFloatingMidPoint() {
        return floatingMidPoint;
    }

    @Override
    public Point getFloatingEndPoint() {
        return floatingEndPoint;
    }
    @Override
    public Point getResizingPoint(){
        return resizingPoint;
    }
    
    @Override
    public void setResizingPoint(Point point) {
        this.resizingPoint=point;
    }
    @Override
    public void setSelected(boolean selection) {
        super.setSelected(selection);
        if(!selection){
            resizingPoint=null;
            for (LinePoint point : points) {
                point.setSelected(selection);
            }
        }
    }
    @Override
    public void shiftFloatingPoints() {
        floatingStartPoint.setLocation(points.get(points.size()-1).x, points.get(points.size()-1).y);
        floatingMidPoint.setLocation(floatingEndPoint.x, floatingEndPoint.y);       
    }
    @Override
    public void drawControlShape(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale){   
        Utilities.drawCrosshair(g2, viewportWindow, scale, points, resizingPoint, selectionRectWidth);
    }
    @Override
    public void deleteLastPoint(){
        if (points.size() == 0)
            return;

        points.remove(points.get(points.size() - 1));

        //***reset floating start point
        if (points.size() > 0)
            floatingStartPoint.setLocation(points.get(points.size() - 1));        
    }

    @Override
    public boolean isFloating(){
      return (!(floatingStartPoint.equals(floatingEndPoint) &&
              floatingStartPoint.equals(floatingMidPoint)));  
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        if((this.copper.getLayerMaskID()&layermask)==0){
            return;
        }
        GeneralPath line=null;
        line = new GeneralPath(GeneralPath.WIND_EVEN_ODD,points.size());      
        line.moveTo((float)points.get(0).getX(),(float)points.get(0).getY());
         for(int i=1;i<points.size();i++){            
             line.lineTo((float)points.get(i).getX(),(float)points.get(i).getY());       
         } 
        g2.setStroke(new BasicStroke(thickness,JoinType.JOIN_ROUND.ordinal(),EndType.CAP_ROUND.ordinal()));
        g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());
        
        g2.draw(line);
    }

    @Override
    public void Resize(int xoffset, int yoffset, Point clickedPoint) {
        clickedPoint.setLocation(clickedPoint.x+xoffset, clickedPoint.y+yoffset);
    }
    @Override
    public void Move(int xoffset, int yoffset) {
        for (Point wirePoint : points) {
            wirePoint.setLocation(wirePoint.x + xoffset, wirePoint.y + yoffset);
        }
    }
    @Override
    public void Rotate(AffineTransform rotation) {
        for(Point wirePoint:points){
            rotation.transform(wirePoint, wirePoint);
        }
    }    
    @Override
    public void Mirror(Point A,Point B) {
        for (Point wirePoint : points) {
            wirePoint.setLocation(Utilities.mirrorPoint(A,B, wirePoint));
        }
    }

    @Override
    public String getDisplayName(){
        return "Line";
    }
}
