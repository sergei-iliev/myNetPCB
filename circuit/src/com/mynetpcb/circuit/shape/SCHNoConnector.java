package com.mynetpcb.circuit.shape;


import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class SCHNoConnector extends Shape implements Externalizable{
    
    public SCHNoConnector() {
        super(0,0,0,0,1,0);
        this.fillColor=Color.BLUE;
        this.setSelectionRectWidth(3);
    }
    
    @Override
    public SCHNoConnector clone() throws CloneNotSupportedException {
        SCHNoConnector copy = (SCHNoConnector)super.clone();
        return copy;
    }
    @Override
    public Rectangle calculateShape() {
        return new Rectangle(getX() - selectionRectWidth, getY() - selectionRectWidth, 2 * selectionRectWidth,
                             2 * selectionRectWidth);
    }
    @Override
    public void Move(int xoffset, int yoffset) {
        setX(getX() + xoffset);
        setY(getY() + yoffset);
    }

    @Override
    public void Mirror(Point A,Point B) {
        Point point = new Point(getX(), getY());
        Utilities.mirrorPoint(A,B, point);
        setX(point.x);
        setY(point.y);
    }

    @Override
    public void Translate(AffineTransform translate) {
        Point point = new Point(getX(), getY());
        translate.transform(point, point);
        setX(point.x);
        setY(point.y);
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        Point point = new Point(getX(), getY());
        rotation.transform(point, point);
        setX(point.x);
        setY(point.y);
    }
//    @Override
//    public boolean isClicked(int x, int y) {
//        Rectangle2D rect =calculateShape().getBounds();
//        if (rect.contains(x, y))
//            return true;
//        else
//            return false;
//    }
    
    @Override
    public long getOrderWeight() {
        return 3;
    }
//    @Override
//    public Point alignToGrid(boolean isRequired) {
//        Point point=getOwningUnit().getGrid().positionOnGrid(getX(),getY());     
//        setX(point.x);
//        setY(point.y);
//        return null;
//    }
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);

        if (!scaledRect.intersects(viewportWindow)) {
            return;
        }
        
        g2.setColor(isSelected() ? Color.GRAY : fillColor);
        g2.setStroke(new BasicStroke((float)(thickness*scale.getScaleX()))); 
        
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line = (Line2D)lineProvider.getShape();
        
        line.setLine(scaledRect.getMinX()-viewportWindow.x, scaledRect.getMinY()-viewportWindow.y,scaledRect.getMaxX()-viewportWindow.x, scaledRect.getMaxY()-viewportWindow.y);
        g2.draw(line);
        
        line.setLine(scaledRect.getMinX()-viewportWindow.x, scaledRect.getMaxY()-viewportWindow.y,scaledRect.getMaxX()-viewportWindow.x, scaledRect.getMinY()-viewportWindow.y);
        g2.draw(line);
        
        lineProvider.reset();    
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        Rectangle rect=getBoundingShape().getBounds();
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(thickness)); 
        
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line = (Line2D)lineProvider.getShape();
        
        line.setLine(rect.getMinX(),rect.getMinY(),rect.getMaxX(), rect.getMaxY());
        g2.draw(line);
        
        line.setLine(rect.getMinX(), rect.getMaxY(),rect.getMaxX(), rect.getMinY());
        g2.draw(line);
        
        lineProvider.reset();       
    }
    
    @Override
    public String getDisplayName() {
        return "NoConnection";
    }
    
    @Override
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<noconnector x=\""+getX()+"\"  y=\""+getY()+"\"/>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element=(Element)node;
        setX(Integer.parseInt(element.getAttribute("x")));
        setY(Integer.parseInt(element.getAttribute("y")));
    }
    
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }


    static class Memento extends AbstractMemento<Circuit,SCHNoConnector>{
        private int Ax;
        
        private int Ay;
        
        public Memento(MementoType mementoType){
           super(mementoType); 
        }
        
        public void loadStateTo(SCHNoConnector shape) {
            super.loadStateTo(shape);
            shape.setX(Ax);
            shape.setY(Ay);
        }
        

        public void saveStateFrom(SCHNoConnector shape){
            super.saveStateFrom(shape);
            Ax=shape.getX();
            Ay=shape.getY();
        }
        
        @Override
        public boolean equals(Object obj){
            if(this==obj){
              return true;  
            }
            if(!(obj instanceof Memento)){
              return false;  
            }
            
            Memento other=(Memento)obj;            
        
            return(getUUID().equals(other.getUUID())&&
                   getMementoType().equals(other.getMementoType())&&
                   Ax==other.Ax&&
                   Ay==other.Ay                
                );
                      
        }
        
        @Override
        public int hashCode(){
            int hash=getUUID().hashCode();
                hash+=this.getMementoType().hashCode();
                hash+=Ax+Ay;
            return hash;
        }        
        public boolean isSameState(Circuit unit) {
            SCHNoConnector junction=(SCHNoConnector)unit.getShape(getUUID());
            return( 
                  Ax==junction.getX()&&
                  Ay==junction.getY() 
                );
        }
    }
    
}
