package com.mynetpcb.core.capi.shape;

import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.d2.shapes.Utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class AbstractLine extends Shape implements Trackable<LinePoint>, Resizeable{
    protected Point floatingStartPoint; //***the last wire point

    protected Point floatingMidPoint; //***mid 90 degree forming

    protected Point floatingEndPoint;

    protected Point resizingPoint;    
    
    protected Polyline<LinePoint> polyline;
    
    protected double rotate;
    
    public AbstractLine(int thickness,int layermaskId) {
        super(thickness,layermaskId);
        this.floatingStartPoint = new Point(0,0);
        this.floatingMidPoint = new Point(0,0);
        this.floatingEndPoint = new Point(0,0);
        this.selectionRectWidth = 3000;                 
        this.displayName="Line";                   
        this.polyline=new Polyline<LinePoint>();
        this.rotate=0;                
    }
    
    
    @Override
    public void clear() {
      polyline.points.clear();
      this.rotate=0;
    }
    
    @Override
    public Box getBoundingShape(){
        return this.polyline.box();
    }
    
    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        getOwningUnit().getGrid().snapToGrid(targetPoint); 
    }
    
    @Override
    public List<LinePoint> getLinePoints() {    
        return this.polyline.points;
    }

    @Override
    public void add(Point point) {
        polyline.points.add(new LinePoint(point));
    }

    @Override
    public void add(double x, double y) {
         polyline.points.add(new LinePoint(x,y));
    }
    @Override
    public boolean isClicked(int x,int y) {
                
                Point pt=new Point(x,y);

                LinePoint prevPoint = this.polyline.points.get(0);
                Line line=new Line(new Point(), new Point());
                for(LinePoint point:this.polyline.points){
                    if(prevPoint.equals(point)){
                        prevPoint = point;
                        continue;
                    }

                    line.setLine(prevPoint, point);
                    Point projectionPoint = line.projectionPoint(pt);

                    if(projectionPoint.distanceTo(pt)>this.thickness){
                        prevPoint = point;
                        continue;
                    }
                    
                    double a = (projectionPoint.x - prevPoint.x) / ((point.x - prevPoint.x) == 0 ? 1 : point.x - prevPoint.x);
                    double b = (projectionPoint.y - prevPoint.y) / ((point.y - prevPoint.y) == 0 ? 1 : point.y - prevPoint.y);

                    if (0 <= a && a <= 1 && 0 <= b && b <= 1) { //is projection between start and end point                                                    
                            return true;
                    }
                    prevPoint = point;
                }
                
            return false;
    }    
    @Override
    public void insertPoint(double x, double y) {
        
       Box rect = Box.fromRect(x
                                                           - (this.thickness / 2), y
                                                           - (this.thickness / 2), this.thickness,
                                                           this.thickness);

       
       
        int count=-1,index=-1;
        
        
        //***make lines and iterate one by one
        LinePoint prevPoint =this.polyline.points.get(0);
        Iterator<LinePoint> i = this.polyline.points.iterator();
        while (i.hasNext()) {
            count++;
            LinePoint point = i.next();
            
            if (Utils.intersectLineLine(prevPoint, point, rect.min, rect.max)){
                index=count;
                break;
            }    
            prevPoint = point;
        }
        
        if(count!=-1){
           this.polyline.points.add(index, new LinePoint(x,y)); 
        }
         
   }

    @Override
    public boolean isEndPoint(double x, double y) {
        if (polyline.points.size() < 2) {
            return false;
        }
        
        Point point=isBendingPointClicked(x, y);
        if(point==null){
            return false;
        }
        //***head point
        if (polyline.points.get(0).x==point.x&&polyline.points.get(0).y==point.y) {
            return true;
        }
        //***tail point
        if ((polyline.points.get(polyline.points.size() - 1)).x==point.x&& (polyline.points.get(polyline.points.size() - 1)).y==point.y) {
            return true;
        }
        return false;
    }
//    @Override
//    public boolean isInRect(Rectangle r) {
//        for(Point wirePoint:points){
//            if (!r.contains(wirePoint))
//                return false;            
//        }
//        return true;
//    }
    @Override
    public void reverse(double x,double y) {
        Point p=isBendingPointClicked(x, y);
        if (Utils.EQ(polyline.points.get(0).x,p.x) &&
            Utils.EQ(polyline.points.get(0).y,p.y)) {
            Collections.reverse(polyline.points);
        }       
    }

    @Override
    public void removePoint(double x, double y) {
        Point point=isBendingPointClicked(x, y);
        if(point!=null){
          this.polyline.points.remove(point);
          point = null;
        }    
    }    
    @Override
    public Point isBendingPointClicked(double x,double y){
        Box rect = Box.fromRect(x
                        - this.selectionRectWidth / 2, y - this.selectionRectWidth
                        / 2, this.selectionRectWidth, this.selectionRectWidth);

        
        Optional<LinePoint> opt= this.polyline.points.stream().filter(( wirePoint)->rect.contains(wirePoint)).findFirst();                  
                  
        
        return opt.orElse(null);
    }

    @Override
    public Point isControlRectClicked(int x, int y) {
        return this.isBendingPointClicked(x, y);
    }
    @Override
    public void move(double xoffset,double yoffset) {
        this.polyline.move(xoffset,yoffset);
    }
    @Override
    public void mirror(Line line) {        
        this.polyline.mirror(line);
    }
    @Override
    public void rotate(double angle, Point origin) {        
        this.polyline.rotate(angle, origin);
    }
    @Override
    public void reset(Point point) {
        this.reset(point.x, point.y);
    }

    @Override
    public void reset() {
        this.reset(floatingStartPoint);
    }

    @Override
    public void reset(double x, double y) {
        Point p = isBendingPointClicked(x, y);
        floatingStartPoint.set(p == null ? x : p.x, p == null ? y : p.y);
        floatingMidPoint.set(p == null ? x : p.x, p == null ? y : p.y);
        floatingEndPoint.set(p == null ? x : p.x, p == null ? y : p.y);
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
            for (LinePoint point : polyline.points) {
                point.setSelected(selection);
            }
        }
    }
    @Override
    public void shiftFloatingPoints() {
        this.floatingStartPoint.set((Point)this.polyline.points.get(this.polyline.points.size()-1));
        this.floatingMidPoint.set(this.floatingEndPoint.x, this.floatingEndPoint.y);      
    }

    @Override
    public void deleteLastPoint(){
        if (polyline.points.size() == 0)
            return;

        polyline.points.remove(polyline.points.get(polyline.points.size() - 1));

        //***reset floating start point
        if (polyline.points.size() > 0)
            floatingStartPoint.set(polyline.points.get(polyline.points.size() - 1));        
    }

    @Override
    public boolean isFloating(){
      return (!(floatingStartPoint.equals(floatingEndPoint) &&
              floatingStartPoint.equals(floatingMidPoint)));  
    }
//
//    @Override
//    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
//        if((this.copper.getLayerMaskID()&layermask)==0){
//            return;
//        }
//        GeneralPath line=null;
//        line = new GeneralPath(GeneralPath.WIND_EVEN_ODD,points.size());      
//        line.moveTo((float)points.get(0).getX(),(float)points.get(0).getY());
//         for(int i=1;i<points.size();i++){            
//             line.lineTo((float)points.get(i).getX(),(float)points.get(i).getY());       
//         } 
//        g2.setStroke(new BasicStroke(thickness,JoinType.JOIN_ROUND.ordinal(),EndType.CAP_ROUND.ordinal()));
//        g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());
//        
//        g2.draw(line);
//    }
//
    @Override
    public void resize(int xoffset, int yoffset, Point clickedPoint) {
        clickedPoint.set(clickedPoint.x+xoffset, clickedPoint.y+yoffset);
    }


}
