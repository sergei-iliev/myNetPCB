package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Segment;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SCHNoConnector extends Shape implements Externalizable{
    
    private Point point;
    
    public SCHNoConnector() {
        super( 1,Layer.LAYER_ALL); 
        this.fillColor=Color.black;
        this.setSelectionRectWidth(3);
        this.point=new Point();
    }
    
    @Override
    public SCHNoConnector clone() throws CloneNotSupportedException {
        SCHNoConnector copy = (SCHNoConnector)super.clone();
        copy.point=point.clone();
        return copy;
    }
    
    @Override
    public long getClickableOrder() {           
        return 3;
    }
    
    @Override
    public Point alignToGrid(boolean required) {        
        this.getOwningUnit().getGrid().snapToGrid(this.point); 
        return null;
    }
    @Override
    public void move(double xoff, double yoff) {
        this.point.move(xoff,yoff);
    }
    @Override
    public void mirror(Line line) {        
        this.point.mirror(line); 
    }    
    @Override
    public void rotate(double angle, Point pt) {        
        this.point.rotate(angle,pt);
    }
        
    @Override
    public Box getBoundingShape() {
        return Box.fromRect(this.point.x - this.selectionRectWidth, this.point.y - this.selectionRectWidth, 2 * this.selectionRectWidth,
                2 * this.selectionRectWidth);
    }


    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
        Box rect = this.getBoundingShape();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }       

        g2.setColor(isSelected()?Color.BLUE:fillColor);
        g2.setStroke(new BasicStroke((float)(this.thickness * scale.getScaleX()),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  
        
        Segment line=new Segment(this.point.x-this.selectionRectWidth, this.point.y-this.selectionRectWidth,this.point.x+this.selectionRectWidth, this.point.y+this.selectionRectWidth);                
        line.scale(scale.getScaleX());
        line.move(-viewportWindow.getX(),- viewportWindow.getY());  
        line.paint(g2,false);  
        
        line.set(this.point.x-this.selectionRectWidth, this.point.y+this.selectionRectWidth,this.point.x+this.selectionRectWidth, this.point.y-this.selectionRectWidth);               
        line.scale(scale.getScaleX());
        line.move(-viewportWindow.getX(),- viewportWindow.getY());  
        line.paint(g2,false);  

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setColor(fillColor);
        g2.setStroke(new BasicStroke((float)(this.thickness ),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  
        
        Segment line=new Segment(this.point.x-this.selectionRectWidth, this.point.y-this.selectionRectWidth,this.point.x+this.selectionRectWidth, this.point.y+this.selectionRectWidth);                
        line.paint(g2,false);  
        
        line.set(this.point.x-this.selectionRectWidth, this.point.y+this.selectionRectWidth,this.point.x+this.selectionRectWidth, this.point.y-this.selectionRectWidth);               
        line.paint(g2,false);    
    }
    @Override
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<noconnector x=\""+Utilities.roundDouble(point.x,1)+"\"  y=\""+Utilities.roundDouble(point.y,1)+"\"/>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element element=(Element)node;
        this.point.set(Double.parseDouble(element.getAttribute("x")),Double.parseDouble(element.getAttribute("y")));        
    }
    @Override
    public String getDisplayName() {
        return "NoConnection";
    }    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    static class Memento extends AbstractMemento<Circuit,SCHNoConnector>{
        private double x;        
        private double y;
        
        public Memento(MementoType mementoType){
           super(mementoType); 
        }
        
        public void loadStateTo(SCHNoConnector shape) {
            super.loadStateTo(shape);
            shape.point.set(x,y);            
        }
        

        public void saveStateFrom(SCHNoConnector shape){
            super.saveStateFrom(shape);
            x=shape.point.x;
            y=shape.point.y;
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
            SCHNoConnector line = (SCHNoConnector) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }
}
