package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Sublineable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.UnitMgr;

import java.awt.event.ActionEvent;

import java.util.Collection;

import javax.swing.SwingUtilities;

public class BlockEventHandle <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    private Collection<Shape> selectedSymbols;
    //***subWire points
    private Collection<LinePoint> selectedWirePoints;
    //***owning wires to redraw-
    private Collection<Sublineable> selectedWires;
    
    private final boolean isAlignable;
    public BlockEventHandle(U component,boolean isAlignable) {
        super(component);
        this.isAlignable=isAlignable;
    }

    @Override
    public void attach(){
        super.attach();
        selectedSymbols = getComponent().getModel().getUnit().getSelectedShapes(false);
        //used in Do/Undo
        selectedWires = UnitMgr.getInstance().getSublineWires(getComponent().getModel().getUnit());
        selectedWirePoints =UnitMgr.getInstance().getSublinePoints(getComponent().getModel().getUnit());    
        
    }
    
    @Override
    protected void clear() {
        selectedSymbols.clear();
        selectedWirePoints.clear();
        selectedWires.clear();
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(SwingUtilities.isRightMouseButton(e)){ 
            getComponent().getPopupMenu().registerBlockPopup(e,null);            
            return;
        } 
        if((e.getModifiers()&ActionEvent.CTRL_MASK)==ActionEvent.CTRL_MASK){
         getComponent().getModel().getUnit().setSelected(getTarget().getUUID(),!getTarget().isSelected());
         this.ctrlButtonPress=true;
         getComponent().Repaint(); 
         return;
        }        
        mx = e.getX();
        my = e.getY(); 
     
        getComponent().getModel().getUnit().registerMemento(new CompositeMemento(MementoType.MOVE_MEMENTO).add(selectedSymbols).add(selectedWires));

        
        e.consume();      
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        if(isAlignable){
            if(getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)!=Boolean.TRUE){
              return;
            }
        }
        UnitMgr.getInstance().alignBlock(getComponent().getModel().getUnit().getGrid(),selectedSymbols);
        //****align subWires
        for(LinePoint wirePoint:selectedWirePoints){  
          wirePoint.set(getComponent().getModel().getUnit().getGrid().positionOnGrid(wirePoint.x, wirePoint.y)); 
        }
        //***register group memento
        getComponent().getModel().getUnit().registerMemento(new CompositeMemento(MementoType.MOVE_MEMENTO).add(selectedSymbols).add(selectedWires));
        getComponent().Repaint();      
    }    

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {               
           int new_mx = e.getX();
           int new_my = e.getY();

           UnitMgr.getInstance().moveBlock(selectedSymbols,new_mx - mx, new_my - my);

           for(LinePoint wirePoint:selectedWirePoints){  
             wirePoint.set(wirePoint.x+(new_mx - mx),wirePoint.y+(new_my - my)); 
           } 
           // update our data
           mx = new_mx;
           my = new_my;    
           
           getComponent().Repaint();           
           e.consume();      
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
    }

}
