package com.mynetpcb.symbol.event;

import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.shape.Arc;

public class ArcStartAngleEventHandle<U extends UnitComponent,S extends Shape>  extends EventHandle<U,S>{
    
    
    public ArcStartAngleEventHandle(U component) {
        super(component);        
    }
    
    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        Arc arc=(Arc)this.getTarget();
        arc.setResizingPoint(arc.getShape().getStart());
        getComponent().Repaint();
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));  
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
    	Arc arc=(Arc)this.getTarget();
    	double new_mx = e.getX();
    	double new_my = e.getY();

        double start = (180/Math.PI*Math.atan2(new_my-arc.getCenter().y,new_mx-arc.getCenter().x));

        if(start<0){
            arc.setStartAngle(-1*(start));            
        }else{
            arc.setStartAngle(360-(start));            
        }
        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));        
        arc.setResizingPoint(arc.getShape().getStart());
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

