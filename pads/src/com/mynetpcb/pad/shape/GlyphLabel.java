package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GlyphLabel extends Shape implements Externalizable{
    protected GlyphTexture texture;
    
    public GlyphLabel(String text,int thickness,int layermaskId) {
        super(0, 0, 0, 0, thickness, layermaskId);
        texture=new GlyphTexture(text,"",0,0,thickness);
        texture.setSize(Grid.MM_TO_COORD(2));
    }
    
    public GlyphLabel(){
       this("",0,Layer.SILKSCREEN_LAYER_FRONT); 
    }
    
    public GlyphLabel clone() throws CloneNotSupportedException {
        GlyphLabel copy = (GlyphLabel) super.clone();    
        copy.texture = this.texture.clone();        
        return copy;
    }
    
    @Override 
    public Rectangle calculateShape(){ 
     return texture.getBoundingShape();
    }
    public GlyphTexture getTexture(){
       return texture;    
    }
    @Override
    public void Mirror(Point A,Point B) {
        texture.Mirror(A,B);
    }
    @Override
    public void Rotate(AffineTransform rotation) {
      texture.Rotate(rotation);
    }
    @Override
    public void Move(int xoffset, int yoffset) {
        texture.Move(xoffset, yoffset);
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        Rectangle2D scaledRect = Utilities.getScaleRect(texture.getBoundingShape(),scale);
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        texture.setFillColor((isSelected()?Color.GRAY:this.copper.getColor()));
        texture.Paint(g2, viewportWindow, scale,copper.getLayerMaskID());
    }
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {        
        if((this.copper.getLayerMaskID()&layermask)==0){
            return;
        }
        texture.setFillColor(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());
        texture.Print(g2, printContext,layermask);
    }
    @Override
    public long getOrderWeight(){
        return 0;
    } 
    @Override
    public String getDisplayName(){
        return "Label";
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
    public String toXML() {
        if (!texture.isEmpty())
            return "<label layer=\""+this.copper.getName()+"\">" + texture.toXML() + "</label>\r\n";
        else
            return "";
    }
    
    @Override
    public void fromXML(Node node){
        //extract layer info
        Element  element= (Element)node;
        if(element.getAttribute("layer")!=null&&!element.getAttribute("layer").isEmpty()){
           this.copper =Layer.Copper.valueOf(element.getAttribute("layer"));
        }else{
           this.copper=Layer.Copper.FSilkS;
        }
        this.texture.fromXML(node);        
    }
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }

    public static class Memento extends AbstractMemento<Footprint,GlyphLabel>{
        GlyphTexture.Memento memento;
        
        public Memento(MementoType mementoType){
          super(mementoType);  
          memento=new GlyphTexture.Memento();
        }
        @Override
        public void loadStateTo(GlyphLabel shape) {
          super.loadStateTo(shape);  
          memento.loadStateTo(shape.texture);  
        }
        @Override
        public void saveStateFrom(GlyphLabel shape){
            super.saveStateFrom(shape);
            memento.saveStateFrom(shape.texture);
        }
        
        @Override
        public void Clear(){
          super.Clear();
          memento.Clear();
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

            return(this.getUUID().equals(other.getUUID()) &&
                   getMementoType().equals(other.getMementoType())&&
                   memento.equals(other.memento)
                );            
          
        }
        
        @Override
        public int hashCode(){
          int hash=getUUID().hashCode();
          hash+=getMementoType().hashCode();
          hash+=memento.hashCode();
          return hash;
        }        
        public boolean isSameState(Footprint unit) {
            GlyphLabel label=(GlyphLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this)); 
        }
    }
}
