package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractLine;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Node;


public class Line extends AbstractLine implements Externalizable {


    
    public Line(int thickness){
        super(thickness,Layer.LAYER_NONE);  
        this.fillColor=Color.BLACK;
        this.selectionRectWidth=4;
    }
    public Line(){
        this(1);
    }
    public Line clone()throws CloneNotSupportedException{
        Line copy=(Line)super.clone();
        copy.floatingStartPoint = new Point();
        copy.floatingMidPoint = new Point();
        copy.floatingEndPoint = new Point();
        copy.points = new LinkedList<LinePoint>();
        for (Point point : points) {
            copy.points.add(new LinePoint(point.x, point.y));
        }
        return copy;
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
         for (Point wirePoint : points) {
            Point point =
                getOwningUnit().getGrid().positionOnGrid(wirePoint.x, wirePoint.y);            
            wirePoint.setLocation(point);
         }
        }
        return null;
    }
//    @Override
//    public void alignResizingPointToGrid(Point targetPoint){
//        getOwningUnit().getGrid().snapToGrid(targetPoint);   
//    }
    
//    @Override
//    public List<Point> getLinePoints() {
//        return points;
//    }
//    @Override
//    public void insertPoint(int x, int y) {
//        if(this.points.size()==0){
//            return;
//        }
//        boolean flag = false;
//        Point point = getOwningUnit().getGrid().positionOnGrid(x, y);
//
//        Rectangle rect =
//            new Rectangle(x - getOwningUnit().getGrid().getGridPointToPoint() / 2,
//                          y - getOwningUnit().getGrid().getGridPointToPoint() / 2,
//                          getOwningUnit().getGrid().getGridPointToPoint(),
//                          getOwningUnit().getGrid().getGridPointToPoint());
//
//        Line2D line = new Line2D.Double();
//
//
//        Point tmp = new Point(point.x, point.y);
//        Point midium = new Point();
//
//        //***add point to the end;
//        addPoint(point);
//
//        Point prev = points.get(0);
//        for (Point next : points) {
//
//            if (!flag) {
//                //***find where the point is - 2 points between the new one
//                line.setLine(prev, next);
//                if (line.intersects(rect))
//                    flag = true;
//            } else {
//                midium.setLocation(tmp); //midium.setPin(tmp.getPin());
//                tmp.setLocation(prev); //tmp.setPin(prev.getPin());
//                prev.setLocation(midium); //prev.setPin(midium.getPin());
//            }
//            prev = next;
//        }
//        if (flag)
//            prev.setLocation(tmp); //prev.setPin(tmp.getPin());
//    }
//    @Override
//    public void deleteLastPoint(){
//        if (points.size() == 0)
//            return;
//
//        points.remove(points.get(points.size() - 1));
//
//        //***reset floating start point
//        if (points.size() > 0)
//            floatingStartPoint.setLocation(points.get(points.size() - 1));        
//    }
//    
//    @Override
//    public void addPoint(Point point) {
//      points.add(point);
//    }
    
//    @Override
//    public Point isBendingPointClicked(int x,int y){
//     return this.isControlRectClicked(x, y);   
//    }

//    public Point isControlRectClicked(int x, int y) {
//        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
//        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
//        rect.setFrame(x-(selectionRectWidth/2), y-(selectionRectWidth/2),selectionRectWidth, selectionRectWidth);
//        
//        Point point=null;
//        for (Point wirePoint : points) {
//            if(rect.contains(wirePoint)){
//              point= wirePoint;
//              break;
//            }
//        }
//        
//        rectProvider.reset();
//        return point;
//    }
    
//    @Override
//    public Point getResizingPoint(){
//        return resizingPoint;
//    }
//
//    @Override
//    public void setResizingPoint(Point point) {
//      this.resizingPoint=point;
//    } 
    
//    @Override
//    public void Resize(int xOffset, int yOffset, Point clickedPoint) {
//        clickedPoint.setLocation(clickedPoint.x+xOffset, clickedPoint.y+yOffset);
//    }
    
//    @Override
//    public Point getFloatingStartPoint() {
//        return floatingStartPoint;
//    }
//
//    @Override
//    public Point getFloatingMidPoint() {
//        return floatingMidPoint;
//    }
//
//    @Override
//    public Point getFloatingEndPoint() {
//        return floatingEndPoint;
//    }
//    
//    @Override
//    public void shiftFloatingPoints() {
//       
//    }
    
//    @Override
//    public void Reset(Point point) {
//        this.Reset(point.x,point.y);
//    }
//    
//    @Override
//    public void Reset() {
//        this.Reset(floatingStartPoint);
//    }
//    
//    @Override
//    public void Reset(int x, int y) {
//        Point p=isBendingPointClicked(x, y);
//        floatingStartPoint.setLocation(p==null?x:p.x,p==null?y:p.y);
//        floatingMidPoint.setLocation(p==null?x:p.x,p==null?y:p.y);
//        floatingEndPoint.setLocation(p==null?x:p.x,p==null?y:p.y);  
//    }
    
//    @Override
//    public boolean isFloating(){
//      return (!(floatingStartPoint.equals(floatingEndPoint) &&
//              floatingStartPoint.equals(floatingMidPoint)));  
//    }
    
//    public void add(int x,int y){
//      this.points.add(new Point(x,y));  
//    }
//    
//    @Override
//    public void setSelected(boolean selection) {
//        super.setSelected(selection);
//        if(!selection){
//            resizingPoint=null;
//        }
//    }
    
//    @Override
//    public void Clear() {
//      points.clear();
//    }


    @Override
    public long getOrderWeight() {
        return 2;
    }
    
//    @Override
//    public void Move(int xoffset, int yoffset) {
//        for(Point wirePoint:points){
//            wirePoint.setLocation(wirePoint.x + xoffset,
//                                  wirePoint.y + yoffset);            
//        } 
//    }
//
//    @Override
//    public void Mirror(Point A,Point B) {
//        for (Point wirePoint : points) {
//            wirePoint.setLocation(Utilities.mirrorPoint(A,B, wirePoint));
//        }
//    }

//    @Override
//    public void Translate(AffineTransform translate) {
//        for(Point wirePoint:points){
//            translate.transform(wirePoint, wirePoint);
//        }
//    }
//
//    @Override
//    public void Rotate(AffineTransform rotation) {
//        for(Point wirePoint:points){
//            rotation.transform(wirePoint, wirePoint);
//        }
//    }

//    @Override
//    public void setLocation(int x, int y) {
//    }

//    @Override
//    public boolean isInRect(Rectangle r) {
//        for(Point wirePoint:points){
//            if (!r.contains(wirePoint))
//                return false;            
//        }
//        return true;
//    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledBoundingRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);         
        if(!this.isFloating()&&!scaledBoundingRect.intersects(viewportWindow)){
          return;   
        }
        
        double lineThickness=thickness*scale.getScaleX();
               
        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
        GeneralPath temporal=(GeneralPath)provider.getShape(); 
        temporal.moveTo(points.get(0).getX(),points.get(0).getY());
        for(int i=1;i<points.size();i++){            
              temporal.lineTo(points.get(i).getX(),points.get(i).getY());       
        } 
        
        AffineTransform translate= AffineTransform.getTranslateInstance(-viewportWindow.x,-viewportWindow.y);
        
        temporal.transform(scale);
        temporal.transform(translate);

        g2.setStroke(new BasicStroke((float)lineThickness));    
        g2.setColor(isSelected()?Color.GRAY:fillColor);

        g2.draw(temporal); 
           
        if(this.isFloating()) {
            temporal.reset();
            temporal.moveTo(floatingStartPoint.getX(), floatingStartPoint.getY());
            temporal.lineTo(floatingMidPoint.getX(),floatingMidPoint.getY());
            temporal.lineTo(floatingEndPoint.getX(),floatingEndPoint.getY());
                        
            temporal.transform(scale);
            temporal.transform(translate);
            g2.draw(temporal);
        }
        
        provider.reset();
        
        if(this.isSelected()){
              this.drawControlShape(g2,viewportWindow,scale);
        } 
    }
    
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        GeneralPath line=null;
        line = new GeneralPath(GeneralPath.WIND_EVEN_ODD,points.size());      
        line.moveTo((float)points.get(0).getX(),(float)points.get(0).getY());
         for(int i=1;i<points.size();i++){            
             line.lineTo((float)points.get(i).getX(),(float)points.get(i).getY());       
         } 

        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(Color.BLACK);
        
        g2.draw(line);    
    }
    
    @Override
    public boolean isClicked(int x, int y) {
        boolean result=false;
        //build testing rect
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(selectionRectWidth/2), y-(selectionRectWidth/2),selectionRectWidth, selectionRectWidth);
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

//    @Override
//    public Rectangle calculateShape(){
//        int x1=Integer.MAX_VALUE,y1=Integer.MAX_VALUE,x2=Integer.MIN_VALUE,y2=Integer.MIN_VALUE;        
//        
//        for (Point point : points) {
//            x1 = Math.min(x1, point.x);
//            y1 = Math.min(y1, point.y);
//            x2 = Math.max(x2, point.x);
//            y2 = Math.max(y2, point.y);
//        } 
//        //add bending points
//        
//        return new Rectangle(x1, y1, (x2 - x1)==0?1:x2 - x1, y2 - y1==0?1:y2 - y1); 
//    }
//    @Override
//    public void Reverse(int x,int y) {
//        Point p=isBendingPointClicked(x, y);
//        if (points.get(0).x == p.x &&
//            points.get(0).y == p.y) {
//            Collections.reverse(points);
//        }       
//    }
//
//    @Override
//    public void removePoint(int x, int y) {
//        Point point=isBendingPointClicked(x, y);
//        if(point!=null){
//          points.remove(point);
//          point = null;
//        }
//    }
//
//    @Override
//    public boolean isEndPoint(int x, int y) {
//        if (points.size() < 2) {
//            return false;
//        }
//        
//        Point point=isBendingPointClicked(x, y);
//        if(point==null){
//            return false;
//        }
//        //***head point
//        if (points.get(0).x==point.x&&points.get(0).y==point.y) {
//            return true;
//        }
//        //***tail point
//        if ((points.get(points.size() - 1)).x==point.x&& (points.get(points.size() - 1)).y==point.y) {
//            return true;
//        }
//        return false;
//    }    


   
//    @Override
//    public String getDisplayName(){
//        return "Line";
//    }
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<line>");
        for(Point point:points){
            sb.append(point.x+","+point.y+","); 
        }        
        sb.append(this.getThickness());
        sb.append("</line>\r\n");
        return sb.toString();
    }

    public void fromXML(Node node) {
        StringTokenizer st = new StringTokenizer(node.getTextContent(), ",");
        int counter=st.countTokens()-1;
        while(st.hasMoreTokens()){
          this.add(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));  
          counter-=2;
          if(counter==0)
              break;
        }   
        setThickness(Integer.parseInt(st.nextToken()));
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }


    public static class Memento extends AbstractMemento<Symbol, Line> {

        private int Ax[];

        private int Ay[];
        
        private int endType;
        
        private int joinType;
              
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }
        
        @Override
        public void loadStateTo(Line shape) {
            super.loadStateTo(shape);
            shape.points.clear();
            //shape.endType = Line.EndType.values()[endType];
            //shape.joinType = Line.JoinType.values()[joinType];
            for (int i = 0; i < Ax.length; i++) {
                shape.add(Ax[i], Ay[i]); 
            }
            //***reset floating start point
            if (shape.points.size() > 0) {
                shape.floatingStartPoint.setLocation(shape.points.get(shape.points.size() -
                                                                 1));
                shape.Reset();
            }
        }
        @Override
        public void saveStateFrom(Line shape) {
            super.saveStateFrom(shape);
            //endType=symbol.endType.ordinal();
            //joinType=symbol.joinType.ordinal();
            
            Ax = new int[shape.points.size()];
            Ay = new int[shape.points.size()];
            for (int i = 0; i < shape.points.size(); i++) {
                Ax[i] = shape.points.get(i).x;
                Ay[i] = shape.points.get(i).y;
            }
        }

        @Override
        public void Clear() {
            super.Clear();
            Ax = null;
            Ay = null;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }
            Memento other = (Memento)obj;
            return (getUUID().equals(other.getUUID()) &&
                    getMementoType()==other.getMementoType() &&
                    thickness==other.thickness&&
                    joinType == other.joinType&&endType==other.endType && Arrays.equals(Ax, other.Ax) &&
                    Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash += this.getMementoType().hashCode();
            hash += endType+joinType+thickness;
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }

        public boolean isSameState(Symbol unit) {
            Line line = (Line)unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }

}
