package com.mynetpcb.core.dialog.load;

import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.pad.Packaging;

import java.awt.Dialog;
import java.awt.Window;

import javax.swing.JDialog;

public abstract class AbstractLoadDialog extends JDialog{
   
    public AbstractLoadDialog(Window owner, String title, Dialog.ModalityType modalityType){
        super(owner,title,modalityType);
    }
    
   public abstract UnitContainer getSelectedModel();
   
   
public static class Builder{
   protected String caption;
   protected boolean enabled;
   protected Window window;
   protected Packaging packaging;
   
   
   public Builder setWindow(Window window){
        this.window=window;
        return this;
   }
   
   public Builder setCaption(String caption){
       this.caption=caption;
       return this;
   }
   
   public Builder setEnabled(boolean enabled){
       this.enabled=enabled;
       return this;
   }
   
   public Builder setPackaging(Packaging packaging){
        this.packaging=packaging;
        return this;
   }
   
   
   public AbstractLoadDialog build(){
       return null;       
   }
   
}
}
