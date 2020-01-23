package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.event.ActionEvent;

import java.lang.ref.WeakReference;

import javax.swing.SwingUtilities;

/**
 *Responsible for text movement of all textable shapes
 * @author Sergey Iliev
 */
public class TextureEventHandle<U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    
    private WeakReference<Texture> texture;
    
    public TextureEventHandle(U component) {
        super(component);
    }

    
    @Override
    protected void clear() {
        if(texture!=null){
            texture.clear();  
            texture=null;
        }
    }
    public void mouseScaledPressed(MouseScaledEvent e) {
        if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
            getComponent().getModel().getUnit().setSelected(getTarget().getUUID(), !getTarget().isSelected());
            getComponent().Repaint(); 
            this.ctrlButtonPress = true;
            return;
        }
        /*
         * right mouse click on text owner
         */
        if(SwingUtilities.isRightMouseButton(e)){
            getComponent().getModel().getUnit().setSelected(false);
            getTarget().setSelected(true);            
            getComponent().Repaint(); 
            try{
            if(getTarget().showContextPopup()!=null){                
               getTarget().showContextPopup().invoke(getComponent().getPopupMenu(),e, getTarget());
            }else{   
               //getComponent().getPopupMenu().registerChipPopup(e, getTarget()); 
            }
            }catch(Exception ex){
                ex.printStackTrace(System.out);
            }
            return;
        }        
        mx = e.getX();
        my = e.getY();         
        getComponent().getModel().getUnit().setSelected(false);               
        getTarget().setSelected(true); 

         texture= new WeakReference<Texture>(((Textable)getTarget()).getClickedTexture(e.getX(),e.getY()));  
         getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
         
         getComponent().Repaint();
         e.consume();          
    }

    public void mouseScaledReleased(MouseScaledEvent e) {
        if(getComponent().getParameter("snaptogrid",Boolean.class,Boolean.FALSE)==Boolean.TRUE){
            getTarget().getOwningUnit().getGrid().snapToGrid(texture.get().getAnchorPoint()); 
        }
        //***update PropertiesPanel           
        getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
        
        
        //***Undo processor
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));           
        getComponent().Repaint();            
    }

    public void mouseScaledDragged(MouseScaledEvent e) {       
        int new_mx = e.getX();
        int new_my = e.getY();        
        
        
        texture.get().move(new_mx - mx, new_my - my);
        getTarget().getOwningUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.PROPERTY_CHANGE));
            
        // update our data
        mx = new_mx;
        my = new_my;   

        getComponent().Repaint();   
        e.consume();                
    }

    public void mouseScaledMove(MouseScaledEvent e) {
    
    }

    public void doubleScaledClick(MouseScaledEvent e) {
    
    }
    
    
}
