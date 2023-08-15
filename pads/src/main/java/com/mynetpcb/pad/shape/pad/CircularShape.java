package com.mynetpcb.pad.shape.pad;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.lang.ref.WeakReference;

import com.mynetpcb.core.board.Net;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.GeometricFigure;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.shape.pad.flyweight.PadFactory;

public class CircularShape implements PadDrawing {
    private WeakReference<PadShape> padRef;
    private Circle circle;

    public CircularShape(double x, double y, double width, PadShape pad) {
        padRef = new WeakReference<>(pad);
        this.circle = new Circle(new Point(x, y), width / 2);
    }

    @Override
    public CircularShape copy(PadShape pad) {
        CircularShape copy = new CircularShape(this.circle
                                                   .pc
                                                   .x, this.circle
                                                           .pc
                                                           .y, 0, pad);
        copy.circle.r = this.circle.r;
        return copy;
    }
    @Override
    public GeometricFigure getGeometricFigure(){
      return circle;  
    }
    
    @Override
    public boolean paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermaskId) {               
        //check if outside of visible window
        Box rect = this.circle.box();
        rect.grow(padRef.get().getSolderMaskExpansion());
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
            return false;
        }
        

        var c=(Circle)PadFactory.acquire(Circle.class);
        try {
        //draw solder mask	
        if((((this.padRef.get().getCopper().getLayerMaskID()&Layer.LAYER_FRONT)!=0)&&((layermaskId&Layer.SOLDERMASK_LAYER_FRONT)!=0))||
        	(((this.padRef.get().getCopper().getLayerMaskID()&Layer.LAYER_BACK)!=0)&&((layermaskId&Layer.SOLDERMASK_LAYER_BACK)!=0))) {
         c.assign(this.circle);                 
         c.grow(padRef.get().getSolderMaskExpansion());
         c.scale(scale.getScaleX());
         c.move(-viewportWindow.getX(), -viewportWindow.getY());        
         g2.setColor(this.padRef
                .get()
                .isSelected() ? Color.GRAY : Layer.Copper.BMask.getColor());

         c.paint(g2, true);
        }
      //draw pad shape
        if(((this.padRef.get().getCopper().getLayerMaskID()&layermaskId)!=0)) {	                           
        	c.assign(this.circle);
        	c.scale(scale.getScaleX());
        	c.move(-viewportWindow.getX(), -viewportWindow.getY());
            g2.setColor(this.padRef
                    .get()
                    .isSelected() ? Color.GRAY : this.padRef.get().getCopper().getColor());
        	c.paint(g2, true);
        }
        }finally {
        	PadFactory.release(c);	
		}
        
        return true;
    }

    @Override
    public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                              ClearanceSource source) {
        Box rect = getBoundingShape();
        rect.grow(source.getClearance());                                  
        
        g2.setColor(Color.BLACK);        
        Circle c = this.circle.clone();
        c.r=rect.getWidth()/2;
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(), -viewportWindow.getY());
        c.paint(g2, true);
        
        //1. THERMAL makes sense if pad has copper on source layer
        if ((source.getCopper().getLayerMaskID() & padRef.get().getCopper().getLayerMaskID()) == 0) {
            return; //not on the same layer
        }

        if(source.isSameNet((Net)padRef.get()) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){        	           	        	         	          	  
        	  Pad pad=(Pad)padRef.get();        	          	  
              g2.setStroke(new BasicStroke((float)((pad.getWidth()/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
              g2.setColor(source.isSelected()? Color.GRAY :source.getCopper().getColor());
              
              Composite originalComposite = g2.getComposite();
              AlphaComposite composite;
              if(((CompositeLayerable)((Shape)source).getOwningUnit()).getActiveSide()==Layer.Side.resolve(source.getCopper().getLayerMaskID())) {
                  composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);                                                        	  
              }else {
                  composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);                                                        	           
              }
              g2.setComposite(composite );
              //g2.setClip(ellipse);
              
              
              Line line=(Line)PadFactory.acquire(Line.class);
              //1. vertical line
              line.setLine(rect.min.x+rect.getWidth()/2, rect.min.y,rect.min.x+rect.getWidth()/2,rect.max.y);
              line.scale(scale.getScaleX());
              line.move(-viewportWindow.getX(), -viewportWindow.getY());
              line.paint(g2, true);
              //2. horizontal line
              line.setLine(rect.min.x, rect.min.y+rect.getWidth()/2 ,rect.max.x,rect.min.y+rect.getWidth()/2);
              line.scale(scale.getScaleX());
              line.move(-viewportWindow.getX(), -viewportWindow.getY());
              line.paint(g2, true);
              
              //g2.setClip(null);              
              g2.setComposite(originalComposite);
              PadFactory.release(line);
        }
        
    }

    @Override
    public void printClearance(Graphics2D g2, PrintContext printContext, ClearanceSource source) {
                               
        
        g2.setColor(printContext.getBackgroundColor());      
        Circle c=circle.clone();
        c.grow(source.getClearance());                
        c.paint(g2, true);
        
        //1. THERMAL makes sense if pad has copper on source layer
        if ((source.getCopper().getLayerMaskID() & padRef.get().getCopper().getLayerMaskID()) == 0) {
            return; //not on the same layer
        }

        if(source.isSameNet((Net)padRef.get()) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){        	           	        	         	          	 
        	  Pad pad=(Pad)padRef.get();        	          	  
              g2.setStroke(new BasicStroke((float)((pad.getWidth()/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
              g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:source.getCopper().getColor());
                    
              Box rect = getBoundingShape();
              rect.grow(source.getClearance()); 
              
              Line line=(Line)PadFactory.acquire(Line.class);
              //1. vertical line
              line.setLine(rect.min.x+rect.getWidth()/2, rect.min.y,rect.min.x+rect.getWidth()/2,rect.max.y);
              line.paint(g2, true);
              //2. horizontal line
              line.setLine(rect.min.x, rect.min.y+rect.getWidth()/2 ,rect.max.x,rect.min.y+rect.getWidth()/2);              
              line.paint(g2, true);             
              
              PadFactory.release(line);
        }


    }

    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setPaint(printContext.isBlackAndWhite() ? Color.BLACK : padRef.get()
                                                                         .getCopper()
                                                                         .getColor());
        this.circle.paint(g2, true);
    }
    @Override
    public void mirror(Line line) {
        this.circle.mirror(line);
    }
    @Override
    public void rotate(double alpha, Point pt) {
        this.circle.rotate(alpha, pt);
    }

    @Override
    public boolean contains(Point pt) {
        return this.circle.contains(pt);
    }

    @Override
    public Point getCenter() {
        return circle.pc;
    }
    
    public double getDiameter(){
        return 2*circle.r;
    }
    
    @Override
    public Box getBoundingShape() {
        return this.circle.box();
    }

    public void move(double xoffset, double yoffset) {
        this.circle.move(xoffset, yoffset);
    }

    @Override
    public void setSize(double width, double height) {
        this.circle.r = width / 2;
    }
    @Override
    public Memento getState(){
        Memento memento=new Memento();
        memento.saveStateFrom(this);
        return memento;
    }
    public static class Memento implements PadDrawing.Memento<CircularShape> {
        double x,y,r;
        
        @Override
        public void loadStateTo(CircularShape shape) {
   
            shape.circle.pc.set(x,y);
            shape.circle.r=r;
        }

        @Override
        public void saveStateFrom(CircularShape shape) {
            this.x=shape.circle.pc.x;
            this.y=shape.circle.pc.y;
            this.r=shape.circle.r;
        }
    }
}
