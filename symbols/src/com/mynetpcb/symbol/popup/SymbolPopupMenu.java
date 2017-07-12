package com.mynetpcb.symbol.popup;


import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.popup.AbstractPopupItemsContainer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.component.SymbolComponent;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


public class SymbolPopupMenu extends AbstractPopupItemsContainer<SymbolComponent>{  

    public SymbolPopupMenu(SymbolComponent component) {      
      super(component); 
    }

    @Override
    protected void createShapeMenuItems(){
        shapeMenu=new LinkedHashMap<String,Object>();  
        Map<String,JMenuItem> submenu=new LinkedHashMap<String,JMenuItem>(); 

        JMenuItem item=new JMenuItem("Left"); item.setActionCommand("RotateLeft"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));        
        submenu.put("RotateLeft",item);  
        item=new JMenuItem("Right"); item.setActionCommand("RotateRight");  item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));        
        submenu.put("RotateRight",item);  
        shapeMenu.put("Rotate",submenu);


        item=new JMenuItem("Clone"); item.setActionCommand("Clone");                                                                   
        shapeMenu.put("Clone",item);
        
        submenu=new LinkedHashMap<String,JMenuItem>(); 
        item = new JMenuItem("Left - Right");item.setActionCommand("LeftRight"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.SHIFT_MASK));
        submenu.put("LeftRight",item);
        item = new JMenuItem("Top - Bottom");item.setActionCommand("TopBottom");item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.SHIFT_MASK));
        submenu.put("TopBottom",item);       
        shapeMenu.put("Mirror",submenu);
        
        //***separator
        shapeMenu.put("Separator1",null);
        
        item=new JMenuItem("Send To Back"); item.setActionCommand("SendToBack");        
        shapeMenu.put("SendToBack",item);        

        item=new JMenuItem("Send To Front"); item.setActionCommand("SendToFront");        
        shapeMenu.put("SendToFront",item);        
        //***separator
        shapeMenu.put("Separator2",null);         
        
        item=new JMenuItem("Delete"); item.setActionCommand("Delete");        
        shapeMenu.put("Delete",item);     
        
        
    }

    public void registerShapePopup(MouseScaledEvent e,Shape target){  
        initializePopupMenu(e,target,shapeMenu);   
        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());             
    }
      
    
    public void registerBasicPopup(MouseScaledEvent e,Shape target){                     
        initializePopupMenu(e,target,basicMenu);   
        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());        
    
    }    

    public void registerUnitPopup(MouseScaledEvent e,Shape target){
        initializePopupMenu(e,target,unitMenu);
        if(ClipboardMgr.getInstance().isTransferDataAvailable(Clipboardable.Clipboard.LOCAL))
          this.setEnabled(unitMenu,"Paste",true);
        else
          this.setEnabled(unitMenu,"Paste",false);  
        
        if(getUnitComponent().getModel().getUnit().getSelectedShapes(false).size()>0)
          this.setEnabled(unitMenu,"Copy",true);
        else
          this.setEnabled(unitMenu,"Copy",false);
        
          this.show(e.getComponent(),e.getWindowX() ,e.getWindowY());
    }
    
    public void actionPerformed(ActionEvent e) {                
      //***Investigate each action command
        if(e.getActionCommand().equals("SendToBack")){
            getUnitComponent().getModel().getUnit().getShapes().remove(getTarget());
            ((List<Shape>)getUnitComponent().getModel().getUnit().getShapes()).add(0,getTarget());
            getUnitComponent().Repaint();
        }
        if (e.getActionCommand().equalsIgnoreCase("Resume")) {
            getUnitComponent().getDialogFrame().setButtonGroup(SymbolComponent.LINE_MODE);
            getUnitComponent().setMode(SymbolComponent.LINE_MODE);         
            getUnitComponent().resumeLine((Trackable)getTarget(),"line", x, y);
        }        
        if(e.getActionCommand().equals("SendToFront")){
            getUnitComponent().getModel().getUnit().getShapes().remove(getTarget());
            getUnitComponent().getModel().getUnit().getShapes().add(getTarget());
            getUnitComponent().Repaint();
        }        

        super.actionPerformed(e);
    }
}

