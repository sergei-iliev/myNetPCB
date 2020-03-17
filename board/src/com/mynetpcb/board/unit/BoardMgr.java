package com.mynetpcb.board.unit;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.dialog.FootprintInlineEditorDialog;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.shape.GlyphLabel;
import com.mynetpcb.pad.unit.Footprint;

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
        //if(pcbfootprint.getSide()!=activeSide){
        //   pcbfootprint.setSide(activeSide);
        //}
        return pcbfootprint;
    }
    private void createPCBFootprint(Footprint footprint,PCBFootprint pcbfootprint) {
        for (Shape shape : footprint.getShapes()) {
            if (shape instanceof GlyphLabel) {
                if (((GlyphLabel)shape).getTexture().getTag().equals("value")) {
                    pcbfootprint.getTextureByTag("value").copy(((GlyphLabel)shape).getTexture());
                    //pcbfootprint.getTextureByTag("value").setLayermaskId(shape.getCopper().getLayerMaskID());
                    continue;
                }
                if (((GlyphLabel)shape).getTexture().getTag().equals("reference")) {
                    pcbfootprint.getTextureByTag("reference").copy(((GlyphLabel)shape).getTexture());
                    //pcbfootprint.getTextureByTag("reference").setLayermaskId(shape.getCopper().getLayerMaskID());
                    continue;
                }
            }
            try {
                pcbfootprint.add(shape.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        pcbfootprint.setDisplayName(footprint.getUnitName());
        pcbfootprint.setGridUnits(footprint.getGrid().getGridUnits());
        pcbfootprint.setGridValue(footprint.getGrid().getGridValue());
    } 
    
    private Footprint createFootprint(PCBFootprint pcbfootprint) {
        
        pcbfootprint.setRotation(0,pcbfootprint.getCenter());
        Footprint footprint = new Footprint((int)Grid.MM_TO_COORD(100),(int)Grid.MM_TO_COORD(100));
        
        //1.shapes
        for (Shape shape : pcbfootprint.getShapes()) {
            try {
                Shape copy=shape.clone();        
                footprint.add(copy);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        //2.text
            if (pcbfootprint.getTextureByTag("value") != null&&! pcbfootprint.getTextureByTag("value").isEmpty() ) {
                GlyphLabel value=new GlyphLabel();
                value.getTexture().copy(pcbfootprint.getTextureByTag("value"));
                value.setCopper(Layer.Copper.resolve(pcbfootprint.getTextureByTag("value").getLayermaskId()));
                footprint.add(value);
            }
            if (pcbfootprint.getTextureByTag("reference") != null && ! pcbfootprint.getTextureByTag("reference").isEmpty()) {
                GlyphLabel value=new GlyphLabel();
                value.getTexture().copy(pcbfootprint.getTextureByTag("reference"));
                value.setCopper(Layer.Copper.resolve(pcbfootprint.getTextureByTag("reference").getLayermaskId()));
                footprint.add(value);
            }

        //3.name
        footprint.setUnitName(pcbfootprint.getDisplayName());
        //4.grid
        footprint.getGrid().setGridUnits(pcbfootprint.getGridValue(), pcbfootprint.getGridUnits());
        
        return footprint;
    } 
    public void openFootprintInlineEditorDialog(BoardComponent unitComponent,PCBFootprint pcbfootprint){
        //clone
        PCBFootprint clone=null;
        try {
            clone = pcbfootprint.clone();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace(); 
        }
        //create Footprint
        FootprintContainer copy=new FootprintContainer();
        copy.add(createFootprint(clone));

        //center the copy
        double x=copy.getUnit().getBoundingRect().getCenter().x;
        double y=copy.getUnit().getBoundingRect().getCenter().y;
        moveBlock(copy.getUnit().getShapes(), (copy.getUnit().getWidth()/2)-x, (copy.getUnit().getHeight()/2)-y);
        alignBlock(copy.getUnit().getGrid(),copy.getUnit().getShapes());
        
        FootprintInlineEditorDialog footprintEditorDialog =
            new FootprintInlineEditorDialog(null, "Footprint Inline Editor",copy);
        footprintEditorDialog.pack();
        footprintEditorDialog.setLocationRelativeTo(null); //centers on screen
        footprintEditorDialog.setFocusable(true);
        footprintEditorDialog.setVisible(true);
        BoardComponent.getUnitKeyboardListener().setComponent(unitComponent);            
        
        if(footprintEditorDialog.getResult()!=null){
            BoardMgr.getInstance().switchFootprint(footprintEditorDialog.getResult().getUnit(),pcbfootprint);
            footprintEditorDialog.getResult().release();
        } 
        clone.clear();
        copy.release();        
        footprintEditorDialog.dispose();        
    }
    /**
     *Equalize inline changed footprint with in board positioned pcbfootprint
     * @param footprint - edited footprint
     * @param pcbfootprint - pcb one that needs to be equalized
     */
    
    public void switchFootprint(Footprint footprint,PCBFootprint pcbfootprint){
        double rotate=pcbfootprint.getRotate();
        //1.unselect
        footprint.setSelected(false);
        pcbfootprint.setSelected(false);
        //2.get current position
        Point rsrc=pcbfootprint.getCenter();
        //3.clear pcbfootprint
        pcbfootprint.clear();
        //pcbfootprint.getChipText().Add(new GlyphTexture("","reference", 0, 0, Grid.MM_TO_COORD(2)));
        //pcbfootprint.getChipText().Add(new GlyphTexture("","value",8, 8, Grid.MM_TO_COORD(2))); 
        //4.transfer
        createPCBFootprint(footprint, pcbfootprint);
        //5.get new position
        Point rdst=pcbfootprint.getCenter();
        //6.go to new position
        pcbfootprint.move(rsrc.x-rdst.x,rsrc.y-rdst.y);
        pcbfootprint.setRotation(rotate, pcbfootprint.getCenter());
    }
//    
//   /**
//     * Align wire end to pad drill center
//     * @param board
//     * @param point
//     */
//    public void alignWirePointToPad(Board board,Point point){
//        for (PCBFootprint footprint : board.<PCBFootprint>getShapes(PCBFootprint.class)) {
//           Pad pad=footprint.getPin(point.x, point.y);
//               if(pad!=null){
//                   if(pad.getType()==Pad.Type.THROUGH_HOLE){
//                      point.setLocation(pad.getDrill().getX(),pad.getDrill().getY());  
//                   }else{
//                       point.setLocation(pad.getX(),pad.getY());  
//                   }
//               }
//               
//        }
//    }
///**
//     * Select track points connected to footrpint pads
//     * @param board
//     * @param chip
//     * @param bind
//     */
//    public void bindChipWirePoints(Board board, Pinaware footprint, boolean bind) {
//        //***to bind you need unselected wire ends
//        for (PCBTrack wire : board.<PCBTrack>getShapes(PCBTrack.class)) {
//            for (LinePoint wirePoint : wire.getLinePoints()) {
//                if (null != footprint.getPin(wirePoint.x, wirePoint.y)) {
//                    wirePoint.setSelected(bind);
//                }
//            }
//        }
//    }    
}
