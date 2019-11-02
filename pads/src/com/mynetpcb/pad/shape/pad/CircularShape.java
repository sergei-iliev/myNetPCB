package com.mynetpcb.pad.shape.pad;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

public class CircularShape implements PadDrawing {
        private WeakReference<Shape> padRef; 
        private Circle circle;
        
        public CircularShape(double x,double y,double width,Shape pad){
            padRef=new WeakReference<>(pad);
            this.circle=new Circle(new Point(x,y), width/2);                        
        }
        @Override
        public CircularShape copy(Shape pad){
              CircularShape copy=new CircularShape(this.circle.pc.x,this.circle.pc.y,0,pad);
              copy.circle.r=this.circle.r;
              return copy;  
            } 
        @Override
        public boolean paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
            
            //check if outside of visible window
            Box rect = this.circle.box();
            rect.scale(scale.getScaleX());
            if (!rect.intersects(viewportWindow)) {
                return false;
            }
            g2.setColor(this.padRef.get().isSelected() ? Color.GRAY : this.padRef.get().getCopper().getColor());

               
            Circle c=this.circle.clone();
            c.scale(scale.getScaleX());
            c.move(-viewportWindow.getX(),- viewportWindow.getY());
            c.paint(g2,true);                                             
            return true;
        }

        @Override
        public void drawClearance(Graphics2D graphics2D, ViewportWindow viewportWindow, AffineTransform affineTransform,
                                  ClearanceSource clearanceSource) {
            // TODO Implement this method

        }

        @Override
        public void printClearance(Graphics2D graphics2D, PrintContext printContext, ClearanceSource clearanceSource) {
            // TODO Implement this method

        }
        @Override
        public void rotate(double alpha, Point pt) {
            this.circle.rotate(alpha,pt);       
        }
        @Override
        public boolean contains(Point pt){
            return this.circle.contains(pt);
        }
        
        @Override
        public Point getCenter(){
          return circle.pc;  
        }
        @Override
        public Box getBoundingShape(){
            return this.circle.box();
        }
        public void move(double xoffset,double yoffset) {
            this.circle.move(xoffset,yoffset);
        }
        
        @Override
        public void setWidth(int width) {
            this.circle.r=width/2;
        }

        @Override
        public void setHeight(int i) {

        }

        @Override
        public void print(Graphics2D graphics2D, PrintContext printContext, int i) {
            // TODO Implement this method

        }
    }