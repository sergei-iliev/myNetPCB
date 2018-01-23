package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Label;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class FontLabel extends Shape implements Label,Externalizable{
    
    protected FontTexture texture;
    
    public FontLabel(){
       this(0,0,0); 
    }
    
    public String toXML() {
       return toXML(this.texture);
    }
    
    public static String toXML(Texture texture){
        if(texture!=null&&!texture.isEmpty())
          return "<label color=\""+texture.getFillColor().getRGB()+"\">"+texture.toXML()+"</label>\r\n";
        else
          return "";          
    }
    public FontLabel(int x,int y,int layermaskId) {
        super(x,y,0,0, -1,layermaskId);
        texture=new FontTexture("label","Label",x,y,Text.Alignment.LEFT,8);
        texture.setFillColor(Color.BLACK);
    }
    
    
    @Override
    public FontLabel clone()throws CloneNotSupportedException{
        FontLabel copy=(FontLabel)super.clone();
        copy.texture=this.texture.clone();
        return copy;
    }
    @Override
    public Texture getTexture(){
        return this.texture;
    }
    
    @Override
    public void Clear() {
        texture.Clear();
    }
    
    @Override
    public void Move(int xoffset, int yoffset) {
        texture.Move(xoffset, yoffset);
    }

    @Override
    public void Mirror(Point A,Point B) {
        texture.Mirror(A,B);
    }

    @Override
    public void Translate(AffineTransform translate) {
        texture.Translate(translate);
    }

    @Override
    public void Rotate(AffineTransform rotation) {
      texture.Rotate(rotation);
    }

    @Override
    public void setLocation(int x, int y) {
      texture.setLocation(x, y);
    }
    @Override 
    public Rectangle calculateShape(){ 
     return texture.getBoundingShape();
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
    public boolean isClicked(int x, int y) {
        return texture.isClicked(x,y);
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
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        texture.Paint(g2, viewportWindow, scale,layermask);
    }
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        texture.setFillColor(printContext.isBlackAndWhite()?Color.BLACK:texture.getFillColor());        
        texture.Paint(g2, new ViewportWindow(0,0,0,0), AffineTransform.getScaleInstance(1, 1),layermask);
    }


    public void fromXML(Node node){              
          fromXML(node,this.texture);              
    }
    /*
     * Use from SCHSymbol
     */
    public static void fromXML(Node node,Texture texture){
        Element  element= (Element)node;
        texture.setFillColor(element.getAttribute("color").equals("")?Color.BLACK:new Color(Integer.parseInt(element.getAttribute("color"))));
        texture.fromXML(node);
    }
    
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
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

            return(this.getUUID().equals(other.getUUID()) &&
                   getMementoType()==other.getMementoType()&&
                   textureMemento.equals(other.textureMemento)
                );            
          
        }
        
        @Override
        public int hashCode(){
          int hash=getUUID().hashCode();
          hash+=getMementoType().hashCode();
          hash+=textureMemento.hashCode();
          return hash;
        }     
        
        public boolean isSameState(Symbol unit) {
            FontLabel label=(FontLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this)); 
        }
    }
}
