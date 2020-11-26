package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Sublineable;
import com.mynetpcb.core.capi.shape.AbstractLine;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.d2.shapes.Rectangle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SCHWire extends AbstractLine implements Sublineable,Externalizable {
    public SCHWire(){
        super(1,Layer.LAYER_NONE);  
        this.fillColor=Color.BLACK;
        this.displayName="Wire";
        this.selectionRectWidth=2;
        
    }
    public SCHWire clone()throws CloneNotSupportedException{
            SCHWire copy=(SCHWire)super.clone();
            copy.floatingStartPoint = new Point();
            copy.floatingMidPoint = new Point();
            copy.floatingEndPoint = new Point();
            copy.resizingPoint=null;
            copy.polyline=this.polyline.clone();
            return copy;
    }  
    @Override
    public Point alignToGrid(boolean isRequired) {
        for (Point wirePoint : getLinePoints()) {
            Point point =
                getOwningUnit().getGrid().positionOnGrid(wirePoint.x, wirePoint.y);
            wirePoint.set(point);
        }
        return null;
    }    
    @Override
    public boolean isSublineSelected() {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean isSublineInRect(Rectangle rectangle) {
        // TODO Implement this method
        return false;
    }

    @Override
    public void setSublineSelected(Rectangle rectangle, boolean b) {
        // TODO Implement this method

    }

    @Override
    public Set<LinePoint> getSublinePoints() {
        // TODO Implement this method
        return Collections.emptySet();
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Box rect = this.polyline.box();
        rect.scale(scale.getScaleX());           
        if (!this.isFloating()&& (!rect.intersects(viewportWindow))) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY :fillColor);
        
        Polyline r=this.polyline.clone();         
        
        // draw floating point
        if (this.isFloating()) {
            Point p = this.floatingMidPoint.clone(); 
            r.add(p);
            p = this.floatingEndPoint.clone();
            r.add(p);
        }
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        double wireWidth = thickness * scale.getScaleX();
        g2.setStroke(new BasicStroke((float)wireWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));   
        //transparent rect
        r.paint(g2, false);
        
        
        
        if (this.isSelected()&&isControlPointVisible) {
            Point pt=null;
            if(resizingPoint!=null){
                pt=resizingPoint.clone();
                pt.scale(scale.getScaleX());
                pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }
            for(Object p:r.points){
              Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),(Point)p); 
            }
        }       
        
    }
    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element e = (Element)node;
        if(!e.getAttribute("style").equals("")){
           //this.style = Integer.parseInt(e.getAttribute("style"));
        }
        
        NodeList nodelist = e.getElementsByTagName("wirepoints");
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node n = nodelist.item(i);
            StringTokenizer st =
                new StringTokenizer(Utilities.trimCRLF(n.getTextContent()), "|");
            
            for (; st.hasMoreElements(); ) {
                LinePoint point = new LinePoint(0,0);
                StringTokenizer stock=new StringTokenizer(st.nextToken(),",");
                point.set(Double.parseDouble(stock.nextToken()),Double.parseDouble(stock.nextToken()));  
                this.polyline.add(point);
            }
        } 

    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Circuit, SCHWire> {

        private double Ax[];

        private double Ay[];
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(SCHWire shape) {
            super.loadStateTo(shape);
            shape.polyline.points.clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.add(new Point(Ax[i], Ay[i]));
            }
            //***reset floating start point
            if (shape.polyline.points.size() > 0) {
                shape.floatingStartPoint.set((Point)shape.polyline.points.get(shape.polyline.points.size() - 1));
                shape.reset();
            }
        }

        @Override
        public void saveStateFrom(SCHWire shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.polyline.points.size()];
            Ay = new double[shape.polyline.points.size()];
            for (int i = 0; i < shape.polyline.points.size(); i++) {
                Ax[i] = ((Point)shape.polyline.points.get(i)).x;
                Ay[i] = ((Point)shape.polyline.points.get(i)).y;
            }
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
        @Override
        public boolean isSameState(Unit unit) {
            SCHWire line = (SCHWire) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }     
}
