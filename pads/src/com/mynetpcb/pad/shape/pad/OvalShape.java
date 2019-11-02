package com.mynetpcb.pad.shape.pad;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

public class OvalShape implements PadDrawing {
    private WeakReference<Shape> padRef;
    private Obround obround;

    public OvalShape(double x, double y, double width, double height, Shape pad) {
        padRef = new WeakReference<>(pad);
        this.obround = new Obround(new Point(x, y), width, height);
    }

    public OvalShape copy(Shape pad) {
        OvalShape copy = new OvalShape(this.obround
                                           .getCenter()
                                           .x, obround.getCenter().y, 0, 0, pad);
        copy.obround = this.obround.clone();
        return copy;
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
    public void drawClearance(Graphics2D graphics2D, ViewportWindow viewportWindow, AffineTransform affineTransform,
                              ClearanceSource clearanceSource) {
        // TODO Implement this method


    }

    @Override
    public void printClearance(Graphics2D graphics2D, PrintContext printContext, ClearanceSource clearanceSource) {
        // TODO Implement this method


    }

    @Override
    public void setWidth(int width) {
        // TODO Implement this method
    }

    @Override
    public void setHeight(int height) {
        // TODO Implement this method
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
    public void print(Graphics2D graphics2D, PrintContext printContext, int i) {
        // TODO Implement this method


    }
}
