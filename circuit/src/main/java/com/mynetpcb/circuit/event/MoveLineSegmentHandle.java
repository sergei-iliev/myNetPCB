package com.mynetpcb.circuit.event;

import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHWire;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Point;

public class MoveLineSegmentHandle extends EventHandle<CircuitComponent,Shape>{
    private Point startPoint;
    private Point endPoint;
    
    public MoveLineSegmentHandle(CircuitComponent component) {
        super(component);
    }
    
    

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(SwingUtilities.isRightMouseButton(e.getMouseEvent())){           
            getComponent().getPopupMenu().registerLinePopup(e,getTarget());  
            return;
        }

        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true); 

        var arr=((SCHWire)this.getTarget()).getSegmentClicked(e.getPoint());
        this.startPoint=arr[0];
        this.endPoint=arr[1];
        
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
	  
    	((SCHWire)this.getTarget()).alignResizingPointToGrid(this.startPoint);
    	((SCHWire)this.getTarget()).alignResizingPointToGrid(this.endPoint);
    	getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
    	getComponent().Repaint();	 		         		  
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {

        ((SCHWire)this.getTarget()).moveSegment(this.startPoint,this.endPoint,e.getX(),e.getY());                
    	getComponent().Repaint();
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
        
 
    }



	@Override
	protected void clear() {
		
		
	}



	@Override
	public void doubleScaledClick(MouseScaledEvent e) {
		
		
	}
    



    


}


