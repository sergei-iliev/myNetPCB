package com.mynetpcb.board.popup;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.popup.AbstractPopupItemsContainer;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.event.ActionEvent;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class BoardPopupMenu extends AbstractPopupItemsContainer<BoardComponent>{
    
    private Map<String,Object>  trackMenu; 
    
    public BoardPopupMenu(BoardComponent component) {
        super(component);
        this.createTrackMenuItems();
    }
    
    
    protected void createTrackMenuItems(){
        trackMenu=new LinkedHashMap<String,Object>();
        
        JMenuItem item=new JMenuItem("Add Via"); item.setActionCommand("addvia");
        trackMenu.put("AddVia",item); 
        
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
         
        trackMenu.put("Bending",submenu);
         
        //***separator
        trackMenu.put("Separator1",null);
        
        item=new JMenuItem("Delete last point"); item.setActionCommand("DeleteLastPoint");
        trackMenu.put("DeleteLastPoint",item);
        item=new JMenuItem("Delete line"); item.setActionCommand("deleteline");
        trackMenu.put("DeleteWire",item); 
        item=new JMenuItem("Cancel"); item.setActionCommand("CancelWiring");
        trackMenu.put("CancelWiring",item); 
    }
    
    public void registerTrackPopup(MouseScaledEvent e, Shape target) {
        initializePopupMenu(e, target, trackMenu);
        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());
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
       chipMenu.remove("EditSymbol");
       chipMenu.remove("Mirror");
       chipMenu.remove("SelectPackage");
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
//            if(getTarget() instanceof PCBTrack){                
//                        Point lastPoint=((PCBTrack)getTarget()).getEndPoint(x,y);
//                        getUnitComponent().getDialogFrame().setButtonGroup(BoardComponent.TRACK_MODE);
//                        getUnitComponent().setMode(BoardComponent.TRACK_MODE);
//                        getUnitComponent().resumeLine((Trackable)getTarget(),"track", lastPoint.x, lastPoint.y);
//                        
//            }else if(getTarget() instanceof PCBLine){
//                getUnitComponent().getDialogFrame().setButtonGroup(BoardComponent.LINE_MODE);
//                getUnitComponent().setMode(BoardComponent.LINE_MODE);
//                getUnitComponent().resumeLine((Trackable)getTarget(),"line", x, y);
//                
//            }
            return;
        }
        if (e.getActionCommand().equalsIgnoreCase("disconnectwires")) {
            Shape shape=getUnitComponent().getModel().getUnit().getClickedShape(x,y,false);
            //if(shape instanceof Pinaware){
                //BoardMgr.getInstance().bindChipWirePoints(getUnitComponent().getModel().getUnit(),(Pinaware)shape,false);
            //}  
            getUnitComponent().Repaint(); 
        }
        if (e.getActionCommand().equalsIgnoreCase("connectwires")) {
            //***sure ->it is a Pinable!!!!!
            Shape shape=getUnitComponent().getModel().getUnit().getClickedShape(x,y,false);
            //if(shape instanceof Pinaware){
                //BoardMgr.getInstance().bindChipWirePoints(getUnitComponent().getModel().getUnit(),(Pinaware)shape,true);
            //}
            getUnitComponent().Repaint(); 
        }
        if(e.getActionCommand().equals("EditFootprint")){
            //BoardMgr.getInstance().openFootprintInlineEditorDialog(getUnitComponent(), (PCBFootprint) getTarget());
            getUnitComponent().Repaint();
            return;
        }
        
     super.actionPerformed(e);   
    }
}
