package com.mynetpcb.symbol.event;

import com.mynetpcb.core.capi.Reshapeable;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.component.SymbolComponent;


public class ReshapeEventHandle extends EventHandle<SymbolComponent,Shape> {
    
    private int targetid;
    
    public ReshapeEventHandle(SymbolComponent component) {
        super(component);
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true);
        mx = e.getX();
        my = e.getY();
        
        targetid=((Reshapeable)getTarget()).getReshapeRectID(e.getX(),e.getY());
        
        //***Undo processor
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO)); 
        
        getComponent().Repaint();

    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        //***Undo processor
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));  
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
        int new_mx = e.getX();
        int new_my = e.getY();
      
        
        ((Reshapeable)getTarget()).Reshape(new_mx - mx, new_my - my,targetid);
        // update our data
        mx = new_mx;
        my = new_my;
        
        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
        
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

