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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;


public class SCHJunction extends Shape implements Externalizable {


    public SCHJunction() {
        super(0, 0, 0, 0, 1,0);
        this.fillColor=Color.BLUE;
        this.setSelectionRectWidth(3);
    }

    @Override
    public SCHJunction clone() throws CloneNotSupportedException {
        SCHJunction copy = (SCHJunction)super.clone();
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

    @Override
    public long getOrderWeight() {
        return 1;
    }
    
    @Override
    public int getDrawingOrder() {        
        return 101;
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);

        if (!scaledRect.intersects(viewportWindow)) {
            return;
        }
        g2.setColor(isSelected() ? Color.GRAY : fillColor);

        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        ellipse.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                         scaledRect.getWidth(), scaledRect.getHeight());
        g2.fill(ellipse);
        ellipseProvider.reset();
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
              
        g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:fillColor);

        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        ellipse.setFrame(getX() - selectionRectWidth, getY() - selectionRectWidth, 2 * selectionRectWidth,
                                     2 * selectionRectWidth);        
        g2.fill(ellipse);
        ellipseProvider.reset();
    }

    @Override
    public String getDisplayName() {
        return "Junction";
    }

    @Override
    public String toXML() {
        StringBuffer xml = new StringBuffer();
        xml.append("<junction>");
        xml.append(getX() + "," + getY());
        xml.append("</junction>\r\n");
        return xml.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        StringTokenizer st = new StringTokenizer(node.getTextContent(), ",");
        setX(Integer.parseInt(st.nextToken()));
        setY(Integer.parseInt(st.nextToken()));
    }
    
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }

    static class Memento extends AbstractMemento<Circuit,SCHJunction>{
        private int Ax;
        
        private int Ay;
        
        public Memento(MementoType mementoType){
           super(mementoType); 
        }
        
        public void loadStateTo(SCHJunction shape) {
            super.loadStateTo(shape);
            shape.setX(Ax);
            shape.setY(Ay);
        }
        

        public void saveStateFrom(SCHJunction shape){
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
            SCHJunction junction=(SCHJunction)unit.getShape(getUUID());
            return( 
                  Ax==junction.getX()&&
                  Ay==junction.getY() 
                );
        }
    }
}
