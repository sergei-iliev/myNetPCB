package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

public class MoveEventHandle <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    public MoveEventHandle(U component) {
        super(component);
    }
    
    public void mouseScaledPressed(MouseScaledEvent e) {
        if ((e.getModifiers() & ActionEvent.CTRL_MASK) == 
            ActionEvent.CTRL_MASK) {
            getComponent().getModel().getUnit().setSelected(getTarget().getUUID(), !getTarget().isSelected());
            getComponent().Repaint(); 
            this.ctrlButtonPress = true;
            return;
        }
        //***is this a right click popup request?
        if (SwingUtilities.isRightMouseButton(e)) {
            if (getTarget() instanceof Trackable){
                getComponent().getPopupMenu().registerLineSelectPopup(e, getTarget());
            }else{
                getComponent().getPopupMenu().registerShapePopup(e, getTarget());
            } 
        }
        mx = e.getX();
        my = e.getY();
        
        //***Undo processor
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        //***one time init
        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true);
        
        getComponent().Repaint();
    }    
    
    public void mouseScaledReleased(MouseScaledEvent e) {
        getTarget().alignToGrid((Boolean)getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE));
        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
        //***Undo processor
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));           
        getComponent().Repaint();
    }    
    
    public void mouseScaledDragged(MouseScaledEvent e) {  
        int new_mx = e.getX();
        int new_my = e.getY();


        getTarget().Move(new_mx - mx, new_my - my);

        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
        // update our data
        mx = new_mx;
        my = new_my;

        getComponent().Repaint();
        e.consume();
    }
    
    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
    }
    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
    }
    @Override
    protected void Clear() {
    
    }    
    
}
