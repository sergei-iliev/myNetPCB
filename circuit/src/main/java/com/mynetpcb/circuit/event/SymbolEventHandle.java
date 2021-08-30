package com.mynetpcb.circuit.event;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

public class SymbolEventHandle extends EventHandle<CircuitComponent,Shape>{
    

    
    public SymbolEventHandle(CircuitComponent component) {
        super(component);
    }
    

    
    public void mouseScaledPressed(MouseScaledEvent e) {
        if (SwingUtilities.isRightMouseButton(e.getMouseEvent())) {                       
            getComponent().getModel().getUnit().setSelected(false);  
            getTarget().setSelected(true);
                              
            getComponent().Repaint();            
            getComponent().getPopupMenu().registerChipPopup(e,getTarget());            
            return;
        }
        if ((e.getMouseEvent().getModifiers() & ActionEvent.CTRL_MASK) ==
            ActionEvent.CTRL_MASK) {
            getComponent().getModel().getUnit().setSelected(getTarget().getUUID(),
                                                 !getTarget().isSelected());
                                  
            
            this.ctrlButtonPress = true;
            getComponent().Repaint();
            return;
        }  

        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true);  
        
        mx = e.getX();
        my = e.getY();
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();

    }

    public void mouseScaledReleased(MouseScaledEvent e) {
        this.getTarget().alignToGrid(true);
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO)); 
        getComponent().Repaint();

    }

    public void mouseScaledDragged(MouseScaledEvent e) {
        double new_mx = e.getX();
        double new_my = e.getY();


        getTarget().move(new_mx - mx, new_my - my);

        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
        // update our data
        mx = new_mx;
        my = new_my;

        getComponent().Repaint();
    }
    @Override
    public void mouseScaledMove(MouseScaledEvent e) {

    }

    public void doubleScaledClick(MouseScaledEvent e) {
       //CircuitMgr.getInstance().openSymbolInlineEditorDialog(getComponent(),(SCHSymbol)getTarget());
       getComponent().Repaint(); 
    }
    @Override
    protected void clear(){   
               
    }
}
