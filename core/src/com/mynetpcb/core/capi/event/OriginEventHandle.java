package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;


public class OriginEventHandle<U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    public OriginEventHandle(U component) {
        super(component);
    }
    
    @Override
    protected void Clear() {
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(SwingUtilities.isRightMouseButton(e)){ 
            //escape
            getComponent().getModel().getUnit().getCoordinateSystem().Reset(0,0);    
            getComponent().getModel().fireUnitEvent(new UnitEvent(null, UnitEvent.PROPERTY_CHANGE));
            getComponent().getDialogFrame().setButtonGroup(getComponent().COMPONENT_MODE);
            getComponent().setMode(getComponent().COMPONENT_MODE);
            getComponent().Repaint();
            return;
        }else{
            getComponent().getModel().getUnit().getCoordinateSystem().Reset(e.getX(),e.getY()); 
        }

        mx = e.getX();
        my = e.getY(); 
        
        getComponent().getModel().getUnit().setSelected(false);
        getComponent().getModel().fireUnitEvent(new UnitEvent(null, UnitEvent.PROPERTY_CHANGE));

    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {

        getComponent().getModel().getUnit().getCoordinateSystem().alignToGrid((Boolean)getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)); 
        getComponent().getModel().fireUnitEvent(new UnitEvent(null, UnitEvent.PROPERTY_CHANGE));
        getComponent().getDialogFrame().setButtonGroup(getComponent().COMPONENT_MODE);
        getComponent().setMode(getComponent().COMPONENT_MODE);
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
        
        getComponent().getModel().getUnit().getCoordinateSystem().Move((new_mx - mx), (new_my - my));
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

    @Override
    public boolean forwardKeyPress(KeyEvent e){
      getComponent().getModel().getUnit().getCoordinateSystem().Reset(0,0); 
      return false;  
    }
    public void Attach() {
        super.Attach();
        getComponent().getModel().getUnit().getCoordinateSystem().Reset(0,0);   
    }


}


