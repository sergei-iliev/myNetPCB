package com.mynetpcb.board.popup;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.BoardMgr;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.popup.AbstractPopupItemsContainer;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.event.ActionEvent;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class BoardPopupMenu extends AbstractPopupItemsContainer<BoardComponent>{
    
    public BoardPopupMenu(BoardComponent component) {
        super(component);
    }
    
    @Override
    protected void createLineMenuItems(){       
       JMenuItem item=new JMenuItem("Add Via"); item.setActionCommand("addvia");
       lineMenu.put("AddVia",item); 
       Map<String,JMenuItem> submenu=new LinkedHashMap<String,JMenuItem>(); 
         
        //***Wire Bending
       ButtonGroup group = new ButtonGroup();
       JMenuItem radioItem = new JRadioButtonMenuItem("Line Slope Bending");radioItem.setActionCommand("lineslopebend");
       group.add(radioItem);
       submenu.put("lineslopebend",radioItem);

       radioItem = new JRadioButtonMenuItem("Slope Line Bending");radioItem.setActionCommand("slopelinebend");
       group.add(radioItem);                                                        
       submenu.put("slopelinebend",radioItem);
        
       radioItem = new JRadioButtonMenuItem("Default Bending");radioItem.setActionCommand("defaultbend"); radioItem.setSelected(true);    
       group.add(radioItem);                                                            
       submenu.put("defaultbend",radioItem);
         
       lineMenu.put("Bending",submenu); 
         
       //***separator
       lineMenu.put("Separator1",null); 
       super.createLineMenuItems();
    }
    
    @Override
    public void registerLinePopup(MouseScaledEvent mouseScaledEvent, Shape target) {        
        super.registerLinePopup(mouseScaledEvent, target);
        if(target instanceof PCBLine){
          super.setEnabled("Bending",false);
          super.setEnabled("addvia",false);
        }else{
          super.setEnabled("Bending",true);
          super.setEnabled("addvia",true);            
        }
    }
    @Override
    protected void createBlockMenuItems(){
        super.createBlockMenuItems();    
        blockMenu.put("Separator1",null);
        //wires
        Map<String,JMenuItem> submenu=new LinkedHashMap<String,JMenuItem>(); 
        JMenuItem item=new JMenuItem("Disconnect");item.setActionCommand("DisconnectWires");
        submenu.put("DisconnectWires",item); 
        item=new JMenuItem("Connect");item.setActionCommand("ConnectWires");
        submenu.put("ConnectWires",item); 
        blockMenu.put("Wire ends",submenu);       
        
    }
    
    @Override
    protected void createChipMenuItems(){       
       JMenuItem item=new JMenuItem("Edit Footprint"); item.setActionCommand("EditFootprint");
       chipMenu.put("Edit Footprint",item);        
       super.createChipMenuItems();       
       chipMenu.remove("SelectPackege");
       chipMenu.remove("Separator");
       chipMenu.remove("ChildConnectors");
    }

    public void registerChipPopup(MouseScaledEvent e, Shape target) {
        initializePopupMenu(e, target, chipMenu);
        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());
    }
    
    public void registerUnitPopup(MouseScaledEvent e, Shape target) {
        initializePopupMenu(e, target, unitMenu);
        if (ClipboardMgr.getInstance().isTransferDataAvailable(Clipboardable.Clipboard.LOCAL))
            this.setEnabled(unitMenu, "Paste", true);
        else
            this.setEnabled(unitMenu, "Paste", false);

        if (getUnitComponent().getModel().getUnit().getSelectedShapes(true).size() > 0)
            this.setEnabled(unitMenu, "Copy", true);
        else
            this.setEnabled(unitMenu, "Copy", false);

        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Resume")) {
            if(getTarget() instanceof PCBTrack){
                        getUnitComponent().getDialogFrame().setButtonGroup(BoardComponent.TRACK_MODE);
                        getUnitComponent().setMode(BoardComponent.TRACK_MODE);
                        getUnitComponent().resumeLine((Trackable)getTarget(),"track", x, y);
                        
            }else if(getTarget() instanceof PCBLine){
                getUnitComponent().getDialogFrame().setButtonGroup(BoardComponent.LINE_MODE);
                getUnitComponent().setMode(BoardComponent.LINE_MODE);
                getUnitComponent().resumeLine((Trackable)getTarget(),"line", x, y);
                
            }
            return;
        }
        if (e.getActionCommand().equalsIgnoreCase("disconnectwires")) {
            Shape shape=getUnitComponent().getModel().getUnit().getClickedShape(x,y,false);
            if(shape instanceof Pinaware){
                BoardMgr.getInstance().bindChipWirePoints(getUnitComponent().getModel().getUnit(),(Pinaware)shape,false);
            }  
            getUnitComponent().Repaint(); 
        }
        if (e.getActionCommand().equalsIgnoreCase("connectwires")) {
            //***sure ->it is a Pinable!!!!!
            Shape shape=getUnitComponent().getModel().getUnit().getClickedShape(x,y,false);
            if(shape instanceof Pinaware){
                BoardMgr.getInstance().bindChipWirePoints(getUnitComponent().getModel().getUnit(),(Pinaware)shape,true);
            }
            getUnitComponent().Repaint(); 
        }
        if(e.getActionCommand().equals("EditFootprint")){
            BoardMgr.getInstance().openFootprintInlineEditorDialog(getUnitComponent(), (PCBFootprint) getTarget());
            //create Footprint
//            FootprintContainer copy=new FootprintContainer();
//            try {
//                copy.Add(BoardMgr.getInstance().createFootprint(pcbfootprint.clone()));
//            } catch (CloneNotSupportedException f) {
//                f.printStackTrace(System.out);
//            }
//            //center the copy
//            int x=(int)copy.getUnit().getBoundingRect().getCenterX();
//            int y=(int)copy.getUnit().getBoundingRect().getCenterY();
//            BoardMgr.getInstance().moveBlock(copy.getUnit().getShapes(), (copy.getUnit().getWidth()/2)-x, (copy.getUnit().getHeight()/2)-y);
//            BoardMgr.getInstance().alignBlock(copy.getUnit().getGrid(),copy.getUnit().getShapes());
//            
//            FootprintInlineEditorDialog footprintEditorDialog =
//                new FootprintInlineEditorDialog(getUnitComponent().getDialogFrame().getParentFrame(), "Footprint Inline Editor",copy);
//            footprintEditorDialog.pack();
//            footprintEditorDialog.setLocationRelativeTo(null); //centers on screen
//            footprintEditorDialog.setFocusable(true);
//            footprintEditorDialog.setVisible(true);
//            BoardComponent.getUnitKeyboardListener().setComponent(getUnitComponent());            
//            
//            if(footprintEditorDialog.getResult()!=null){
//                BoardMgr.getInstance().switchFootprint(footprintEditorDialog.getResult().getUnit(),pcbfootprint);
//            }    
//            copy.Release();
//            footprintEditorDialog.dispose();
            getUnitComponent().Repaint();
            return;
        }
        
     super.actionPerformed(e);   
    }
}
