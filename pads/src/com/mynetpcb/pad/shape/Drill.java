package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

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
    public void set(double x,double y){
        this.circle.pc.set(x,y); 
    }
    
    @Override
    public void move(double xoffset,double yoffset) {
        this.circle.move(xoffset,yoffset);
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
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void fromXML(Node node) {
        // TODO Implement this method

    }

}
