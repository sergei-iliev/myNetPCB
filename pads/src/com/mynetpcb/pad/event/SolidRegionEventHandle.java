package com.mynetpcb.pad.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.shape.SolidRegion;

import javax.swing.SwingUtilities;

public class SolidRegionEventHandle <U extends UnitComponent,S extends Shape>  extends EventHandle<U,S>{
    public SolidRegionEventHandle(U component) {
        super(component);        
    }


    @Override
    protected void clear() {
        // TODO Implement this method
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        this.mx=e.getX();
        this.my=e.getY();
        if(SwingUtilities.isRightMouseButton(e.getMouseEvent())){                         
             return;
        }
        
        this.getComponent().getModel().getUnit().setSelected(false);
        this.getTarget().setSelected(true);

        Point p;      
        
        if((Boolean)getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)){
          p=this.getComponent().getModel().getUnit().getGrid().positionOnGrid(e.getX(),e.getY());                
        }else{
          p=new Point(e.getX(),e.getY());
        }
        boolean justcreated=((SolidRegion)this.getTarget()).getLinePoints().size()==2;
        
        if(((SolidRegion)this.getTarget()).getLinePoints().size()==0){
            ((SolidRegion)this.getTarget()).add(p);    
            //avoid point over point
        }else if(!((SolidRegion)this.getTarget()).getLinePoints().get(((SolidRegion)this.getTarget()).getLinePoints().size()-1).equals(p)){
            ((SolidRegion)this.getTarget()).getLinePoints().add(p);           
        }
        
        
        this.getComponent().Repaint();      
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
        ((SolidRegion)this.getTarget()).getFloatingEndPoint().set(e.getX(),e.getY());   
        this.getComponent().Repaint();
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
        this.getTarget().setSelected(false);
        this.getComponent().getEventMgr().resetEventHandle();
        this.getComponent().Repaint();   
    }
    
    @Override
    public void detach() {
        ((SolidRegion)this.getTarget()).reset(); 
        if(((SolidRegion)this.getTarget()).getLinePoints().size()<3){
           (this.getTarget()).getOwningUnit().delete(getTarget().getUUID());
        }
        
        super.detach();
    }
}
