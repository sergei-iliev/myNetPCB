package com.mynetpcb.pad.shape.pad;

import com.mynetpcb.core.board.Net;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.GeometricFigure;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Rectangle;
import com.mynetpcb.d2.shapes.Vector;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.shape.pad.flyweight.PadFactory;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
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
        
        //1. THERMAL makes sense if pad has copper on source layer
        if ((source.getCopper().getLayerMaskID() & padRef.get().getCopper().getLayerMaskID()) == 0) {
            return; //not on the same layer
        }
        if(source.isSameNet((Net)padRef.get()) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){        	           	      	        	       	  
            //Utilities.drawCrosshair(g2, null,300,r.points.get(0));
            //Utilities.drawCrosshair(g2, null,300,r.points.get(3));
        	Pad pad=(Pad)padRef.get();        	          	              
            g2.setColor(source.isSelected()? Color.GRAY :source.getCopper().getColor());
            
            Composite originalComposite = g2.getComposite();
            AlphaComposite composite;
            if(((CompositeLayerable)((Shape)source).getOwningUnit()).getActiveSide()==Layer.Side.resolve(source.getCopper().getLayerMaskID())) {
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);                                                        	  
            }else {
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);                                                        	           
            }
            g2.setComposite(composite );   
            double d=this.rect.points.get(0).distanceTo(this.rect.points.get(1));
            double distance=r.points.get(0).distanceTo(r.points.get(3));

            //first point on line
            Vector v=new Vector(r.points.get(0),r.points.get(1));
            Vector n=v.normalize();
            n.rotate90CCW();
            
            //first point
            Point p=r.points.get(0).middleOf(r.points.get(1));            
            //second point
            double a=p.x +distance*n.x;
            double b=p.y +distance*n.y;
                        
      	    Line line=(Line)PadFactory.acquire(Line.class);
      	    g2.setStroke(new BasicStroke((float)((d/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
      	    line.setLine(p.x,p.y,a,b);            
            line.paint(g2, true);
            
            //second line
             d=this.rect.points.get(1).distanceTo(this.rect.points.get(2));
             distance=r.points.get(0).distanceTo(r.points.get(1));
            
            v=new Vector(r.points.get(1),r.points.get(2));
            n=v.normalize();
            n.rotate90CCW();
            
            //first point
            p=r.points.get(1).middleOf(r.points.get(2));            
            //second point
            a=p.x +distance*n.x;
            b=p.y +distance*n.y;
                        
      	    
      	    g2.setStroke(new BasicStroke((float)((d/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
      	    line.setLine(p.x,p.y,a,b);            
            line.paint(g2, true);
            
            
            g2.setComposite(originalComposite);
            PadFactory.release(line);
        }

    }

    @Override
    public void printClearance(Graphics2D g2, PrintContext printContext, ClearanceSource source) {
        g2.setColor(printContext.getBackgroundColor());  
        Rectangle r=this.rect.clone();
        r.grow(source.getClearance());                 
        r.paint(g2,true);
        //1. THERMAL makes sense if pad has copper on source layer
        if ((source.getCopper().getLayerMaskID() & padRef.get().getCopper().getLayerMaskID()) == 0) {
            return; //not on the same layer
        }
        if(source.isSameNet((Net)padRef.get()) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){        	           	      	        	       	        	          	              
        	g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:source.getCopper().getColor());              
            double d=this.rect.points.get(0).distanceTo(this.rect.points.get(1));
            double distance=r.points.get(0).distanceTo(r.points.get(3));

            //first point on line
            Vector v=new Vector(r.points.get(0),r.points.get(1));
            Vector n=v.normalize();
            n.rotate90CCW();
            
            //first point
            Point p=r.points.get(0).middleOf(r.points.get(1));            
            //second point
            double a=p.x +distance*n.x;
            double b=p.y +distance*n.y;
                        
      	    Line line=(Line)PadFactory.acquire(Line.class);
      	    g2.setStroke(new BasicStroke((float)((d/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
      	    line.setLine(p.x,p.y,a,b);            
            line.paint(g2, true);
            
            //second line
             d=this.rect.points.get(1).distanceTo(this.rect.points.get(2));
             distance=r.points.get(0).distanceTo(r.points.get(1));
            
            v=new Vector(r.points.get(1),r.points.get(2));
            n=v.normalize();
            n.rotate90CCW();
            
            //first point
            p=r.points.get(1).middleOf(r.points.get(2));            
            //second point
            a=p.x +distance*n.x;
            b=p.y +distance*n.y;
                        
      	    
      	    g2.setStroke(new BasicStroke((float)((d/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
      	    line.setLine(p.x,p.y,a,b);            
            line.paint(g2, true);
            
            PadFactory.release(line);
        }
        
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
