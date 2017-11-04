package com.mynetpcb.pad.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.shape.Arc;


public class ArcExtendAngleEventHandler <U extends UnitComponent,S extends Shape>  extends EventHandle<U,S>{
    
    private Arc arc;

    
    public ArcExtendAngleEventHandler(U component) {
        super(component);
    }
    
    @Override
    public void Attach() {        
        super.Attach();
        arc=(Arc)this.getTarget();
    }
    @Override
    protected void Clear() {   
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
        int new_mx = e.getX();
        int new_my = e.getY();
        
        int centerX=arc.getCenterX();
        int centerY=arc.getCenterY();
        
        
        double extend = (180/Math.PI*Math.atan2(new_my-centerY,new_mx-centerX));

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
        
        
        getComponent().Repaint();
        
        e.consume(); 
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
  
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
      
    }
    
}
