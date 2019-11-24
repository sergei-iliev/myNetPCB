package com.mynetpcb.board.shape;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.GlyphLabel;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class PCBLabel extends GlyphLabel implements PCBShape,ClearanceTarget{
    
    
    public PCBLabel(int layermaskId) {
        super("Label",(int)Grid.MM_TO_COORD(0.3),layermaskId);
    }

    @Override
    public <T extends ClearanceSource> void drawClearence(Graphics2D graphics2D, ViewportWindow viewportWindow,
                                                          AffineTransform affineTransform, T clearanceSource) {
        // TODO Implement this method

    }

    @Override
    public <T extends ClearanceSource> void printClearence(Graphics2D graphics2D, PrintContext printContext,
                                                           T clearanceSource) {
        // TODO Implement this method

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
