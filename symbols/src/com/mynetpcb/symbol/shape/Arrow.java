package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.w3c.dom.Node;


public class Arrow extends Shape implements Resizeable,Externalizable {

    //***arrow points
    private Point A1, A2, A3;

    private Point left,right;
    
    private Point resizingPoint;
    
    private int headSize;

    private Polygon arrowHead ;
    
    public Arrow( int Ax, int Ay, int Bx, 
                       int By) {
        super(0,0,0,0,1,0);
        this.setFillColor(Color.BLACK);
        left = new Point(Ax, Ay);
        right = new Point(Bx, By);
        arrowHead = new Polygon();
        A1 = new Point();
        A2 = new Point();
        A3 = new Point();
        setHeadSize(3);
    }
    public Arrow(){
        this(0,0,0,0);
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
          Point point=getOwningUnit().getGrid().positionOnGrid(left.x, left.y);
          this.Move(point.x-left.x, point.y-left.y);           
        }
        return null;
    }
    
    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        getOwningUnit().getGrid().snapToGrid(targetPoint);   
    }
    public String getHeadSize() {
        return String.valueOf(headSize);
    }

    public void setHeadSize(int headSize) {
        this.headSize = headSize;
        A1.setLocation(0, 0);
        A2.setLocation(headSize, -2 * headSize);
        A3.setLocation(-headSize, -2 * headSize);
    }

    /*
     * used for dressing fromXML
     */

    public Arrow clone()throws CloneNotSupportedException{
        Arrow copy=(Arrow)super.clone();
        copy.arrowHead = new Polygon();
        copy.A1 = new Point();
        copy.A2 = new Point();
        copy.A3 = new Point();
        copy.setHeadSize(this.headSize);
        copy.left=new Point(this.left.x,this.left.y);
        copy.right=new Point(this.right.x,this.right.y);
        return copy;
    }
//    public Arrow(Unit owningUnit) {
//        this(owningUnit, 0, 0, 0, 0);
//    }
    //***Ordering waight

    @Override
    public void Resize(int xOffset, int yOffset, 
                       Point clickedPoint) {
        if(clickedPoint!=null)
        clickedPoint.setLocation(clickedPoint.x + xOffset, 
                                 clickedPoint.y + yOffset);
    }

    public void Move(int xOffset, int yOffset) {
        left.setLocation(left.x + xOffset, 
                              left.y + yOffset);
        right.setLocation(right.x + xOffset, 
                               right.y + yOffset);
    }
    
    @Override
    public void Mirror(Point A,Point B) {
        left.setLocation(Utilities.mirrorPoint(A,B, left));
        right.setLocation(Utilities.mirrorPoint(A,B, right));
    }


    public void Translate(AffineTransform translate) {
        translate.transform(left, left);
        translate.transform(right, right);
    }

    public void Rotate(AffineTransform rotation) {                      
                  rotation.transform(left,left);
                  rotation.transform(right,right);
    }

    public void setLocation(int x, int y) {
        throw new IllegalAccessError("Doubt any one would call it.");
//        Rectangle r=this.getBoundingRect();
//        Point point=owningUnit.getGrid().positionOnGrid(x,y); 
//        int x_offset=point.x-r.x;
//        int y_offset=point.y-r.y;
//        this.Move((int)x_offset,(int)y_offset);         
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledBoundingRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);  
        if(!scaledBoundingRect.intersects(viewportWindow)){
          return;   
        }   
        
        //Flyweight
        FlyweightProvider lineProvider=ShapeFlyweightFactory.getProvider(Line2D.class);        
        
        Line2D.Double line = (Line2D.Double)lineProvider.getShape();
        Point2D A=new Point2D.Double();
        Point2D B=new Point2D.Double();
        scale.transform(left, A);
        scale.transform(right, B);
        line.setLine(A.getX()-viewportWindow.x, A.getY()-viewportWindow.y, B.getX()-viewportWindow.x, B.getY()-viewportWindow.y);
        
        double lineThickness=thickness*scale.getScaleX();
        g2.setStroke(new BasicStroke((float)lineThickness,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER));  
       
       
        g2.setColor(isSelected()?Color.GRAY:fillColor);
        g2.draw(line);
        
        
        
        Point2D a1 = new Point2D.Double();
        Point2D a2 = new Point2D.Double();
        Point2D a3 = new Point2D.Double();

        scale.transform(A1, a1);
        scale.transform(A2, a2);
        scale.transform(A3, a3);
        arrowHead.reset(); 
        arrowHead.addPoint((int)a1.getX(), (int)a1.getY());
        arrowHead.addPoint((int)a2.getX(), (int)a2.getY());
        arrowHead.addPoint((int)a3.getX(), (int)a3.getY());
        
        AffineTransform oldtransform = g2.getTransform();
        double angle = Math.atan2((line.y2) - (line.y1), (line.x2 - line.x1));
        g2.translate(line.x2, line.y2);
        g2.rotate((angle - Math.PI / 2));
        
      
        if (fill==Fill.FILLED) {
            g2.setPaint(Color.BLACK);
            g2.fill(arrowHead);
        }else { 
            g2.setPaint(Color.WHITE);
            g2.fill(arrowHead);
        }
        g2.setColor(isSelected()?Color.GRAY:fillColor);
        g2.draw(arrowHead);
        g2.setTransform(oldtransform);
        
        lineProvider.reset();
        
        if(this.isSelected()){
              this.drawControlShape(g2,viewportWindow,scale);
        }
        
        
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        //Flyweight
        FlyweightProvider lineProvider=ShapeFlyweightFactory.getProvider(Line2D.class);        
        
        Line2D.Double line = (Line2D.Double)lineProvider.getShape();
        line.setLine(left.getX(), left.getY(), right.getX(), right.getY());
        

        g2.setStroke(new BasicStroke(thickness,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER));  
        
        g2.setColor(Color.BLACK);
        g2.draw(line);
        
        
        
        arrowHead.reset(); 
        arrowHead.addPoint(A1.x, A1.y);
        arrowHead.addPoint(A2.x, A2.y);
        arrowHead.addPoint(A3.x, A3.y);
        
        AffineTransform oldtransform = g2.getTransform();
        double angle = Math.atan2((line.y2) - (line.y1), (line.x2 - line.x1));
        g2.translate(line.x2, line.y2);
        g2.rotate((angle - Math.PI / 2));
        
        
        if (fill==Fill.FILLED) {
            g2.setPaint(Color.BLACK);
            g2.fill(arrowHead);
        }else { 
            g2.setPaint(Color.WHITE);
            g2.fill(arrowHead);
        }
        g2.setColor(Color.BLACK);
        g2.draw(arrowHead);
        g2.setTransform(oldtransform);
        
        lineProvider.reset();
        
    }

    public boolean isInRect(Rectangle r) {
        if (r.contains(left) && r.contains(right))
            return true;
        else
            return false;
    }
    
    @Override
    public boolean isClicked(int x, int y) {
        boolean result=false;
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        
        rect.setRect(x - selectionRectWidth / 2, y - selectionRectWidth / 2, selectionRectWidth, selectionRectWidth);
            //****construct testing rectangle
        Line2D l = new Line2D.Float();
        l.setLine(left, right);
        if (l.intersects(rect))
            result= true;
        else
            result= false;
        
       rectProvider.reset();
       
       return result;
    }

    @Override
    public Rectangle calculateShape(){
        Rectangle r = new Rectangle();
        double x = Math.min(left.x, right.x);
        double y = Math.min(left.y, right.y);
        double w = Math.abs(left.x - right.x);
        double h = Math.abs(left.y - right.y);
        r.setRect(x, y, w, h);
        Utilities.IncrementRect(r, 2, 2);
        return r;
    }
    
    @Override
    public String getDisplayName(){
        return "Arrow";
    }

    @Override
    public long getOrderWeight() {
        return 1L;
    }
    public String toXML() {
        return "<arrow>" + left.x + "," + left.y + "," + right.x + "," + right.y + "," +
            this.getThickness() + "," + headSize + ","+getFill().index+"</arrow>\r\n";
    }
    
    public void fromXML(Node node) { 
        StringTokenizer st = new StringTokenizer(node.getTextContent(), ",");
        left.setLocation(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
        right.setLocation(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
        setThickness(Byte.parseByte(st.nextToken()));
        setHeadSize(Integer.parseInt(st.nextToken()));
        try{
            setFill(Fill.byIndex(Byte.parseByte(st.nextToken()))); 
        }catch(NoSuchElementException e){
           //previous version older then 1.2 does not have fillness 
        }
    }
    
    @Override
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        Utilities.drawCrosshair(g2, viewportWindow, scale,resizingPoint , selectionRectWidth, left,right);        
    }

    @Override
    public Point isControlRectClicked(int x, int y) {
        FlyweightProvider rectFlyweightProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D)rectFlyweightProvider.getShape();

        Point p = new Point(x, y);
        try {
            rect.setRect(left.x - selectionRectWidth / 2, left.y - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(p)) {
                return left;
            }
            rect.setRect(right.x - selectionRectWidth / 2, right.y - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(p))
                return right;

        } finally {
            rectFlyweightProvider.reset();
        }
        return null;
    }

    @Override
    public Point getResizingPoint() {
        return resizingPoint;
    }

    @Override
    public void setResizingPoint(Point point) {
        this.resizingPoint = point;
    }
    @Override
    public void setSelected(boolean selection) {
        super.setSelected(selection);
        if (!selection) {
            resizingPoint = null;
        }
    }
    
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        ((Memento)memento).loadStateTo(this);
    }

    public static class Memento extends AbstractMemento<Symbol,Arrow> {
        private int headSize;
        
        private int x1,y1,x2,y2;
        
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        public void saveStateFrom(Arrow shape) {
            super.saveStateFrom(shape);
            this.headSize = shape.headSize;
            this.x1=shape.left.x;
            this.y1=shape.left.y;
            this.x2=shape.right.x;
            this.y2=shape.right.y;
        }

        public void loadStateTo(Arrow shape) {
            super.loadStateTo(shape);
            shape.headSize = headSize;
            shape.left.setLocation(x1, y1);
            shape.right.setLocation(x2,y2);
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
            return  getMementoType()==other.getMementoType()&&
                    getUUID().equals(other.getUUID()) &&
                    this.headSize == other.headSize&& this.fill == other.fill&&
                    this.x1==other.x1&&this.y1==other.y1&&
                    this.x2==other.x2&&this.y2==other.y2;
        }

        @Override
        public int hashCode() {
            return  getMementoType().hashCode()+ getUUID().hashCode()+ x1+x2+y1+y2+ headSize+fill;
        }

        @Override
        public String toString() {
            return "Arrow";
        }

        public boolean isSameState(Symbol unit) {
            Arrow other = (Arrow)unit.getShape(this.getUUID());
            return  this.headSize == other.headSize&& this.fill == other.getFill().ordinal()&&
                    this.x1==other.left.x&&this.y1==other.left.y&&
                    this.x2==other.right.x&&this.y2==other.right.y;
        }
    }
}

