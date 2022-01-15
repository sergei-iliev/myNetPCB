package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.pad.shape.GlyphLabel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class PCBLabel extends GlyphLabel implements PCBShape,ClearanceTarget{
    
    private int clearance;
    
    public PCBLabel(int layermaskId) {
        super("Label",(int)Grid.MM_TO_COORD(0.3),layermaskId);
    }
    
    @Override
    public void mirror(Line line) {
        texture.mirror(line);
    }

//    @Override
//    public int getDrawingLayerPriority() {
//   	 	if(!this.copper.isCopperLayer()) {
//		 return super.getDrawingLayerPriority();
//   	 	}
//	  	if(((CompositeLayerable)getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())){
//   	 		return 4;
//   	 	}else{
//   	 		return 3; 
//   	 	}           
//    }

    @Override
    public <T extends ClearanceSource> void drawClearance(Graphics2D g2, ViewportWindow viewportWindow,
                                                          AffineTransform scale, T source) {
        Shape shape=(Shape)source;
        if((shape.getCopper().getLayerMaskID()&this.copper.getLayerMaskID())==0){        
             return;  //not on the same layer
        }         
        Box rect = this.getBoundingShape();
        rect.grow(this.clearance!=0?this.clearance:source.getClearance());        
        
        //is via within copper area
        if(!(source.getBoundingShape().intersects(rect))){
           return; 
        }
        
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)){
                return;
        }   
        
        
        rect.move(-viewportWindow.getX(),- viewportWindow.getY());
        g2.setColor(Color.BLACK);
        rect.paint(g2,true);

    }

    @Override
    public <T extends ClearanceSource> void printClearance(Graphics2D graphics2D, PrintContext printContext,
                                                           T source) {


    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {        
        Box rect = this.texture.getBoundingShape();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
            return;
        }
        Composite originalComposite = g2.getComposite();
        AlphaComposite composite;
        if(((CompositeLayerable)this.getOwningUnit()!=null)&&(((CompositeLayerable)this.getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID()))) {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);                                                        	  
        }else {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);                                                        	           
        }                                               
        g2.setComposite(composite );             
        
        texture.setFillColor((isSelected()?Color.GRAY:this.copper.getColor()));
        texture.paint(g2, viewportWindow, scale,copper.getLayerMaskID());
        
        g2.setComposite(originalComposite);
    }

    @Override
    public void setClearance(int clearance) {
        this.clearance=clearance;
    }

    @Override
    public int getClearance() {        
        return clearance;
    }

    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    public static class Memento extends AbstractMemento<Board,PCBLabel>{
        GlyphTexture.Memento memento;
        
        public Memento(MementoType mementoType){
          super(mementoType);  
          memento=new GlyphTexture.Memento();
        }
        @Override
        public void loadStateTo(PCBLabel shape) {
          super.loadStateTo(shape);  
          memento.loadStateTo(shape.texture);  
        }
        @Override
        public void saveStateFrom(PCBLabel shape){
            super.saveStateFrom(shape);
            memento.saveStateFrom(shape.texture);
        }
        
        @Override
        public void clear(){
          super.clear();
          memento.clear();
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

            return(super.equals(obj)&&
                   memento.equals(other.memento)
                );            
          
        }
        
        @Override
        public int hashCode(){
          int hash=super.hashCode();          
          hash+=memento.hashCode();
          return hash;
        }        
        @Override
        public boolean isSameState(Unit unit) {
            PCBLabel label=(PCBLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this)); 
        }
    }

}
