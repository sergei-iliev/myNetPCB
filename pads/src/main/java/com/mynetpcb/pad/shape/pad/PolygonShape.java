package com.mynetpcb.pad.shape.pad;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.GeometricFigure;
import com.mynetpcb.d2.shapes.Hexagon;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.shape.pad.flyweight.PadFactory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

public class PolygonShape implements PadDrawing {
    private WeakReference<PadShape> padRef;
    private Hexagon hexagon;

    public PolygonShape(double x, double y, double width, PadShape pad) {
        padRef = new WeakReference<>(pad);
        this.hexagon=new Hexagon(x,y,width);
    }
    @Override
    public GeometricFigure getGeometricFigure(){
      return hexagon;  
    }
    @Override
    public boolean paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermaskId) {
        //check if outside of visible window
        Box rect = this.hexagon.box();
        rect.grow(this.padRef.get().getSolderMaskExpansion());
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
            return false;
        }

        var h=(Hexagon)PadFactory.acquire(Hexagon.class);
        try {
            //draw solder mask	
        if((((this.padRef.get().getCopper().getLayerMaskID()&Layer.LAYER_FRONT)!=0)&&((layermaskId&Layer.SOLDERMASK_LAYER_FRONT)!=0))||
            	(((this.padRef.get().getCopper().getLayerMaskID()&Layer.LAYER_BACK)!=0)&&((layermaskId&Layer.SOLDERMASK_LAYER_BACK)!=0))) {        	        	
        	h.assign(this.hexagon);        
        	h.grow(padRef.get().getSolderMaskExpansion());
        	h.scale(scale.getScaleX());
        	h.move(-viewportWindow.getX(), -viewportWindow.getY());        
        	g2.setColor(this.padRef
                .get()
                .isSelected() ? Color.GRAY : Layer.Copper.BMask.getColor());

        	h.paint(g2, true);
        }
        	  
        //draw pad shape  
        if(((this.padRef.get().getCopper().getLayerMaskID()&layermaskId)!=0)) {	 
        	h.assign(this.hexagon);
        	h.scale(scale.getScaleX());
        	h.move(-viewportWindow.getX(), -viewportWindow.getY());
        	g2.setColor(this.padRef
                .get()
                .isSelected() ? Color.GRAY : this.padRef.get().getCopper().getColor());
        	h.paint(g2, true);
        }
        }finally {
        	PadFactory.release(h);	
		}
        
        return true;
    }

    @Override
    public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                              ClearanceSource source) {
        g2.setColor(Color.BLACK); 
        Hexagon h = this.hexagon.clone();
        h.grow(source.getClearance());
        h.scale(scale.getScaleX());
        h.move(-viewportWindow.getX(), -viewportWindow.getY());
        h.paint(g2, true);
        
    }

    @Override
    public void printClearance(Graphics2D g2, PrintContext printContext, ClearanceSource source) {
        g2.setColor(printContext.getBackgroundColor());       
        Hexagon h = this.hexagon.clone();
        h.grow(source.getClearance());                
        h.paint(g2, true);

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:padRef.get().getCopper().getColor());  
        hexagon.paint(g2, true);

    }

    @Override
    public void setSize(double width,double height){
        this.hexagon.setWidth(width);
        this.hexagon.rotate(this.padRef.get().getRotate());
    }

    @Override
    public void mirror(Line line) {
        hexagon.mirror(line);
    }
    
    @Override
    public void move(double xoffset, double yoffset) {
       hexagon.move(xoffset, yoffset);

    }

    @Override
    public Point getCenter() {
        return hexagon.pc;    
    }
    @Override
    public Box getBoundingShape() {
       return hexagon.box(); 
    }

    @Override
    public PolygonShape copy(PadShape shape) {
        PolygonShape copy=new PolygonShape(0,0,0,shape);
        copy.hexagon=this.hexagon.clone(); 
        return copy; 
    }

    @Override
    public boolean contains(Point pt) {
        return hexagon.contains(pt);
    }

    @Override
    public void rotate(double angle, Point pt) {
        hexagon.rotate(angle, pt);
    }
    @Override
    public Memento getState(){
        Memento memento=new Memento();
        memento.saveStateFrom(this);
        return memento;
    }
    public static class Memento implements PadDrawing.Memento<PolygonShape> {
        private double pcx,pcy;
        private double width;

        private double x[],y[];
        
        @Override
        public void loadStateTo(PolygonShape shape) {    
            shape.hexagon.pc.set(pcx,pcy);
            shape.hexagon.width=width;
            int i=0;
            for(Point pt:shape.hexagon.points){
                pt.set(x[i],y[i]);
                i++;               
            }            

        }

        @Override
        public void saveStateFrom(PolygonShape shape) {
            pcx=shape.hexagon.pc.x;
            pcy=shape.hexagon.pc.y;
            width=shape.hexagon.width;
            x=new double[shape.hexagon.points.size()];
            y=new double[shape.hexagon.points.size()];
            int i=0;
            for(Point pt:shape.hexagon.points){
                x[i]=pt.x;
                y[i]=pt.y;
                i++;
            }            
        }
    } 
}
