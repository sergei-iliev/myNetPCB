package com.mynetpcb.symbol.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.shape.Arc;

public class ArcStartAngleEventHandle<U extends UnitComponent,S extends Shape>  extends EventHandle<U,S>{
    
    double centerX;
    double centerY;
    
    public ArcStartAngleEventHandle(U component) {
        super(component);        
    }
    
    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        Arc arc=(Arc)this.getTarget();
        centerX=arc.getCenter().x;
        centerY=arc.getCenter().y;      
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));  
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
    	double new_mx = e.getX();
    	double new_my = e.getY();
        
        Arc arc=(Arc)this.getTarget();
        
        
        double start = (180/Math.PI*Math.atan2(new_my-centerY,new_mx-centerX));

        if(start<0){
            arc.setStartAngle(-1*(start));            
        }else{
            arc.setStartAngle(360-(start));            
        }

        
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

    }
}

