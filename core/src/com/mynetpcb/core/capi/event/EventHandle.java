package com.mynetpcb.core.capi.event;


import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.UndoCallback;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.awt.geom.Point2D;

import java.lang.ref.WeakReference;

import javax.swing.SwingUtilities;


/**
 *Base class for all events happenning on the unit component
 * Depending on the event target different event handlers are activated dyncamically
 * @param <S>
 * @param <C>
 * @author Sergey Iliev
 */
public abstract class EventHandle<C extends UnitComponent,S extends Shape> implements UndoCallback{
    
    protected int mx, my;
    //***class does not own target
    private WeakReference<S> weakTargetRef;

    private final WeakReference<C> weakComponentRef;

    protected boolean ctrlButtonPress;

    public EventHandle(C component) {
        this.weakComponentRef = new WeakReference<C>(component);
    }

    public void mousePressed(MouseEvent e) {         
        mouseScaledPressed(new  MouseScaledEvent(e,getComponent().getModel().getUnit().getScalableTransformation().getInversePoint(new Point((int)(getComponent().getViewportWindow().getX()+e.getX()),(int)(getComponent().getViewportWindow().getY()+e.getY())))));                                                
    }

    public void mouseReleased(MouseEvent e) {
       if((!SwingUtilities.isRightMouseButton(e))&&(!this.ctrlButtonPress)){                       
         mouseScaledReleased(new MouseScaledEvent(e,getComponent().getModel().getUnit().getScalableTransformation().getInversePoint(new Point((int)(getComponent().getViewportWindow().getX()+e.getX()),(int)(getComponent().getViewportWindow().getY()+e.getY())))));                                                            
       } 
    }

    public void mouseDragged(MouseEvent e) {
        //****disallow dragging when CTRL is clicked
        if((!SwingUtilities.isRightMouseButton(e))&&(!this.ctrlButtonPress)){ 
          mouseScaledDragged(new MouseScaledEvent(e,getComponent().getModel().getUnit().getScalableTransformation().getInversePoint(new Point((int)(getComponent().getViewportWindow().getX()+e.getX()),(int)(getComponent().getViewportWindow().getY()+e.getY())))));                                                           
        }
    }

    public void mouseMove(MouseEvent e) {
        if(!SwingUtilities.isRightMouseButton(e)&&(!this.ctrlButtonPress)){   
          mouseScaledMove(new MouseScaledEvent(e,getComponent().getModel().getUnit().getScalableTransformation().getInversePoint(new Point((int)(getComponent().getViewportWindow().getX()+e.getX()),(int)(getComponent().getViewportWindow().getY()+e.getY())))));                                                         
        }
    }

    public void doubleClick(MouseEvent e) {
        doubleScaledClick(new MouseScaledEvent(e,getComponent().getModel().getUnit().getScalableTransformation().getInversePoint(new Point((int)(getComponent().getViewportWindow().getX()+e.getX()),(int)(getComponent().getViewportWindow().getY()+e.getY())))));                                                         

    }

    public void mouseEntered(MouseEvent e) {
        mouseScaledEntered(new MouseScaledEvent(e,getComponent().getModel().getUnit().getScalableTransformation().getInversePoint(new Point((int)(getComponent().getViewportWindow().getX()+e.getX()),(int)(getComponent().getViewportWindow().getY()+e.getY())))));                                                         

    }

    public void mouseExited(MouseEvent e) {
        mouseScaledExited(new MouseScaledEvent(e,getComponent().getModel().getUnit().getScalableTransformation().getInversePoint(new Point((int)(getComponent().getViewportWindow().getX()+e.getX()),(int)(getComponent().getViewportWindow().getY()+e.getY())))));                                                          

    }

    public void mouseScaledEntered(MouseScaledEvent e) {

    }

    public void mouseScaledExited(MouseScaledEvent e) {

    }
   
    protected abstract void Clear();
    /*
     * zoomin,rotating and so on
     */
    public void resizingEvent(){
        
    }
    
    public void keyPressed(KeyEvent e){
//            //***ESCAPE==cancel
//            if(e.getKeyCode()==KeyEvent.VK_ESCAPE){   
//                        getComponent().getDialogFrame().setButtonGroup(getComponent().getMode(), 0,false);
//                        getComponent().setMode(0);
//            }
    }
    //***event forwarded from the global key hook
    public boolean forwardKeyPress(KeyEvent e){
      return false;  
    }
    
    public void onUndo(){
        if(null!=getTarget()&&null==getComponent().getModel().getUnit().getShape(getTarget().getUUID())){
          getComponent().getEventMgr().resetEventHandle();
        }        
    }
    
    public abstract void mouseScaledPressed(MouseScaledEvent e);

    public abstract void mouseScaledReleased(MouseScaledEvent e);

    public abstract void mouseScaledDragged(MouseScaledEvent e);

    public abstract void mouseScaledMove(MouseScaledEvent e);

    public abstract void doubleScaledClick(MouseScaledEvent e);

    //***use to init resources on Handle select

    public void Attach() {
        this.ctrlButtonPress = false;
        mx=0;
        my=0;  
    }
    //***use to clean resources on Handle close

    public void Detach() {
        Clear();
    }

    public void setTarget(S target) {
        if (this.weakTargetRef != null && this.weakTargetRef.get() != null) {
            this.weakTargetRef.clear();
        }
        this.weakTargetRef = new WeakReference<S>(target);
    }

    public S getTarget() {
        return weakTargetRef.get();
    }

    public C getComponent() {
        return weakComponentRef.get();
    }
  
    //***helper method
    protected void drawSelectionRect(Graphics2D g,Rectangle selectionRect) {
        if (selectionRect.getWidth() != 0 || selectionRect.getHeight() != 0) {
            AlphaComposite composite =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            Composite originalComposite = g.getComposite();
            g.setPaint(Color.GRAY);
            g.setComposite(composite);
            g.fill(selectionRect);
            g.setComposite(originalComposite);
            g.setStroke(new BasicStroke(1));
            g.draw(selectionRect);            
        }
    }
}

