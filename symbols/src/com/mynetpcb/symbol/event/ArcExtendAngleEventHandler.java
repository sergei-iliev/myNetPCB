package com.mynetpcb.symbol.event;

import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.shape.Arc;


public class ArcExtendAngleEventHandler <U extends UnitComponent,S extends Shape>  extends EventHandle<U,S>{    

    
    public ArcExtendAngleEventHandler(U component) {
        super(component);
    }
    
    @Override
    protected void clear() {   
    	((Resizeable)getTarget()).setResizingPoint(null);
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        Arc arc=(Arc)this.getTarget();
        arc.setResizingPoint(arc.getShape().getEnd());
        getComponent().Repaint();           
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
        
        double extend = (180/Math.PI*Math.atan2(new_my-arc.getCenter().y,new_mx-arc.getCenter().x));

        if(extend<0){
            extend=(-1*(extend));                  
        }else{
            extend=(360-extend);         
        }
        
        //-360<extend<360 
        double extendAngle=arc.getExtendAngle();
        if(extendAngle<0){        
            if(extend-arc.getStartAngle()>0) {                
              arc.setExtendAngle(((extend-arc.getStartAngle()))-360);
            }else{
              arc.setExtendAngle(extend-arc.getStartAngle());
            }
        }else{           
            if(extend-arc.getStartAngle()>0) {
              arc.setExtendAngle(extend-arc.getStartAngle());
            }else{
              arc.setExtendAngle((360-arc.getStartAngle())+extend);
            }
        }
        
        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));        
        ((Resizeable)getTarget()).setResizingPoint(((Arc)getTarget()).getShape().getEnd());
        
        getComponent().Repaint();

    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
  
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
      
    }
    
}

