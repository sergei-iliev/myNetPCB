package com.mynetpcb.circuit.event;


import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHJunction;
import com.mynetpcb.circuit.shape.SCHWire;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;


public class LineEventHandle extends EventHandle<CircuitComponent,Shape>{
    
    public LineEventHandle(CircuitComponent component) {
        super(component);
    }
    
    @Override
    protected void Clear() {   
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(SwingUtilities.isRightMouseButton(e)){           
            getComponent().getPopupMenu().registerLinePopup(e,getTarget());  
            return;
        }

        getComponent().getLineBendingProcessor().Initialize((Trackable)getTarget());
        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true); 
        
        //points are always gridable on circuit
        Point p=getComponent().getModel().getUnit().getGrid().positionOnGrid(e.getX(),e.getY()); 
        getComponent().getLineBendingProcessor().setGridAlignable(true);
        
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE)); 
        
        boolean justcreated=((Trackable)getTarget()).getLinePoints().size()==1; 
        
        if(getComponent().getLineBendingProcessor().addLinePoint(p)){
            if(justcreated){
                getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.CREATE_MEMENTO));   
                getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));    
            }
            if(((Trackable)getTarget()).getLinePoints().size()>=2){
            //this could be a click over wire ->accomodate junction
                Trackable  track=CircuitMgr.getInstance().getClickedLine(getComponent().getModel().getUnit(), p.x, p.y,(Trackable)getTarget());
                //is press on track
                if(track!=null&&(track.getClass()==SCHWire.class))
                 //is press on junction
                 if(!getComponent().getModel().getUnit().getShapeAt(p.x, p.y,SCHJunction.class).isPresent()){
                  //create junction
                  SCHJunction junction = new SCHJunction();
                  junction.setLocation(p.x,p.y);               
                  getComponent().getModel().getUnit().Add(junction);  
                  
                  getComponent().getModel().getUnit().registerMemento(junction.getState(MementoType.CREATE_MEMENTO));
                  getComponent().getModel().getUnit().registerMemento(junction.getState(MementoType.MOVE_MEMENTO));    
                 }
                  getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));    
                 
            }            
        }
        getComponent().Repaint(); 
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
    
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
        getComponent().getLineBendingProcessor().moveLinePoint(e.getX(),e.getY());  
            
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));

        getComponent().Repaint();    
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
        getComponent().getLineBendingProcessor().Release();  
        getTarget().setSelected(false);
        getComponent().getEventMgr().resetEventHandle();
        getComponent().Repaint();
    }
    
    @Override
    public void resizingEvent(){
        getComponent().Repaint();        
    } 
    
    @Override
    public void keyPressed(KeyEvent keyEvent) { 
        if(keyEvent.getKeyCode()==KeyEvent.VK_SPACE){   
            LineBendingProcessor lineBendingProcessor=getComponent().getBendingProcessorFactory().resolve(getComponent().getLineBendingProcessor());
            getComponent().setLineBendingProcessor(lineBendingProcessor);
        }
    }
    public void Detach(){
        if(getTarget()!=null){
          if(getComponent().getLineBendingProcessor().getLine()!=null)
              getComponent().getLineBendingProcessor().Release(); 
        }
        super.Detach();     
     }
    
}


