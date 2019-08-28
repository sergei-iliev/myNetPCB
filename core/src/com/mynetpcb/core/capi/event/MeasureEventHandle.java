package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.Point;

public class MeasureEventHandle  <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    
    
    public MeasureEventHandle(U component) {
            super(component);
    }

    @Override
    protected void Clear() {
 
    }
    
    @Override
    public void Detach() {
        getComponent().getModel().getUnit().getRuler().setResizingPoint(null);
        super.Detach();
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {        
        getComponent().getModel().getUnit().getRuler().setResizingPoint(new Point(e.getX(),e.getY()));
        mx = e.getX();
        my = e.getY();
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {

    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {

    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
        int new_mx = e.getX();
        int new_my = e.getY();
        getComponent().getModel().getUnit().getRuler().Resize(new_mx - mx, new_my - my,null);
        // update our data
        mx = new_mx;
        my = new_my;
        
        getComponent().Repaint();
        e.consume();          
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {

    }
}
