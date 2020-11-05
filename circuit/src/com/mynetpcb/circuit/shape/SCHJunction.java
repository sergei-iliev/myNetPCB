package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class SCHJunction extends Shape implements Externalizable{
    private Circle circle;

   public SCHJunction(){
	  super(1,Layer.LAYER_ALL);	
	  this.displayName = "Junction";	
	  this.selectionRectWidth=2;	
          this.fillColor=Color.BLACK;
	  this.circle=new Circle(new Point(0,0),this.selectionRectWidth);	
    }
   @Override
    public SCHJunction clone() throws CloneNotSupportedException {
        SCHJunction copy=(SCHJunction)super.clone();        
        copy.circle=this.circle.clone();
       return copy;
    }
    @Override
    public Point alignToGrid(boolean isRequired) {        
            Point point=this.getOwningUnit().getGrid().positionOnGrid(this.circle.pc.x, this.circle.pc.y);
            this.circle.pc.set(point.x,point.y);    
            return null;                
    }
    @Override
    public Box getBoundingShape() {
            return this.circle.box();    
    }
    @Override
    public void move(double xoff, double yoff) {        
        this.circle.move(xoff,yoff);
    }
    
    @Override
    public void rotate(double angle, Point origin) {
        this.circle.rotate(angle,origin);        
    }
    
    @Override
    public void mirror(Line line) {
        this.circle.mirror(line);        
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
        Box rect = this.circle.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }             
        g2.setColor(isSelected()?Color.BLUE:fillColor);

        Circle c=this.circle.clone();
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX() ,- viewportWindow.getY());        
        c.paint(g2,true);        
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        StringTokenizer st = new StringTokenizer(node.getTextContent(), ",");
        circle.pc.set(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));        
    }

    public static class Memento extends AbstractMemento<Circuit, SCHJunction> {

        private double x;
        private double y;
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(SCHJunction shape) {
            super.loadStateTo(shape);
            shape.circle.pc.set(x, y);
        }

        @Override
        public void saveStateFrom(SCHJunction shape) {
            super.saveStateFrom(shape);
            x=shape.circle.pc.x;
            y=shape.circle.pc.y;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }
            Memento other = (Memento) obj;
            return (super.equals(obj)&&
                    Utils.EQ(x, other.x) && Utils.EQ(y, other.y));
        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode();
            hash += Double.hashCode(x);
            hash += Double.hashCode(y);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            SCHJunction line = (SCHJunction) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }   
}
