package com.mynetpcb.pad.popup;


import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.popup.AbstractPopupItemsContainer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.dialog.save.FootprintSaveDialog;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;


public class FootprintPopupMenu extends AbstractPopupItemsContainer<FootprintComponent>{
    
    public FootprintPopupMenu(FootprintComponent component) {      
      super(component);
    }
    @Override
    protected void createUnitMenu(){
       super.createUnitMenu();
       if(Configuration.get().isIsOnline()){
         unitMenu.put("Separator4",null);
         JMenuItem item=new JMenuItem("Add to local library");item.setActionCommand("addtolocallibrary");
         unitMenu.put("addtolocallibrary",item); 
       }
    }
    
    public void registerBasicPopup(MouseScaledEvent e,Shape target){                     
        initializePopupMenu(e,target,basicMenu);   
        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());            
    }   
    
    public void registerShapePopup(MouseScaledEvent e,Shape target){  
        initializePopupMenu(e,target,shapeMenu);   
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
        if(e.getActionCommand().equals("addtolocallibrary")){
          new FootprintSaveDialog(this.getUnitComponent().getDialogFrame().getParentFrame(), this.getUnitComponent(),false).build();
          return;  
        }
        if (e.getActionCommand().equalsIgnoreCase("Resume")) {
            getUnitComponent().getDialogFrame().setButtonGroup(FootprintComponent.LINE_MODE);
            getUnitComponent().setMode(FootprintComponent.LINE_MODE);         
            getUnitComponent().resumeLine((Trackable)getTarget(),"line", x, y);
        }  
        super.actionPerformed(e);      
        
    }
}

