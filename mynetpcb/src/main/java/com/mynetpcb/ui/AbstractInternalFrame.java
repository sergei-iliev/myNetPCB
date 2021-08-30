package com.mynetpcb.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.JInternalFrame;

public abstract class AbstractInternalFrame extends JInternalFrame implements VetoableChangeListener{
    
    public AbstractInternalFrame(String name) {
        super(name,
              true, //resizable
              true, //closable
              true, //maximizable
              true);//
              this.addVetoableChangeListener(this);

    }
    public abstract boolean exit();
    
    public abstract boolean isChanged();
    
    @Override
    public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
        if(event.getPropertyName().equals(IS_CLOSED_PROPERTY)||event.getPropertyName().equals(IS_ICON_PROPERTY)){
            if(!exit()){ 
               throw new PropertyVetoException("Cancelled",null);
            }
        }
    }

}
