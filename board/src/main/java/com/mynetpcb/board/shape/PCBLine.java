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
import com.mynetpcb.core.capi.line.Trackable.ResumeState;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.pad.shape.Line;

public class PCBLine extends Line implements PCBShape{
    public PCBLine(int thickness,int layermaskId) {
        super(thickness,layermaskId);
    }
    
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
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
        Box rect = this.polyline.box();
        rect.scale(scale.getScaleX());           
        if (!this.isFloating()&& (!rect.intersects(viewportWindow))) {
                return;
        }
        Composite originalComposite = g2.getComposite();
        AlphaComposite composite;
        if(((CompositeLayerable)this.getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())) {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);                                                        	  
        }else {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);                                                        	           
        }
        g2.setComposite(composite );
        
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        
        Polyline r=this.polyline.clone();   
        
        // draw floating point
        if (this.isFloating()) {                                                    
            if(this.getResumeState()==ResumeState.ADD_AT_FRONT){                
                Point p = this.floatingEndPoint.clone();
                r.points.add(0,p);                
            }else{
                            
                Point p = this.floatingEndPoint.clone();
                r.add(p);                                
            }            
        }
        
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        double wireWidth = thickness * scale.getScaleX();
        g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
       
        r.paint(g2, false);    
        g2.setComposite(originalComposite);
    }

    
    public static class Memento extends Line.Memento {
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }
    }           
}
