package com.mynetpcb.pad.shape.pad;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.GeometricFigure;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

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
    public boolean paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        //check if outside of visible window
        Box rect = this.obround.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
            return false;
        }
        g2.setColor(this.padRef
                        .get()
                        .isSelected() ? Color.GRAY : this.padRef
                                                         .get()
                                                         .getCopper()
                                                         .getColor());


        Obround o = this.obround.clone();
        o.scale(scale.getScaleX());
        o.move(-viewportWindow.getX(), -viewportWindow.getY());
        o.paint(g2, true);
        return true;
    }

    @Override
    public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                              ClearanceSource source) {
                                       
        g2.setColor(Color.BLACK);        
        Obround o=this.obround.clone();
        o.grow(source.getClearance());

        
        o.scale(scale.getScaleX());
        o.move(-viewportWindow.getX(), -viewportWindow.getY());
        o.paint(g2, true);
    }

    @Override
    public void printClearance(Graphics2D g2, PrintContext printContext, ClearanceSource source) {
        Obround o=this.obround.clone();
        g2.setColor(printContext.getBackgroundColor());          
        o.grow(source.getClearance());
        o.paint(g2, true);
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
