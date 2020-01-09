package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.pad.shape.GlyphLabel;

import java.awt.Color;
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
    public void setClearance(int i) {
        // TODO Implement this method
    }

    @Override
    public int getClearance() {
        // TODO Implement this method
        return 0;
    }
    public static class Memento extends GlyphLabel.Memento{
        public Memento(MementoType mementoType) {
            super(mementoType);
        }
    }
}
