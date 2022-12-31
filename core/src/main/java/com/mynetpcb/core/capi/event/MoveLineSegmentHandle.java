package com.mynetpcb.core.capi.event;

import javax.swing.SwingUtilities;

import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.line.Segmentable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Point;

public class MoveLineSegmentHandle <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    
    private MoveLineSegmentAdapter adapter;
    
    public MoveLineSegmentHandle(U component) {
        super(component);
    }
    
    

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        this.getComponent().getModel().getUnit().setSelected(false);
        this.getTarget().setSelected(true);        
        this.getComponent().Repaint();
    	
    	if(SwingUtilities.isRightMouseButton(e.getMouseEvent())){
    		this.getComponent().getPopupMenu().registerLineSelectPopup(e,this.getTarget());           
            return;
        }         
        
        var segment=((Segmentable)this.getTarget()).getSegmentClicked(e.getPoint());        
        this.adapter=new  MoveLineSegmentAdapter((Segmentable)this.getTarget(),segment);
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
    	if(getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)==Boolean.TRUE){
          ((Resizeable)getTarget()).alignResizingPointToGrid(this.adapter.segment.ps);
          ((Resizeable)getTarget()).alignResizingPointToGrid(this.adapter.segment.pe);	      
		}
    	this.adapter.validateNonZeroVector();    	
    	getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
    	this.getComponent().Repaint();	 
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
        this.adapter.moveSegment(new Point(e.getX(),e.getY()));   
    	this.getComponent().Repaint();     
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
