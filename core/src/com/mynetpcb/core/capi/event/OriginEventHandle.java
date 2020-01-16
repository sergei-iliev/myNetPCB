package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;


public class OriginEventHandle<U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    public OriginEventHandle(U component) {
        super(component);
    }
    
    @Override
    protected void clear() {
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        mx = e.getX();
        my = e.getY(); 
            
        getComponent().getModel().getUnit().getCoordinateSystem().reset(e.getX(),e.getY());   
        getComponent().getModel().getUnit().setSelected(false);
        getComponent().getModel().fireUnitEvent(new UnitEvent(null, UnitEvent.PROPERTY_CHANGE));
        getComponent().getDialogFrame().setButtonGroup(Mode.COMPONENT_MODE);
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {

        getComponent().getModel().getUnit().getCoordinateSystem().alignToGrid((Boolean)getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)); 
        getComponent().getModel().fireUnitEvent(new UnitEvent(null, UnitEvent.PROPERTY_CHANGE));
        getComponent().getDialogFrame().setButtonGroup(Mode.COMPONENT_MODE);
        getComponent().setMode(Mode.COMPONENT_MODE);
        getComponent().Repaint();
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {   
        this.mouseScaledMove(e);
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
        int new_mx = e.getX();
        int new_my = e.getY();
        
        getComponent().getModel().getUnit().getCoordinateSystem().move((new_mx - mx), (new_my - my));
        getComponent().getModel().fireUnitEvent(new UnitEvent(null, UnitEvent.PROPERTY_CHANGE));
        // update our data
        mx = new_mx;
        my = new_my;    
        e.consume(); 
        getComponent().Repaint();     
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
    }
    public void Attach() {
        super.attach();
        getComponent().getModel().getUnit().getCoordinateSystem().reset(0,0);   
    }


}


