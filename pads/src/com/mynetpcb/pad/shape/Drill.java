package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Drill extends Shape implements Externalizable{
    private Circle circle;    
    public Drill(double x,double y,double width){
        super(0,0);
        this.circle = new Circle(new Point(x, y),width/2);
        fillColor=Color.BLACK;
    }
    
    @Override
    public Drill clone()throws CloneNotSupportedException{
        Drill copy= (Drill)super.clone();
        copy.circle=this.circle.clone();        
        return copy;
    }
    public double getWidth(){
        return 2*this.circle.r;
    }
    
    public void setWidth(double width){
            this.circle.r=width/2;
    }
    
    public void set(double x,double y){
        this.circle.pc.set(x,y); 
    }
    
    @Override
    public void move(double xoffset,double yoffset) {
        this.circle.move(xoffset,yoffset);
    }
    @Override
    public void rotate(double alpha, Point pt) {
     this.circle.rotate(alpha,pt);        
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        
        g2.setColor(fillColor);
        Circle c=this.circle.clone();
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());
        c.paint(g2,true);
                        
    }
    
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {        
        g2.setColor(printContext.getBackgroundColor());        
        this.circle.paint(g2, true);                 
    }
    
    @Override
    public String toXML() {
        return "<drill type=\"CIRCULAR\" x=\""+this.circle.pc.x+"\" y=\""+this.circle.pc.y+"\" width=\""+2*this.circle.r+"\" />";      
    }

    @Override
    public void fromXML(Node node) {
        Element  element= (Element)node;
        this.set(Double.parseDouble(element.getAttribute("x")),Double.parseDouble(element.getAttribute("y")));
        this.setWidth(Double.parseDouble(element.getAttribute("width")));  
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    
     public static class Memento extends AbstractMemento<Footprint,Drill>{
        double x,y,r;

        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(Drill shape) {
            if(shape==null)
                return;
            super.loadStateTo(shape);
            shape.circle.pc.set(x,y);
            shape.circle.r=r;                            
        }
        
        @Override
        public void saveStateFrom(Drill shape) {
            if(shape==null)
                return;

            super.saveStateFrom(shape);
            x=shape.circle.pc.x;
            y=shape.circle.pc.y;
            r=shape.circle.r;            
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
            
               return Utils.EQ(x,other.x)
                    &&Utils.EQ(y,other.y)
                    &&Utils.EQ(r,other.r);                
        }

        @Override
        public int hashCode() {
            int hash=31;
            hash +=Double.hashCode(x)+Double.hashCode(y)+Double.hashCode(r);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
             throw new IllegalStateException("Drill can not exist by iself");
        }
    
        
    }
}
