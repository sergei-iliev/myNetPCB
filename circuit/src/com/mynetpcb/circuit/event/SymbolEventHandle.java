package com.mynetpcb.circuit.event;


import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.event.ActionEvent;

import java.util.Collection;

import javax.swing.SwingUtilities;


/**
 *Process all symbols capable of owning other symbols
 * @author Sergey Iliev
 */
public class SymbolEventHandle extends EventHandle<CircuitComponent,Shape>{
    
    private  Collection<Shape> selectedSymbols; 
    
    public SymbolEventHandle(CircuitComponent component) {
        super(component);
    }
    
    @Override
    public void Attach() {
        super.Attach();        
        selectedSymbols=CircuitMgr.getInstance().getChildrenByParent(getComponent().getModel().getUnit().getShapes(),getTarget());        
    }
    
    public void mouseScaledPressed(MouseScaledEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {                       
            getComponent().getModel().getUnit().setSelected(false);  
            getTarget().setSelected(true);
            for (Shape children : selectedSymbols) {
                children.setSelected(true);
            }                  
            getComponent().Repaint();            
            getComponent().getPopupMenu().registerChipPopup(e,getTarget());            
            return;
        }
        if ((e.getModifiers() & ActionEvent.CTRL_MASK) ==
            ActionEvent.CTRL_MASK) {
            getComponent().getModel().getUnit().setSelected(getTarget().getUUID(),
                                                 !getTarget().isSelected());
                                  
            for (Shape children : selectedSymbols) {
                children.setSelected(getTarget().isSelected());
            }
            
            this.ctrlButtonPress = true;
            getComponent().Repaint();
            return;
        }
        //***one time init
        getComponent().getModel().getUnit().setSelected(false);        

        mx = e.getX();
        my = e.getY();

        selectedSymbols.add(getTarget());        
        for (Shape children : selectedSymbols) {
            children.setSelected(true);
        }
        //chip could be a bundle of connectors and labels

        getComponent().getModel().getUnit().registerMemento(new CompositeMemento(MementoType.MOVE_MEMENTO).Add(selectedSymbols));
        getComponent().Repaint();
        e.consume();


    }

    public void mouseScaledReleased(MouseScaledEvent e) {
        CircuitMgr.getInstance().alignBlock(getComponent().getModel().getUnit().getGrid(),selectedSymbols);
        getComponent().getModel().getUnit().registerMemento(new CompositeMemento(MementoType.MOVE_MEMENTO).Add(selectedSymbols));
        getComponent().Repaint();

    }

    public void mouseScaledDragged(MouseScaledEvent e) {
        int new_mx = e.getX();
        int new_my = e.getY();

        for(Shape shape:selectedSymbols){
                shape.Move(new_mx - mx, new_my - my);
        }            

        
        // update our data
        mx = new_mx;
        my = new_my;
      
        getComponent().Repaint();        
        e.consume();
    }

    public void mouseScaledMove(MouseScaledEvent e) {
        e.consume();
    }

    public void doubleScaledClick(MouseScaledEvent e) {
       CircuitMgr.getInstance().openSymbolInlineEditorDialog(getComponent(),(SCHSymbol)getTarget());
       getComponent().Repaint(); 
    }
    @Override
    protected void Clear(){   
        if (selectedSymbols != null) {            
            selectedSymbols.clear();
            selectedSymbols = null;
        }        
    }
}
