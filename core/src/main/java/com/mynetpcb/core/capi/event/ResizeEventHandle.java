package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;


import com.mynetpcb.d2.shapes.Point;

import javax.swing.SwingUtilities;

public class ResizeEventHandle <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    
    private Point targetPoint;
    
    public ResizeEventHandle(U component) {
            super(component);
    }
    
    @Override
    protected void clear() {
    	((Resizeable)getTarget()).setResizingPoint(null);
        targetPoint=null;        
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if (SwingUtilities.isRightMouseButton(e.getMouseEvent())) {
                if (getTarget() instanceof Trackable){
                    getComponent().getPopupMenu().registerLineSelectPopup(e, getTarget());
                }
        }
        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true);
        mx = e.getX();
        my = e.getY();
        
        targetPoint=(((Resizeable)getTarget()).isControlRectClicked(e.getX(),e.getY(),getComponent().getViewportWindow()));
        ((Resizeable)getTarget()).setResizingPoint(targetPoint);
        //***update PropertiesPanel  X,Y         
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));  
        getComponent().Repaint();    
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        //***snap to grid
        if((Boolean)getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)==true){
         ((Resizeable)getTarget()).alignResizingPointToGrid(targetPoint);
         getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
          getComponent().Repaint(); 
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));  
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
        double new_mx = e.getX();
        double new_my = e.getY();
        
        ((Resizeable)getTarget()).resize((new_mx - mx),(new_my - my),targetPoint);
        // update our data
        mx = new_mx;
        my = new_my;
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
    
}
