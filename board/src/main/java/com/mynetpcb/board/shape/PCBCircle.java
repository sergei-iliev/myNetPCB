package com.mynetpcb.board.shape;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape.Fill;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.shape.Circle;

public class PCBCircle extends Circle implements PCBShape{
    public PCBCircle(double x,double y,double r,int thickness,int layermaskId) {
        super(x,y,r,thickness,layermaskId);
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
//    @Override
//    public int getDrawingLayerPriority() {
//   	 	if(!this.copper.isCopperLayer()) {
//		 return super.getDrawingLayerPriority();
//   	 	}
//	  	if(((CompositeLayerable)getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())){
//   	 		return 4;
//   	 	}else{
//   	 		return 3; 
//   	 	}           
//    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {        
        Box rect = this.circle.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        
        com.mynetpcb.d2.shapes.Circle  c=this.circle.clone();
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        Composite originalComposite = g2.getComposite();
        AlphaComposite composite;
        if(((CompositeLayerable)this.getOwningUnit()!=null)&&(((CompositeLayerable)this.getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID()))) {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);                                                        	  
        }else {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);                                                        	           
        }                                               
        g2.setComposite(composite );             

        
        if (fill == Fill.EMPTY) { //framed
            double wireWidth = thickness * scale.getScaleX();
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));            
            c.paint(g2, false);

        } else { //filled
            c.paint(g2,true);
        }                
        g2.setComposite(originalComposite);

    }
    
    public static class Memento extends Circle.Memento {

        public Memento(MementoType mementoType) {
            super(mementoType);
        }

    }    
}
