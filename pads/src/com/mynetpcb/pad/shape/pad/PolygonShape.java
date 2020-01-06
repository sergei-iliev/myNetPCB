package com.mynetpcb.pad.shape.pad;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.GeometricFigure;
import com.mynetpcb.d2.shapes.Hexagon;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

public class PolygonShape implements PadDrawing {
    private WeakReference<Shape> padRef;
    private Hexagon hexagon;

    public PolygonShape(double x, double y, double width, Shape pad) {
        padRef = new WeakReference<>(pad);
        this.hexagon=new Hexagon(x,y,width);
    }
    @Override
    public GeometricFigure getGeometricFigure(){
      return hexagon;  
    }
    @Override
    public boolean paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        //check if outside of visible window
        Box rect = this.hexagon.box();
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


        Hexagon h = this.hexagon.clone();
        h.scale(scale.getScaleX());
        h.move(-viewportWindow.getX(), -viewportWindow.getY());
        h.paint(g2, true);
        return true;
    }

    @Override
    public void drawClearance(Graphics2D graphics2D, ViewportWindow viewportWindow, AffineTransform affineTransform,
                              ClearanceSource clearanceSource) {
        // TODO Implement this method

    }

    @Override
    public void printClearance(Graphics2D g2, PrintContext printContext, ClearanceSource clearanceSource) {
        // TODO Implement this method

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:padRef.get().getCopper().getColor());  
        hexagon.paint(g2, true);

    }

    @Override
    public void setSize(double width,double height){
        this.hexagon.setWidth(width);
        this.hexagon.rotate(this.padRef.get().getRotation());
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
    public PolygonShape copy(Shape shape) {
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
