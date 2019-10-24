package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.shape.AbstractLine;
import com.mynetpcb.core.capi.shape.Shape.Fill;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;

import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.d2.shapes.RoundRectangle;

import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class Line extends AbstractLine implements Externalizable{
    public Line(int thickness,int layermaskId) {
        super(thickness, layermaskId);
    }

    public Line clone() throws CloneNotSupportedException {
        Line copy = (Line) super.clone();
        copy.floatingStartPoint = new Point();
        copy.floatingMidPoint = new Point();
        copy.floatingEndPoint = new Point();
        copy.polyline=this.polyline.clone();
        return copy;
    }


    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        
        Box rect = this.polyline.box();
        rect.scale(scale.getScaleX());           
        if (!this.isFloating()&& (!rect.intersects(viewportWindow))) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        
        Polyline r=this.polyline.clone();   
        
        // draw floating point
        if (this.isFloating()) {
            Point p = this.floatingEndPoint.clone();                              
            r.add(p); 
        }
        
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        double wireWidth = thickness * scale.getScaleX();
        g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));

        //transparent rect
        r.paint(g2, false);
      
        if (this.isSelected()) {
            polyline.points.forEach(p->Utilities.drawCrosshair(g2, viewportWindow, scale,  resizingPoint,selectionRectWidth,(Point)p));
            
        }
        
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
    
    public static class Memento extends AbstractMemento<Footprint, Line> {

        private double Ax[];

        private double Ay[];
        private double rotate;
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(Line shape) {
            super.loadStateTo(shape);
            shape.polyline.points.clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.addPoint(new Point(Ax[i], Ay[i]));
            }
            //***reset floating start point
            if (shape.polyline.points.size() > 0) {
                shape.floatingStartPoint.set((Point)shape.polyline.points.get(shape.polyline.points.size() - 1));
                shape.reset();
            }
        }

        @Override
        public void saveStateFrom(Line shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.polyline.points.size()];
            Ay = new double[shape.polyline.points.size()];
            for (int i = 0; i < shape.polyline.points.size(); i++) {
                Ax[i] = ((Point)shape.polyline.points.get(i)).x;
                Ay[i] = ((Point)shape.polyline.points.get(i)).y;
            }
            this.rotate=shape.rotate;
        }

        @Override
        public void clear() {
            super.clear();
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
            Memento other = (Memento) obj;
            return (super.equals(obj)&&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode();
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }

        public boolean isSameState(Footprint unit) {
            Line line = (Line) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }    
}
