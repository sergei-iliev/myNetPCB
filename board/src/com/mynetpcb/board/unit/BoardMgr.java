package com.mynetpcb.board.unit;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.dialog.FootprintInlineEditorDialog;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.shape.GlyphLabel;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.Point;
import java.awt.Rectangle;

public final class BoardMgr extends UnitMgr {
    private static BoardMgr circuitMgr;

    private BoardMgr() {

    }


    public static synchronized BoardMgr getInstance() {
        if (circuitMgr == null) {
            circuitMgr = new BoardMgr();
        }
        return circuitMgr;
    }
    public PCBFootprint createPCBFootprint(Footprint footprint,Layer.Side activeSide) {
        PCBFootprint pcbfootprint = new PCBFootprint(Layer.LAYER_FRONT);
        this.createPCBFootprint(footprint,pcbfootprint);
        //default side is TOP 
        if(pcbfootprint.getSide()!=activeSide){
           pcbfootprint.setSide(activeSide);
        }
        return pcbfootprint;
    }
    private void createPCBFootprint(Footprint footprint,PCBFootprint pcbfootprint) {
        for (Shape shape : footprint.getShapes()) {
            if (shape instanceof GlyphLabel) {
                if (((GlyphLabel)shape).getTexture().getTag().equals("value")) {
                    pcbfootprint.getChipText().getTextureByTag("value").copy(((GlyphLabel)shape).getTexture());
                    pcbfootprint.getChipText().getTextureByTag("value").setLayermaskId(shape.getCopper().getLayerMaskID());
                    continue;
                }
                if (((GlyphLabel)shape).getTexture().getTag().equals("reference")) {
                    pcbfootprint.getChipText().getTextureByTag("reference").copy(((GlyphLabel)shape).getTexture());
                    pcbfootprint.getChipText().getTextureByTag("reference").setLayermaskId(shape.getCopper().getLayerMaskID());
                    continue;
                }
            }
            try {
                pcbfootprint.Add(shape.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        pcbfootprint.setDisplayName(footprint.getUnitName());
        pcbfootprint.setGridUnits(footprint.getGrid().getGridUnits());
        pcbfootprint.setGridValue(footprint.getGrid().getGridValue());
    } 
    
    public Footprint createFootprint(PCBFootprint pcbfootprint) {
        Footprint footprint = new Footprint(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));
        //1.shapes
        for (Shape shape : pcbfootprint.getShapes()) {
            try {
                Shape copy=shape.clone();        
                footprint.Add(copy);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        //2.text
            if (pcbfootprint.getChipText().getTextureByTag("value") != null&&! pcbfootprint.getChipText().getTextureByTag("value").isEmpty() ) {
                GlyphLabel value=new GlyphLabel();
                value.getTexture().copy(pcbfootprint.getChipText().getTextureByTag("value"));
                value.setCopper(Layer.Copper.resolve(pcbfootprint.getChipText().getTextureByTag("value").getLayermaskId()));
                footprint.Add(value);
            }
            if (pcbfootprint.getChipText().getTextureByTag("reference") != null && ! pcbfootprint.getChipText().getTextureByTag("reference").isEmpty()) {
                GlyphLabel value=new GlyphLabel();
                value.getTexture().copy(pcbfootprint.getChipText().getTextureByTag("reference"));
                value.setCopper(Layer.Copper.resolve(pcbfootprint.getChipText().getTextureByTag("reference").getLayermaskId()));
                footprint.Add(value);
            }

        //3.name
        footprint.setUnitName(pcbfootprint.getDisplayName());
        //4.grid
        footprint.getGrid().setGridUnits(pcbfootprint.getGridValue(), pcbfootprint.getGridUnits());
        
        return footprint;
    } 
    public void openFootprintInlineEditorDialog(BoardComponent unitComponent,PCBFootprint pcbfootprint){
        
        //create Footprint
        FootprintContainer copy=new FootprintContainer();
        copy.Add(createFootprint(pcbfootprint));

        //center the copy
        int x=(int)copy.getUnit().getBoundingRect().getCenterX();
        int y=(int)copy.getUnit().getBoundingRect().getCenterY();
        moveBlock(copy.getUnit().getShapes(), (copy.getUnit().getWidth()/2)-x, (copy.getUnit().getHeight()/2)-y);
        alignBlock(copy.getUnit().getGrid(),copy.getUnit().getShapes());
        
        FootprintInlineEditorDialog footprintEditorDialog =
            new FootprintInlineEditorDialog(unitComponent.getDialogFrame().getParentFrame(), "Footprint Inline Editor",copy);
        footprintEditorDialog.pack();
        footprintEditorDialog.setLocationRelativeTo(null); //centers on screen
        footprintEditorDialog.setFocusable(true);
        footprintEditorDialog.setVisible(true);
        BoardComponent.getUnitKeyboardListener().setComponent(unitComponent);            
        
        if(footprintEditorDialog.getResult()!=null){
            BoardMgr.getInstance().switchFootprint(footprintEditorDialog.getResult().getUnit(),pcbfootprint);
            footprintEditorDialog.getResult().Release();
        }    
        copy.Release();        
        footprintEditorDialog.dispose();        
    }
    /**
     *Equalize inline changed footprint with in board positioned pcbfootprint
     * @param footprint - edited footprint
     * @param pcbfootprint - pcb one that needs to be equalized
     */
    
    public void switchFootprint(Footprint footprint,PCBFootprint pcbfootprint){
        //1.unselect
        footprint.setSelected(false);
        //2.get current position
        Rectangle rsrc=pcbfootprint.getPinsRect();        
        //3.clear pcbfootprint
        pcbfootprint.Clear();
        pcbfootprint.getChipText().Add(new GlyphTexture("","reference", 0, 0, Grid.MM_TO_COORD(2)));
        pcbfootprint.getChipText().Add(new GlyphTexture("","value",8, 8, Grid.MM_TO_COORD(2))); 
        //4.transfer
        createPCBFootprint(footprint, pcbfootprint);
        //5.get new position
        Rectangle rdst=pcbfootprint.getPinsRect();
        //6.go to new position
        pcbfootprint.Move(rsrc.x-rdst.x,rsrc.y-rdst.y);
    }
    
   /**
     * Align wire end to pad drill center
     * @param board
     * @param point
     */
    public void alignWirePointToPad(Board board,Point point){
        for (PCBFootprint footprint : board.<PCBFootprint>getShapes(PCBFootprint.class)) {
           Pad pad=footprint.getPin(point.x, point.y);
               if(pad!=null){
                   if(pad.getType()==Pad.Type.THROUGH_HOLE){
                      point.setLocation(pad.getDrill().getX(),pad.getDrill().getY());  
                   }else{
                       point.setLocation(pad.getX(),pad.getY());  
                   }
               }
               
        }
    }
/**
     * Select track points connected to footrpint pads
     * @param board
     * @param chip
     * @param bind
     */
    public void bindChipWirePoints(Board board, Pinaware footprint, boolean bind) {
        //***to bind you need unselected wire ends
        for (PCBTrack wire : board.<PCBTrack>getShapes(PCBTrack.class)) {
            for (LinePoint wirePoint : wire.getLinePoints()) {
                if (null != footprint.getPin(wirePoint.x, wirePoint.y)) {
                    wirePoint.setSelected(bind);
                }
            }
        }
    }    
}
