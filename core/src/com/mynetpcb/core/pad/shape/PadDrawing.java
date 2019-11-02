package com.mynetpcb.core.pad.shape;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.print.Printable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public interface PadDrawing extends Printable {
        
        public boolean paint(Graphics2D  g2, ViewportWindow viewportWindow, AffineTransform scale);

        public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                                  ClearanceSource source);

        public void printClearance(Graphics2D g2,PrintContext printContext, ClearanceSource source);

        public void setWidth(int width);

        public void setHeight(int height);
        
        public void move(double x,double y);
        
        public Point getCenter();
        
        public Box getBoundingShape();
            
        public <D extends PadDrawing> D copy(Shape pad);

        public boolean contains(Point pt);
        
        public void rotate(double rotate, Point pt) ;

}
