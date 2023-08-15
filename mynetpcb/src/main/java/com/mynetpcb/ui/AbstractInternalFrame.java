package com.mynetpcb.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;

public abstract class AbstractInternalFrame extends JInternalFrame implements VetoableChangeListener{
    
    public AbstractInternalFrame(String name) {
        super(name,
              true, //resizable
              true, //closable
              true, //maximizable
              true);//
              this.addVetoableChangeListener(this);

    }
    public <C extends UnitComponent<Unit<Shape>, Shape, UnitContainer<Unit<Shape>, Shape>>> C getUnitComponent(){
    	return null;
    }
    
    public abstract boolean exit();
    
    public abstract boolean isChanged();
    /**
     * close project
     */
    public void Close() {
        if(getUnitComponent().getModel().isChanged()){
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "There is a changed element.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {
                return;
            }
        }
        getUnitComponent().clear();
        getUnitComponent().fireContainerEvent(new ContainerEvent(null, ContainerEvent.DELETE_CONTAINER));
    
        getUnitComponent().componentResized(null);
        getUnitComponent().revalidate();
        getUnitComponent().Repaint(); 
    }
    @Override
    public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
        if(event.getPropertyName().equals(IS_CLOSED_PROPERTY)||event.getPropertyName().equals(IS_ICON_PROPERTY)){
            if(!exit()){ 
               throw new PropertyVetoException("Cancelled",null);
            }
        }
    }

}
