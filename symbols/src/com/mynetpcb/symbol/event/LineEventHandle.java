package com.mynetpcb.symbol.event;


import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.component.SymbolComponent;

import java.awt.Point;

import javax.swing.SwingUtilities;


public class LineEventHandle extends EventHandle<SymbolComponent,Shape>{
    
    public LineEventHandle(SymbolComponent component) {
        super(component);
    }
    
    @Override
    protected void Clear() {   
    }
    @Override
    public void Attach() {        
        super.Attach();
        getComponent().getLineBendingProcessor().Initialize((Trackable)getTarget());
    }
    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(SwingUtilities.isRightMouseButton(e)){           
            getComponent().getPopupMenu().registerLinePopup(e,getTarget());  
            return;
        }

        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true); 
        
        Point p;
               
        if(getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)){
            p=getComponent().getModel().getUnit().getGrid().positionOnGrid(e.getX(),e.getY()); 
            getComponent().getLineBendingProcessor().setGridAlignable(true);
        }else{
            p=new Point(e.getX(),e.getY());
            getComponent().getLineBendingProcessor().setGridAlignable(false);
        }
        
        
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
        getComponent().getLineBendingProcessor().Release();  
        getTarget().setSelected(false);
        getComponent().getEventMgr().resetEventHandle();
        getComponent().Repaint();
    }
    
    @Override
    public void resizingEvent(){
        getComponent().Repaint();        
    } 
  
    public void Detach(){
        if(getTarget()!=null){
          if(getComponent().getLineBendingProcessor().getLine()!=null)
              getComponent().getLineBendingProcessor().Release(); 
        }
        super.Detach();     
     }
}

