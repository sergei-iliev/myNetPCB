package com.mynetpcb.pad.event;

import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Arc;

public class ArcMidPointEventHandle<U extends UnitComponent,S extends Shape>  extends EventHandle<U,S>{

    
    public ArcMidPointEventHandle(U component) {
        super(component);        
    }
    
    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {

    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
       
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));  
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {    
        Arc arc=(Arc)this.getTarget();
        

        arc.resize((int)e.getX(),(int)e.getY(),null);
        
        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
        
        
        getComponent().Repaint();
        
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
  
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
      
    }


    @Override
    protected void clear() {
        ((Resizeable)getTarget()).setResizingPoint(null);
    }
}


