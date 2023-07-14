package com.mynetpcb.pad.shape.pad;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.layer.ClearanceSource;
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
import com.mynetpcb.d2.shapes.Rectangle;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.shape.pad.flyweight.PadFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

public class RectangularShape implements PadDrawing {
        private WeakReference<PadShape> padRef; 
        private Rectangle rect;
        
    public RectangularShape(double x,double y,double width,double height,PadShape pad){
        padRef=new WeakReference<>(pad);
        this.rect=new Rectangle(x-width/2,y-height/2,width,height);
    }

    @Override
    public boolean paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermaskId) {
        //check if outside of visible window
        Box rect = this.rect.box();
        rect.grow(padRef.get().getSolderMaskExpansion());
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
            return false;
        }                       

        var r=(Rectangle)PadFactory.acquire(Rectangle.class);
        try {
            //draw solder mask	
          if((((this.padRef.get().getCopper().getLayerMaskID()&Layer.LAYER_FRONT)!=0)&&((layermaskId&Layer.SOLDERMASK_LAYER_FRONT)!=0))||
            	(((this.padRef.get().getCopper().getLayerMaskID()&Layer.LAYER_BACK)!=0)&&((layermaskId&Layer.SOLDERMASK_LAYER_BACK)!=0))) {        	
       	
        	r.assign(this.rect);
            r.grow(padRef.get().getSolderMaskExpansion());
            r.scale(scale.getScaleX());
            r.move(-viewportWindow.getX(), -viewportWindow.getY());        
            g2.setColor(this.padRef
                    .get()
                    .isSelected() ? Color.GRAY :Layer.Copper.BMask.getColor());

            r.paint(g2, true);
          }
            	  
          //draw pad shape  
          if(((this.padRef.get().getCopper().getLayerMaskID()&layermaskId)!=0)) {	                 
            r.assign(this.rect);
            r.scale(scale.getScaleX());
            r.move(-viewportWindow.getX(), -viewportWindow.getY());
            g2.setColor(this.padRef
                    .get()
                    .isSelected() ? Color.GRAY:this.padRef.get().getCopper().getColor());
            r.paint(g2, true);        	
          }
        }finally {
        	PadFactory.release(r);
        }
             
         return true;
    }
    
    @Override
    public GeometricFigure getGeometricFigure(){
      return rect;  
    }
    
    @Override
    public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                              ClearanceSource source) {
        Rectangle r=this.rect.clone();
        r.grow(source.getClearance());         
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());
        g2.setColor(Color.BLACK);
        r.paint(g2,true);
        
        if(this.padRef.get().isSameNet(source)&&source.getPadConnection()==PadShape.PadConnection.THERMAL) {
        	
        }

    }

    @Override
    public void printClearance(Graphics2D g2, PrintContext printContext, ClearanceSource source) {
        g2.setColor(printContext.getBackgroundColor());  
        Rectangle rect=this.rect.clone();
        rect.grow(source.getClearance());                 
        rect.paint(g2,true);
    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:padRef.get().getCopper().getColor());  
        rect.paint(g2, true);
    }    
    @Override
    public void rotate(double alpha, Point pt) {
        this.rect.rotate(alpha,pt);       
    }
    @Override
    public void setSize(double width,double height){
        this.rect.setSize(width,height);
        this.rect.rotate(this.padRef.get().getRotate(), this.getCenter());
    }

    @Override
    public void mirror(Line line) {
        this.rect.mirror(line);
    }
    @Override
    public void move(double xoffset, double yoffset) {
        this.rect.move(xoffset,yoffset);
    }

    @Override
    public Point getCenter() {
        return this.rect.box().getCenter();
    }

    @Override
    public Box getBoundingShape() {
        return this.rect.box();
    }

    @Override
    public RectangularShape copy(PadShape pad) {
        RectangularShape copy=new RectangularShape(0,0,0,0,pad);
        copy.rect=this.rect.clone(); 
        return copy;  
    }

    @Override
    public boolean contains(Point pt) {
        return this.rect.contains(pt);
    }
    
    @Override
    public Memento getState(){
        Memento memento=new Memento();
        memento.saveStateFrom(this);
        return memento;
    }
    public static class Memento implements PadDrawing.Memento<RectangularShape> {
        private double x[],y[];
        
        @Override
        public void loadStateTo(RectangularShape shape) {    
            int i=0;
            for(Point pt:shape.rect.points){
                pt.set(x[i],y[i]);
                i++;               
            }            

        }

        @Override
        public void saveStateFrom(RectangularShape shape) {
            x=new double[shape.rect.points.size()];
            y=new double[shape.rect.points.size()];
            int i=0;
            for(Point pt:shape.rect.points){
                x[i]=pt.x;
                y[i]=pt.y;
                i++;
            }            
        }
    }

	 
}
