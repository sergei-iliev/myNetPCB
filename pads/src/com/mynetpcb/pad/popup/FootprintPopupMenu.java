package com.mynetpcb.pad.popup;


import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.popup.AbstractPopupItemsContainer;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.dialog.save.FootprintSaveDialog;
import com.mynetpcb.pad.unit.FootprintMgr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


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
        shapeMenu.put("Separator2",null);  
        
        item=new JMenuItem("Send To Back"); item.setActionCommand("SendToBack");                                                                   
        shapeMenu.put("SendToBack",item);
        item=new JMenuItem("Bring To Front"); item.setActionCommand("BringToFront");                                                                   
        shapeMenu.put("BringToFront",item);
        
        //***separator
        shapeMenu.put("Separator3",null);         
        
        item=new JMenuItem("Delete"); item.setActionCommand("Delete");        
        shapeMenu.put("Delete",item);     
                
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
        
        if(getUnitComponent().getModel().getUnit().getSelectedShapes().size()>0)
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
        if(e.getActionCommand().equalsIgnoreCase("sendtoback")){
           FootprintMgr.getInstance().sendToBack(getUnitComponent().getModel().getUnit().getShapes(),getTarget());
           getUnitComponent().Repaint();            
           return;
        }
        if(e.getActionCommand().equalsIgnoreCase("bringtofront")){
           FootprintMgr.getInstance().bringToFront(getUnitComponent().getModel().getUnit().getShapes(),getTarget());
           getUnitComponent().Repaint();            
           return;
        }
        if (e.getActionCommand().equalsIgnoreCase("Resume")) {
            getUnitComponent().getDialogFrame().setButtonGroup(Mode.LINE_MODE);
            getUnitComponent().setMode(Mode.LINE_MODE);         
            getUnitComponent().resumeLine((Trackable)getTarget(),"line", x, y);
        }  
        super.actionPerformed(e);      
        
    }
}

