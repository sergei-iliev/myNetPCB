package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

public class DragingEventHandle <U extends UnitComponent,S extends Shape> extends EventHandle<U,S>{
    public DragingEventHandle(U component) {
        super(component);
    }

    @Override
    protected void clear() {
    }

    @Override
    public void mouseScaledPressed(MouseScaledEvent e) {
        Cursor cursor =
                Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getComponent(),
                                                                                     "images/dragclose.png").getImage(),
                                                               new Point(16,
                                                                         16),
                                                               "DragHeandClose");
        getComponent().setCursor(cursor);  


        mx = e.getWindowX();
        my = e.getWindowY();
        //getComponent().getDialogFrame().getHorizontalScrollBar().getModel().removeChangeListener(getComponent());
        //getComponent().getDialogFrame().getVerticalScrollBar().getModel().removeChangeListener(getComponent());    
    }

    @Override
    public void mouseScaledReleased(MouseScaledEvent e) {
        Cursor cursor =
                Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getComponent(),
                                                                                     "images/dragopen.png").getImage(),
                                                               new Point(16,
                                                                         16),
                                                               "DragHeandOpen");
        getComponent().setCursor(cursor);  
        //getComponent().getDialogFrame().getHorizontalScrollBar().getModel().addChangeListener(getComponent());
        //getComponent().getDialogFrame().getVerticalScrollBar().getModel().addChangeListener(getComponent());
    }

    @Override
    public void mouseScaledDragged(MouseScaledEvent e) {              
        double newX =getComponent().getViewportWindow().getX()- (e.getWindowX() - mx);
        double newY =getComponent().getViewportWindow().getY()- (e.getWindowY() - my);        
         
    
        getComponent().getViewportWindow().setLocation(newX,newY);
        //getComponent().getDialogFrame().getHorizontalScrollBar().setValue((int)newX);
        //getComponent().getDialogFrame().getVerticalScrollBar().setValue((int)newY);
        
        mx = e.getWindowX();
        my = e.getWindowY();
        getComponent().Repaint();     
    }

    @Override
    public void mouseScaledMove(MouseScaledEvent e) {
    }

    @Override
    public void doubleScaledClick(MouseScaledEvent e) {
    }   
}
