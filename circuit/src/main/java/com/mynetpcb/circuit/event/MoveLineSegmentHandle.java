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
import com.mynetpcb.d2.shapes.Segment;

public class MoveLineSegmentHandle extends EventHandle<CircuitComponent,Shape>{
    //private Point startPoint;
    //private Point endPoint;
    private Segment segment;
    
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

        this.segment=((SCHWire)this.getTarget()).getSegmentClicked(e.getPoint());
        
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
	  
    	((SCHWire)this.getTarget()).alignResizingPointToGrid(this.segment.ps);
    	((SCHWire)this.getTarget()).alignResizingPointToGrid(this.segment.pe);
    	getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
    	getComponent().Repaint();	 		         		  
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {

        ((SCHWire)this.getTarget()).moveSegment(this.segment,e.getX(),e.getY());                
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


