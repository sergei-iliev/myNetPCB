package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

public class UnitEventHandle <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    private final Rectangle selectionRect;
    
    public UnitEventHandle(U component) {
        super(component);
        selectionRect = new Rectangle();
    }

    @Override
    protected void Clear() {
        selectionRect.setRect(0,0,0,0);
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            getComponent().getPopupMenu().registerUnitPopup(e, getTarget());
            return;
        }
        getComponent().getModel().getUnit().setSelected(false);  
        getComponent().Repaint();
        mx = e.getWindowX();
        my = e.getWindowY();
        e.consume();     
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        getComponent().getModel().getUnit().setSelected(getComponent().getModel().getUnit().getScalableTransformation().getInverseRect(new Rectangle(getComponent().getViewportWindow().x+selectionRect.x,getComponent().getViewportWindow().y+selectionRect.y,selectionRect.width,selectionRect.height)));
        getComponent().Repaint();    
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
        int w=e.getWindowX() - mx;
        int h=e.getWindowY() - my;
        selectionRect.setRect(mx - (w < 0 ? Math.abs(w) : 0),
                                  my - (h < 0 ? Math.abs(h) : 0), Math.abs(w),
                                  Math.abs(h));
        
        getComponent().Repaint();
        drawSelectionRect((Graphics2D)getComponent().getCanvas().getGraphics(),selectionRect);   
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
    }
    }