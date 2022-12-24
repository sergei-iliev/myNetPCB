package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;

import com.mynetpcb.d2.shapes.Box;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

public class UnitEventHandle <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    private final Box selectionRect;
    
    public UnitEventHandle(U component) {
        super(component);
        selectionRect = new Box();
    }

    @Override
    protected void clear() {
        selectionRect.setRect(0,0,0,0);
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        getComponent().getModel().getUnit().setSelected(false);  
        getComponent().Repaint();
    	if (SwingUtilities.isRightMouseButton(e.getMouseEvent())) {
            getComponent().getPopupMenu().registerUnitPopup(e, getTarget());
            return;
        }
        mx = e.getWindowX();
        my = e.getWindowY();    
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        selectionRect.move(getComponent().getViewportWindow().getX(), getComponent().getViewportWindow().getY());
        getComponent().getModel().getUnit().setSelected(getComponent().getModel().getUnit().getScalableTransformation().getInverseRect(selectionRect));
        getComponent().Repaint();    
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {
        int w=e.getWindowX() - (int)mx;
        int h=e.getWindowY() - (int)my;
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