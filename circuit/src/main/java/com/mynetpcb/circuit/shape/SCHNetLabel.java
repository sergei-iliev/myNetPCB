package com.mynetpcb.circuit.shape;


import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Label;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Utils;

import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;

import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;

import com.mynetpcb.core.capi.unit.Unit;

import com.mynetpcb.core.utils.Utilities;

import java.awt.Font;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.awt.geom.Ellipse2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SCHNetLabel extends Shape implements  Label,Externalizable{
    
    private static final int OFFSET=2;
    private SymbolFontTexture texture;
    private Point point;
    
    public SCHNetLabel() {
        super( 1,Layer.LAYER_ALL); 
        this.selectionRectWidth=3;
        this.texture=new SymbolFontTexture("netLabel","name",-4,2,Texture.Alignment.RIGHT.ordinal(),8,Font.PLAIN); 
        this.selectionRectWidth=2;
        this.point=new Point();
        setTextLocation();        
        
    }

    public SCHNetLabel clone() throws CloneNotSupportedException {
        SCHNetLabel copy = (SCHNetLabel)super.clone();
        copy.point=this.point.clone();
        copy.texture = this.texture.clone();
        return copy;
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {        
        Point p=this.getOwningUnit().getGrid().positionOnGrid(point.x,point.y);
        this.move(p.x - point.x,p.y - point.y);   
        return null;
    }  
    
    @Override
    public Texture getTexture(){
        return texture;
    }
    
    @Override
    public String getDisplayName() {
        return "NetLabel";
    }
    
    
    @Override
    public Box getBoundingShape() {        
        return this.texture.getBoundingShape();
    }
    
    @Override
    public void move(double xoffset,double yoffset) {
        this.point.move(xoffset,yoffset);        
        this.texture.move(xoffset, yoffset);     
    }

    @Override
    public void rotate(double angle,Point origin) {
        this.point.rotate(angle,origin);
        this.texture.setRotation(angle,origin);        
    }
    @Override
    public void mirror(Line line) {        
        this.point.mirror(line);
        this.texture.setMirror(line);                  
    }
    public void setAlignment(Texture.Alignment alignment){
        texture.setAlignment(alignment);            
        setTextLocation();
    }
    
    private void setTextLocation(){
        if(texture.getAlignment().getOrientation()==Texture.Orientation.HORIZONTAL){
          //texture.setAlignment(Texture.Alignment.LEFT);
          texture.set(point.x+OFFSET,point.y-OFFSET);
        }else{
          //texture.setAlignment(Texture.Alignment.BOTTOM);
          texture.set(point.x-OFFSET,point.y-OFFSET);  
        }
    }
    
    @Override
    public void fromXML(Node node) {        
        Element element=(Element)node;
        point.set(Double.parseDouble(element.getAttribute("x")),Double.parseDouble(element.getAttribute("y")));        
        this.texture.fromXML(node);
        this.setTextLocation();
    }
    
    @Override
    public String toXML() {        
        return "<netlabel x=\""+ Utilities.roundDouble(point.x,1)+"\" y=\""+ Utilities.roundDouble(point.y,1)+"\">" + texture.toXML() + "</netlabel>\r\n";        
    }
    
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
      
                  Box rect = this.texture.getBoundingShape();
                  rect.scale(scale.getScaleX());
                  if (!rect.intersects(viewportWindow)) {
                        return;
                  }
                  texture.setFillColor(isSelected()?Color.BLUE:fillColor);        
                  this.texture.paint(g2, viewportWindow, scale,layersmask);   
                  
                  if(isSelected()){
                      g2.setColor(Color.GRAY);
                      Circle c=new Circle(point.clone(),selectionRectWidth);
                      c.scale(scale.getScaleX());
                      c.move(-viewportWindow.getX() ,- viewportWindow.getY());        
                      c.paint(g2,true); 
                  }
                    
                    
    }
    @Override
    public void print(Graphics2D g2,PrintContext printContext, int layermask) {        
        this.texture.print(g2,printContext,layermask); 
    }
    
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    static class Memento extends AbstractMemento<Circuit, SCHNetLabel> {

        Texture.Memento textureMemento;
        private double x;        
        private double y;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
            textureMemento=new SymbolFontTexture.Memento();
        }

        @Override
        public void loadStateTo(SCHNetLabel shape) {
            super.loadStateTo(shape);
            shape.point.set(x,y);            
            textureMemento.loadStateTo(shape.texture);  
        }

        @Override
        public void saveStateFrom(SCHNetLabel shape) {
            super.saveStateFrom(shape);
            x=shape.point.x;
            y=shape.point.y;
            textureMemento.saveStateFrom(shape.texture);            
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

            return super.equals(obj)&&textureMemento.equals(other.textureMemento)&&                      
                            Utils.EQ(x, other.x) && Utils.EQ(y, other.y);
                    
        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode()+textureMemento.hashCode();
            hash += Double.hashCode(x);
            hash += Double.hashCode(y);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            SCHNetLabel label = (SCHNetLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this));
        }
    }    
    
}
