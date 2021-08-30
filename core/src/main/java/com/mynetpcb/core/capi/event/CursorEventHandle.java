package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.d2.shapes.Point;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CursorEventHandle  <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    public CursorEventHandle(U component) {
        super(component);
    }
    @Override
    public void attach() {
        super.attach();
        mx=(int)getTarget().getCenter().x;
        my=(int)getTarget().getCenter().y;
    } 
    @Override
    protected void clear() {
        
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if(e.getMouseEvent().getModifiers()==InputEvent.BUTTON3_MASK){  
           getComponent().getDialogFrame().setButtonGroup(Mode.COMPONENT_MODE);
           getComponent().setMode(Mode.COMPONENT_MODE);  
           getComponent().Repaint();
           return;   //***right button click 
        } 
        try {
            Shape shape = getTarget().clone();
            getComponent().getModel().getUnit().add(shape);
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
        
        double new_mx = e.getX();
        double new_my = e.getY();

        getTarget().move((new_mx - mx), (new_my - my));
        
        // update our data
        mx = new_mx;
        my = new_my;            
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
                       
                        Point p=new Point(mx,my);
                        if(e.getKeyCode()==KeyEvent.VK_Q){ //left                                                                                    
                            getTarget().rotate(90,p);     
                        }else{  //right
                            getTarget().rotate(-90,p);     
                        }           
                        //unitMgr.normalizePinText(getTarget());
                        getComponent().Repaint(); 
                        
                    }  
                }
            } 
            
            if(e.getModifiers()==ActionEvent.SHIFT_MASK ){                            
            {    
                    if(e.getKeyCode()==KeyEvent.VK_Q||e.getKeyCode()==KeyEvent.VK_A){                        
                        Point p=new Point(mx,my);
                        if(e.getKeyCode()==KeyEvent.VK_Q){
                            //getTarget().mirror(new Point(p.x-10,p.y),new Point(p.x+10,p.y)); 
                        }else{
                            //getTarget().mirror(new Point(p.x,p.y-10),new Point(p.x,p.y+10)); 
                        }                        
                        //unitMgr.normalizePinText(getTarget());  
                        getComponent().Repaint();  
                       
                    }
               
                }
            }          
        }     
        return true;
    }
}
