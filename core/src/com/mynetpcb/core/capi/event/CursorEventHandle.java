package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.Moveable;
import com.mynetpcb.core.capi.Rectangular;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.UnitMgr;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CursorEventHandle  <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    public CursorEventHandle(U component) {
        super(component);
    }

    @Override
    protected void Clear() {
        
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(e.getModifiers()==InputEvent.BUTTON3_MASK){  
           getComponent().getDialogFrame().setButtonGroup( getComponent().COMPONENT_MODE);
           getComponent().setMode(getComponent().COMPONENT_MODE);  
           getComponent().Repaint();
           return;   //***right button click 
        } 
        try {
            Shape shape = getTarget().clone();
            getComponent().getModel().getUnit().Add(shape);
            getComponent().getModel().getUnit().setSelected(false);
            shape.setSelected(true);
            shape.alignToGrid((Boolean)getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE));
    //            if(shape instanceof Textable&&getComponent().getModel().getUnit().getTextLayoutVisibility()){
    //               ((Textable)shape).getChipText().setTextLayoutVisible(true);
    //            }
            getComponent().Repaint();
            getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(shape, ShapeEvent.SELECT_SHAPE));
            //register create with undo mgr
            getComponent().getModel().getUnit().registerMemento(shape.getState(MementoType.CREATE_MEMENTO));
            //register placement 
            getComponent().getModel().getUnit().registerMemento(shape.getState(MementoType.MOVE_MEMENTO));
        } catch (CloneNotSupportedException f) {
            f.printStackTrace(System.out);
        }        
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {

    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {

    }

    public void mouseScaledMove(MouseScaledEvent e) {
        
        int new_mx = e.getX()-(getTarget() instanceof Rectangular?getTarget().getWidth()/2:0);
        int new_my = e.getY()-(getTarget() instanceof Rectangular?getTarget().getHeight()/2:0);

        getTarget().Move((new_mx - mx), (new_my - my));
        
        // update our data
        mx = new_mx;
        my = new_my;    
        e.consume(); 
        getComponent().Repaint();
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {

    }
    
    @Override
    public boolean forwardKeyPress(KeyEvent e){
        UnitMgr unitMgr=new UnitMgr();
        if (e.getModifiersEx()!=0) {
            if(e.getModifiers()==ActionEvent.CTRL_MASK){       
                {
                    if(e.getKeyCode()==KeyEvent.VK_Q||e.getKeyCode()==KeyEvent.VK_A){  
                        getTarget().Rotate(e.getKeyCode()==KeyEvent.VK_Q?Moveable.Rotate.LEFT:Moveable.Rotate.RIGHT);
                        
                        unitMgr.normalizePinText(getTarget());
                        getComponent().Repaint(); 
                        
                    }  
                }
            } 
            
            if(e.getModifiers()==ActionEvent.SHIFT_MASK ){                            
            {    
                    if(e.getKeyCode()==KeyEvent.VK_Q||e.getKeyCode()==KeyEvent.VK_A){
                        getTarget().Mirror(e.getKeyCode()==KeyEvent.VK_Q?Moveable.Mirror.HORIZONTAL:Moveable.Mirror.VERTICAL);
                        
                        unitMgr.normalizePinText(getTarget());  
                        getComponent().Repaint();  
                       
                    }
               
                }
            }          
        }     
        return true;
    }
}
