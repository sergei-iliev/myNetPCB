package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.d2.shapes.Point;


public class MeasureEventHandle  <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    
    
    public MeasureEventHandle(U component) {
            super(component);
    }

    @Override
    protected void clear() {
 
    }
    
    @Override
    public void detach() {
        getComponent().getModel().getUnit().getRuler().setResizingPoint(null);
        super.detach();
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
        double new_mx = e.getX();
        double new_my = e.getY();
        getComponent().getModel().getUnit().getRuler().resize((int)(new_mx - mx),(int)(new_my - my),null);
        // update our data
        mx = new_mx;
        my = new_my;
        
        getComponent().Repaint();                 
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {

    }
}
