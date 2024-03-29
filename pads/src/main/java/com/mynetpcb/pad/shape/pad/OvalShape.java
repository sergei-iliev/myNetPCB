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
import com.mynetpcb.d2.shapes.GeometricFigure;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Vector;
import com.mynetpcb.pad.shape.pad.flyweight.PadFactory;

public class OvalShape implements PadDrawing {
    private WeakReference<PadShape> padRef;
    private Obround obround;

    public OvalShape(double x, double y, double width, double height, PadShape pad) {
        padRef = new WeakReference<>(pad);
        this.obround = new Obround(x,y, width, height);
    }

    public OvalShape copy(PadShape pad) {
        OvalShape copy = new OvalShape(this.obround
                                           .getCenter()
                                           .x, obround.getCenter().y, 0, 0, pad);
        copy.obround = this.obround.clone();
        return copy;
    }
    @Override
    public GeometricFigure getGeometricFigure(){
      return obround;  
    }
    @Override
    public boolean paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermaskId) {
        //check if outside of visible window
        Box rect = this.obround.box();
        rect.grow(padRef.get().getSolderMaskExpansion());
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
            return false;
        }

        var o=(Obround)PadFactory.acquire(Obround.class);
        try {
            //draw solder mask	
        if((((this.padRef.get().getCopper().getLayerMaskID()&Layer.LAYER_FRONT)!=0)&&((layermaskId&Layer.SOLDERMASK_LAYER_FRONT)!=0))||
            	(((this.padRef.get().getCopper().getLayerMaskID()&Layer.LAYER_BACK)!=0)&&((layermaskId&Layer.SOLDERMASK_LAYER_BACK)!=0))) {        	
        	o.assign(this.obround);                
        	o.grow(padRef.get().getSolderMaskExpansion(),this.padRef.get().getRotate());
        	o.scale(scale.getScaleX());
        	o.move(-viewportWindow.getX(), -viewportWindow.getY());        
        	g2.setColor(this.padRef
                .get()
                .isSelected() ? Color.GRAY : Layer.Copper.BMask.getColor());

        	o.paint(g2, true);
        }	  
        //draw pad shape        
        if(((this.padRef.get().getCopper().getLayerMaskID()&layermaskId)!=0)) {	        
        	o.assign(this.obround);
        	o.scale(scale.getScaleX());
        	o.move(-viewportWindow.getX(), -viewportWindow.getY());
            g2.setColor(this.padRef
                    .get()
                    .isSelected() ? Color.GRAY : this.padRef.get().getCopper().getColor());
        	o.paint(g2, true);
        }
        }finally {
        	PadFactory.release(o);	
		}
        return true;
    }

    @Override
    public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                              ClearanceSource source) {
                                       
        g2.setColor(Color.BLACK);        
        Obround o=this.obround.clone();
        o.grow(source.getClearance(),this.padRef.get().getRotate());

        
        o.scale(scale.getScaleX());
        o.move(-viewportWindow.getX(), -viewportWindow.getY());
        o.paint(g2, true);
        
        //1. THERMAL makes sense if pad has copper on source layer
        if ((source.getCopper().getLayerMaskID() & padRef.get().getCopper().getLayerMaskID()) == 0) {
            return; //not on the same layer
        }
        if(source.isSameNet((Net)padRef.get()) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){        	           	      	        	       	        	          	              
            g2.setColor(source.isSelected()? Color.GRAY :source.getCopper().getColor());
            
            Composite originalComposite = g2.getComposite();
            AlphaComposite composite;
            if(((CompositeLayerable)((Shape)source).getOwningUnit()).getActiveSide()==Layer.Side.resolve(source.getCopper().getLayerMaskID())) {
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);                                                        	  
            }else {
                composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);                                                        	           
            }
            g2.setComposite(composite );
            //horizontal line
            double r=o.getDiameter()/2;
            //first point on line
            Vector v=new Vector(o.pe,o.ps);
            Vector n=v.normalize();
            double a=o.ps.x +r*n.x;
            double b=o.ps.y +r*n.y;  
            //second point on line
            v=new Vector(o.ps,o.pe);
            n=v.normalize();
            double c=o.pe.x +r*n.x;
            double d=o.pe.y +r*n.y;  
            g2.setStroke(new BasicStroke((float)((this.obround.getDiameter() /2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            Line line=(Line)PadFactory.acquire(Line.class); 
            line.setLine(a,b,c,d);
            line.paint(g2, true);                        
            
            //vertical line
            v.rotate90CW();
            n=v.normalize();
            a=o.pc.x +r*n.x;
            b=o.pc.y +r*n.y; 
            
            n.invert();
            c=o.pc.x +r*n.x;
            d=o.pc.y +r*n.y; 
      	                          
            g2.setStroke(new BasicStroke((float)(((this.obround.ps.distanceTo(this.obround.pe)+this.obround.getDiameter())/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            line.setLine(a,b,c,d);
            line.paint(g2, true);
            
            
            g2.setComposite(originalComposite);
            PadFactory.release(line);
            
      }
        
    }

    @Override
    public void printClearance(Graphics2D g2, PrintContext printContext, ClearanceSource source) {
        Obround o=this.obround.clone();
        g2.setColor(printContext.getBackgroundColor());          
        o.grow(source.getClearance(),this.padRef.get().getRotate());
        o.paint(g2, true);
        //1. THERMAL makes sense if pad has copper on source layer
        if ((source.getCopper().getLayerMaskID() & padRef.get().getCopper().getLayerMaskID()) == 0) {
            return; //not on the same layer
        }
        if(source.isSameNet((Net)padRef.get()) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){        	           	      	        	       	        	          	              
        	g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:source.getCopper().getColor());          
            //horizontal line
            double r=o.getDiameter()/2;
            //first point on line
            Vector v=new Vector(o.pe,o.ps);
            Vector n=v.normalize();
            double a=o.ps.x +r*n.x;
            double b=o.ps.y +r*n.y;  
            //second point on line
            v=new Vector(o.ps,o.pe);
            n=v.normalize();
            double c=o.pe.x +r*n.x;
            double d=o.pe.y +r*n.y;  
            g2.setStroke(new BasicStroke((float)((this.obround.getDiameter() /2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            Line line=(Line)PadFactory.acquire(Line.class); 
            line.setLine(a,b,c,d);
            line.paint(g2, true);                        
            
            //vertical line
            v.rotate90CW();
            n=v.normalize();
            a=o.pc.x +r*n.x;
            b=o.pc.y +r*n.y; 
            
            n.invert();
            c=o.pc.x +r*n.x;
            d=o.pc.y +r*n.y; 
      	                          
            g2.setStroke(new BasicStroke((float)(((this.obround.ps.distanceTo(this.obround.pe)+this.obround.getDiameter())/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            line.setLine(a,b,c,d);
            line.paint(g2, true);
            

            PadFactory.release(line);
            
      }
        
    }

    @Override
    public void setSize(double width, double height) {        
        this.obround.setSize(width,height);        
        this.obround.rotate(this.padRef.get().getRotate());
    }
    @Override
    public void mirror(Line line) {
        this.obround.mirror(line);
    }
    @Override
    public void move(double xoffset, double yoffset) {
        obround.move(xoffset, yoffset);

    }

    @Override
    public Point getCenter() {
        return this.obround.getCenter();
    }

    @Override
    public Box getBoundingShape() {
        return obround.box();
    }


    @Override
    public boolean contains(Point pt) {
        return obround.contains(pt);
    }

    @Override
    public void rotate(double angle, Point pt) {
        obround.rotate(angle, pt);

    }

    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:padRef.get().getCopper().getColor());  
        obround.paint(g2, true);
    }
    

    @Override
    public Memento getState(){
        Memento memento=new Memento();
        memento.saveStateFrom(this);
        return memento;
    }
    
    public static class Memento implements PadDrawing.Memento<OvalShape> {
        private double pcx,pcy;
        private double width;
        private double height;
        private double psx,psy, pex,pey;
        
        @Override
        public void loadStateTo(OvalShape shape) {
    
            shape.obround.pc.set(pcx,pcy);
            shape.obround.ps.set(psx,psy);
            shape.obround.pe.set(pex,pey);
            
            shape.obround.width=width;
            shape.obround.height=height;
        }

        @Override
        public void saveStateFrom(OvalShape shape) {
            pcx=shape.obround.pc.x;
            pcy=shape.obround.pc.y;
            
            psx=shape.obround.ps.x;
            psy=shape.obround.ps.y;
            
            pex=shape.obround.pe.x;
            pey=shape.obround.pe.y;
            
            width=shape.obround.width;
            height=shape.obround.height;
        }
    }    
}
