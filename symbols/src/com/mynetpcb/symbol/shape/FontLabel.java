package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Label;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class FontLabel extends Shape implements Label,Externalizable{
    private SymbolFontTexture texture;
    
    public FontLabel() {
        super(1,Layer.LAYER_ALL);
        this.setDisplayName("Label");           
        this.texture=new SymbolFontTexture("label","Label",0,0,8,0);
      
    }
    @Override
    public FontLabel clone()throws CloneNotSupportedException{
        FontLabel copy=(FontLabel)super.clone();
        copy.texture=this.texture.clone();
        return copy;
    }
    @Override
    public void clear() {
        texture.clear();
    }
    @Override
    public Box getBoundingShape() {        
        return this.texture.getBoundingShape();
    }
    @Override
    public boolean isClicked(int x,int y) {
            if (this.texture.isClicked(x, y))
                    return true;
            else
                    return false;
    }    
    @Override
    public void setSelected(boolean selected) {
        this.texture.setSelected(selected);
    }
    @Override
    public boolean isSelected() {
        return this.texture.isSelected();
    }
    @Override
    public void rotate(double angle,Point origin) {
       this.texture.rotate(angle,origin);
    }
    @Override
    public void move(double xoffset,double yoffset) {
        this.texture.move(xoffset, yoffset);
    }
    public Point getCenter() {
       return this.texture.getAnchorPoint();
    }    
    
    @Override
    public Texture getTexture() {    
        return texture;
    }

    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        // TODO Implement this method

    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
      
                  Box rect = this.texture.getBoundingShape();
                  rect.scale(scale.getScaleX());
                  if (!rect.intersects(viewportWindow)) {
                        return;
                  }

                  this.texture.paint(g2, viewportWindow, scale,layersmask);        
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public static class Memento extends AbstractMemento<Symbol,FontLabel>{
        Texture.Memento textureMemento;
        
        public Memento(MementoType mementoType){
          super(mementoType);  
          textureMemento=new FontTexture.Memento();
        }
        @Override
        public void loadStateTo(FontLabel shape) {
          super.loadStateTo(shape);  
          textureMemento.loadStateTo(shape.texture);  
        }
        @Override
        public void saveStateFrom(FontLabel shape){
            super.saveStateFrom(shape);
            textureMemento.saveStateFrom(shape.texture);
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

            return super.equals(obj)&&textureMemento.equals(other.textureMemento);                          
        }
        
        @Override
        public int hashCode(){
          int hash = super.hashCode()+textureMemento.hashCode();
          return hash;
        }     
        
        public boolean isSameState(Symbol unit) {
            FontLabel label=(FontLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this));             
        }
    }
}
