package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Label;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.Texture.Alignment;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FontLabel extends Shape implements Label,Externalizable{
    private SymbolFontTexture texture;
    
    public FontLabel() {
        super(1,Layer.LAYER_ALL);
        this.setDisplayName("Label");           
        this.texture=new SymbolFontTexture("label","Label",0,0,Texture.Alignment.LEFT.ordinal(),8,Font.PLAIN);
      
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
    public long getClickableOrder() {           
            return 0;
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
        Alignment alignment=Texture.Alignment.from(this.texture.shape.alignment);                 
        this.texture.rotate(angle,origin);
        if(angle<0){  //clockwise              
            if(alignment.getOrientation() == Texture.Orientation.HORIZONTAL){
                this.texture.shape.anchorPoint.set(this.texture.shape.anchorPoint.x+(this.texture.shape.metrics.ascent-this.texture.shape.metrics.descent),this.texture.shape.anchorPoint.y);            
            }
        }else{                   
            if(alignment.getOrientation() == Texture.Orientation.VERTICAL){
                this.texture.shape.anchorPoint.set(this.texture.shape.anchorPoint.x,this.texture.shape.anchorPoint.y+(this.texture.shape.metrics.ascent-this.texture.shape.metrics.descent));                   
            }
        }
    }
    @Override
    public void mirror(Line line) {
        Alignment alignment=Texture.Alignment.from(this.texture.shape.alignment); 
        this.texture.mirror(line);      
        if (line.isVertical()) { //right-left mirroring
            if (this.texture.shape.alignment == alignment.ordinal()) {
                this.texture.shape.anchorPoint.set(this.texture.shape.anchorPoint.x +
                                        (this.texture.shape.metrics.ascent - this.texture.shape.metrics.descent),this.texture.shape.anchorPoint.y);
            }
        } else { //***top-botom mirroring          
            if (this.texture.shape.alignment == alignment.ordinal()) {
                this.texture.shape.anchorPoint.set(this.texture.shape.anchorPoint.x,this.texture.shape.anchorPoint.y +(this.texture.shape.metrics.ascent - this.texture.shape.metrics.descent));
            }
        }  
        
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
//        if(this.texture!=null&&!this.texture.isEmpty())
//            return "<label color=\""+texture.getFillColor().getRGB()+"\">"+this.texture.toXML()+"</label>";
//          else
//            return "";  
       return toXML(this.texture);
    }
    public static String toXML(Texture texture){
        if(texture!=null&&!texture.isEmpty())
          return "<label color=\""+texture.getFillColor().getRGB()+"\">"+texture.toXML()+"</label>\r\n";
        else
          return "";          
    }

    @Override
    public void fromXML(Node node) {
        Element  element= (Element)node;
        texture.setFillColor(element.getAttribute("color").equals("")?Color.BLACK:new Color(Integer.parseInt(element.getAttribute("color"))));
        texture.fromXML(node);        
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
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {               
        texture.print(g2,printContext,layermask);
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
          textureMemento=new SymbolFontTexture.Memento();
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
        @Override
        public boolean isSameState(Unit unit) {
            FontLabel label=(FontLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this));             
        }
    }
}
