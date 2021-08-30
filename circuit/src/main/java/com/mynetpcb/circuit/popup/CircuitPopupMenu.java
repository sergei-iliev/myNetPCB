package com.mynetpcb.circuit.popup;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHBus;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.popup.AbstractPopupItemsContainer;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.event.ActionEvent;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class CircuitPopupMenu extends AbstractPopupItemsContainer<CircuitComponent> {

    public CircuitPopupMenu(CircuitComponent component) {
        super(component);
    }

    public void registerBasicPopup(MouseScaledEvent e, Shape target) {
        initializePopupMenu(e, target, basicMenu);
        this.show(e.getMouseEvent().getComponent(), e.getWindowX(), e.getWindowY());
    }

    @Override
    public void registerLineSelectPopup(MouseScaledEvent e, Shape target) {
//        if(target instanceof SCHBusPin){
//            registerShapePopup(e, target);
//        }else{   
            super.registerLineSelectPopup(e, target); 
        //}
    }
    public void registerTextureMethod(MouseScaledEvent e, Shape target){
        registerShapePopup(e, target);
    }
    public void registerChipPopup(MouseScaledEvent e, Shape target) {
        initializePopupMenu(e, target, chipMenu);
        //this.setEnabled(chipMenu, "Paste", false);
        //***is target connected to wires?
//        if (!CircuitMgr.getInstance().isWirePointToChip(getUnitComponent().getModel().getUnit(), (Pinaware) target)) {
//            this.setEnabled(chipMenu, "DisconnectWires", false);
//            this.setEnabled(chipMenu, "ConnectWires", false);
//        } else {
//            this.setEnabled(chipMenu, "DisconnectWires", true);
//            this.setEnabled(chipMenu, "ConnectWires", true);
//        }

//        if (CircuitMgr.getInstance().isConnectorToChip(getUnitComponent().getModel().getUnit(), (Pinaware) target)) {
//            this.setEnabled(chipMenu, "Bind", true);
//            this.setEnabled(chipMenu, "Unbind", true);
//        } else {
//            this.setEnabled(chipMenu, "Bind", false);
//            this.setEnabled(chipMenu, "Unbind", false);
//        }

        this.show(e.getMouseEvent().getComponent(), e.getWindowX(), e.getWindowY());

    }
    
    @Override
    protected void createLineMenuItems() {
        Map<String,JMenuItem> submenu=new LinkedHashMap<String,JMenuItem>(); 
         
        //***Wire Bending
        ButtonGroup group = new ButtonGroup();
        JMenuItem radioItem = new JRadioButtonMenuItem("Vertical To Horizontal Bending");radioItem.setActionCommand("vhbend");
        group.add(radioItem);
        submenu.put("rightbend",radioItem);

        radioItem = new JRadioButtonMenuItem("Horizontal To Vertical Bending");radioItem.setActionCommand("hvbend");
        group.add(radioItem);                                                        
        submenu.put("topbend",radioItem);
        
         radioItem = new JRadioButtonMenuItem("Default Bending");radioItem.setActionCommand("defaultbend"); radioItem.setSelected(true);    
         group.add(radioItem);                                                            
         submenu.put("defaultbend",radioItem);
         
         
         lineMenu.put("Bending",submenu); 
         
         //***separator
         lineMenu.put("Separator",null);         
         super.createLineMenuItems();
    }
    
    @Override
    protected void createUnitMenu() {
        super.createUnitMenu();
        //unitMenu.put("Separator4", null);
        //JMenuItem item = new JMenuItem("Assign board");
        //item.setActionCommand("assignboard");
        //unitMenu.put("assignboard", item);
    }

    @Override
    protected void createBlockMenuItems() {
        super.createBlockMenuItems();
//        blockMenu.put("Separator1", null);
//        //wires
//        Map<String, JMenuItem> submenu = new LinkedHashMap<String, JMenuItem>();
//        JMenuItem item = new JMenuItem("Disconnect");
//        item.setActionCommand("DisconnectWires");
//        submenu.put("DisconnectWires", item);
//        item = new JMenuItem("Connect");
//        item.setActionCommand("ConnectWires");
//        submenu.put("ConnectWires", item);
//        blockMenu.put("Wire ends", submenu);

    }

    /*
     * what if a right click on circuit canvas ->target is null
     */

    public void registerUnitPopup(MouseScaledEvent e, Shape target) {
        initializePopupMenu(e, target, unitMenu);
        if (ClipboardMgr.getInstance().isTransferDataAvailable(Clipboardable.Clipboard.LOCAL))
            this.setEnabled(unitMenu, "Paste", true);
        else
            this.setEnabled(unitMenu, "Paste", false);

        if (getUnitComponent().getModel().getUnit().getSelectedShapes().size() > 0)
            this.setEnabled(unitMenu, "Copy", true);
        else
            this.setEnabled(unitMenu, "Copy", false);

        this.show(e.getMouseEvent().getComponent(), e.getWindowX(), e.getWindowY());
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("EditSymbol")) {
            CircuitMgr.getInstance().openSymbolInlineEditorDialog(getUnitComponent(),(SCHSymbol)getTarget());       
            getUnitComponent().Repaint();
            return;
        }                
//        if (e.getActionCommand().equalsIgnoreCase("assignpackage")) {
//            FootprintMgr.getInstance().assignPackage(getUnitComponent().getDialogFrame().getParentFrame(),
//                                                     ((Packageable) getTarget()).getPackaging());
//        }
//        if (e.getActionCommand().equalsIgnoreCase("disconnectwires")) {
//            Shape shape = getUnitComponent().getModel().getUnit().getClickedShape(x, y, false);
//            if (shape instanceof Pinaware) {
//                CircuitMgr.getInstance().bindChipWirePoints(getUnitComponent().getModel().getUnit(), (Pinaware) shape,
//                                                            false);
//            }
//            getUnitComponent().Repaint();
//        }
//        if (e.getActionCommand().equalsIgnoreCase("connectwires")) {
//            //***sure ->it is a Pinable!!!!!
//            Shape shape = getUnitComponent().getModel().getUnit().getClickedShape(x, y, false);
//            if (shape instanceof Pinaware) {
//                CircuitMgr.getInstance().bindChipWirePoints(getUnitComponent().getModel().getUnit(), (Pinaware) shape,
//                                                            true);
//            }
//            getUnitComponent().Repaint();
//        }
        if (e.getActionCommand().equalsIgnoreCase("Resume")) {
            //***we keep the popup origine which is the control point itself
            //getUnitComponent().Repaint();
        }
        if (e.getActionCommand().equalsIgnoreCase("Resume")) {
            if (getTarget() instanceof SCHBus) {
                getUnitComponent().getDialogFrame().setButtonGroup(Mode.BUS_MODE);
                getUnitComponent().setMode(Mode.BUS_MODE); 
            } else {
                getUnitComponent().getDialogFrame().setButtonGroup(Mode.WIRE_MODE);
                getUnitComponent().setMode(Mode.WIRE_MODE); 
                
            }           
            getUnitComponent().resumeLine((Trackable)getTarget(),"wire", x, y);
        }


        if (e.getActionCommand().equalsIgnoreCase("FixSymbolNames")) {
            //CircuitMgr.getInstance().generateShapeNaming(getUnitComponent().getModel().getUnit());
            //getUnitComponent().Repaint();
        }

        super.actionPerformed(e);

    }

}

