package com.mynetpcb.pad.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Arc;

/*
 * Arc two point resizing
 */
public class ArcResizeEventHandle <U extends UnitComponent,S extends Shape>  extends EventHandle<U,S>{

	private boolean isStartPoint;
	
    public ArcResizeEventHandle(U component) {
        super(component);        
    }
    
	@Override
	public void mouseScaledPressed(MouseScaledEvent e) {
		this.mx=e.getX();
		this.my=e.getY();
		this.isStartPoint=((Arc)this.getTarget()).isStartAnglePointClicked(e.getX(), e.getY());
	
	}

	@Override
	public void mouseScaledReleased(MouseScaledEvent e) {
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));  
	}

	@Override
	public void mouseScaledDragged(MouseScaledEvent e) {			    
        int new_mx = e.getX();
        int new_my = e.getY();
		((Arc)this.getTarget()).resizeStartEndPoint(new_mx - mx, new_my - my, isStartPoint);
        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
        mx = new_mx;
        my = new_my;
		this.getComponent().Repaint();
		
	}

	@Override
	public void mouseScaledMove(MouseScaledEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doubleScaledClick(MouseScaledEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clear() {		
		
	}

}
