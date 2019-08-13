package com.mynetpcb.board.event;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.Point;

import javax.swing.SwingUtilities;

public class CopperAreaEventHandle  extends EventHandle<BoardComponent,Shape>{
    
    public CopperAreaEventHandle(BoardComponent component) {
        super(component);
    }

    @Override
    protected void Clear() {

    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(SwingUtilities.isRightMouseButton(e)){           
            return;
        }
        getComponent().getModel().getUnit().setSelected(false);
        getTarget().setSelected(true); 
        
        Point p;      
        if(getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)){
            p=getComponent().getModel().getUnit().getGrid().positionOnGrid(e.getX(),e.getY()); 
        }else{
            p=new Point(e.getX(),e.getY());
        }
        
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE)); 
        
        Trackable area=((Trackable)getTarget());
        boolean justcreated=area.getLinePoints().size()==2;         
        
        if(area.getLinePoints().size()==0){
           area.addPoint(p);    
           //avoid point over point
        }else if(!area.getLinePoints().get(area.getLinePoints().size()-1).equals(p)){
           area.addPoint(p);             
        }
        if(justcreated){
           getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.CREATE_MEMENTO));   
           getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));    
        }
        if(((Trackable)getTarget()).getLinePoints().size()>=3){
           getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));    
        }            
        
        getComponent().Repaint(); 
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        // TODO Implement this method
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
        // TODO Implement this method
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
        ((Trackable)getTarget()).getFloatingEndPoint().setLocation(e.getX(),e.getY());   
        getComponent().Repaint(); 
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
        getTarget().setSelected(false);
        getComponent().getEventMgr().resetEventHandle();
        getComponent().Repaint();
    }
    
    @Override
    public void Detach() {
        ((Trackable)getTarget()).Reset(); 
        if(((Trackable)getTarget()).getLinePoints().size()<3&&(getTarget()).getOwningUnit()!=null){
            getTarget().getOwningUnit().delete(((getTarget())).getUUID());
        }
        super.Detach();
    }
}
