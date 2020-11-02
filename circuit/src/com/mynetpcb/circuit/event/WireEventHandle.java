package com.mynetpcb.circuit.event;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Point;

import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

public class WireEventHandle extends EventHandle<CircuitComponent,Shape>{
    
    public WireEventHandle(CircuitComponent component) {
        super(component);
    }
    
    
    @Override
    public void attach() {        
        super.attach();
        getComponent().getLineBendingProcessor().initialize((Trackable)getTarget());
    }
    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(SwingUtilities.isRightMouseButton(e)){           
            getComponent().getPopupMenu().registerLinePopup(e,getTarget());  
            return;
        }

        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true); 
        
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
        getComponent().getLineBendingProcessor().release();  
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
              getComponent().getLineBendingProcessor().release(); 
        }
        super.detach();     
     }

    @Override
    protected void clear() {
        
    }
}


