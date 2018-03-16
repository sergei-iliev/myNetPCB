package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.ClearanceSource;
import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.board.CompositeLayerable;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.shape.GlyphLabel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import java.lang.ref.WeakReference;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PCBLabel extends GlyphLabel implements PCBShape,ClearanceTarget,Ownerable<Shape>, Externalizable{    
    
    private WeakReference<Shape> weakParentRef;
    
    private int clearance;
    
    public PCBLabel(int layermaskId) {
        super("Label",Grid.MM_TO_COORD(0.3),layermaskId);
    }
    
    public PCBLabel clone() throws CloneNotSupportedException {
        PCBLabel copy = (PCBLabel)super.clone();
        copy.weakParentRef=null;        
        return copy;
    }
    
    public Shape getOwner() {
        if (weakParentRef != null && weakParentRef.get() != null) {
            return weakParentRef.get();
        }
        return null;
    }
    
    @Override
    public void Clear() {
        super.Clear();
        setOwner(null);
    }
    
    public void setOwner(Shape parent) {
        if (parent == null) {
            /*
          * nulify
          */
            if (this.weakParentRef != null && this.weakParentRef.get() != null) {
                this.weakParentRef.clear();
                this.weakParentRef = null;
            }
        } else {
            /*
           * assign
           */
            if (this.weakParentRef != null && this.weakParentRef.get() != null) {
                this.weakParentRef.clear();
            }
            this.weakParentRef = new WeakReference<Shape>(parent);
        }
    }
    
    @Override
    public int getDrawingOrder() {
        int order=super.getDrawingOrder();
        if(getOwningUnit()==null){            
            return order;
        }
        
        if(((CompositeLayerable)getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())){
          order= 4;
        }else{
          order= 3; 
        }  
        return order;
    }
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }

    @Override
    public <T extends PCBShape & ClearanceSource> void drawClearence(Graphics2D g2,
                                                                     ViewportWindow viewportWindow,
                                                                     AffineTransform scale, T source) {

        Shape shape=(Shape)source;
        if((shape.getCopper().getLayerMaskID()&this.copper.getLayerMaskID())==0){        
             return;  //not on the same layer
        } 
        
        Rectangle rect=texture.getBoundingShape();
        rect.grow(this.clearance!=0?this.clearance:source.getClearance(),this.clearance!=0?this.clearance:source.getClearance());

        if(!shape.getBoundingShape().intersects(rect)){
           return; 
        }        
        
        Rectangle2D scaledRect = Utilities.getScaleRect(rect,scale);
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        scaledRect.setRect(scaledRect.getX()-viewportWindow.x, scaledRect.getY()-viewportWindow.y, scaledRect.getWidth(), scaledRect.getHeight());
        g2.setColor(Color.BLACK);        
        g2.fill(scaledRect);
        
        
    }
    
    @Override
    public <T extends PCBShape & ClearanceSource> void printClearence(Graphics2D g2,PrintContext printContext, T source) {
        Shape shape=(Shape)source;
        if((source.getCopper().getLayerMaskID()&this.copper.getLayerMaskID())==0){        
             return;  //not on the same layer
        }
        
        Rectangle rect=texture.getBoundingShape();        
        rect.grow(this.clearance!=0?this.clearance:source.getClearance(), this.clearance!=0?this.clearance:source.getClearance());
        
        if(!shape.getBoundingShape().intersects(rect)){
           return; 
        }  
        
        g2.setColor(printContext.getBackgroundColor());        
        g2.fill(rect);
    }
    
    @Override
    public void setClearance(int clearance) {
          this.clearance=clearance;    
    }

    @Override
    public int getClearance() {
        return clearance;
    }

    @Override
    public String toXML() {
        if (!texture.isEmpty())
            return "<label layer=\""+this.copper.getName()+"\" clearance=\""+this.clearance+"\" >" + texture.toXML() + "</label>\r\n";
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
        if(!element.getAttribute("clearance").isEmpty()){
            setClearance(Integer.parseInt(element.getAttribute("clearance")));
        }
        this.texture.fromXML(node);        
    }

    public static class Memento extends AbstractMemento<Board,PCBLabel>{
        GlyphTexture.Memento memento;
        private int clearance;
        
        public Memento(MementoType mementoType){
          super(mementoType);  
            memento=new GlyphTexture.Memento();
        }
        @Override
        public void loadStateTo(PCBLabel shape) {
          super.loadStateTo(shape);  
          memento.loadStateTo(shape.texture); 
          shape.clearance=clearance;
        }
        @Override
        public void saveStateFrom(PCBLabel shape){
            super.saveStateFrom(shape);
            memento.saveStateFrom(shape.texture);
            clearance=shape.clearance;
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

            return(this.getUUID().equals(other.getUUID()) &&layerindex==other.layerindex&&
                   getMementoType().equals(other.getMementoType())&&
                   memento.equals(other.memento)&&clearance==other.clearance
                );            
          
        }
        
        @Override
        public int hashCode(){
          int hash=getUUID().hashCode();
          hash+=getMementoType().hashCode()+layerindex;
          hash+=memento.hashCode();
          hash+=clearance;
          return hash;
        }        
        public boolean isSameState(Board unit) {
            PCBLabel label=(PCBLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this)); 
        }
    }

}
