package com.mynetpcb.core.capi;


import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;

import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;

import com.mynetpcb.d2.shapes.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;


/**
 *Represent a coordinate system origin.Could be shifted/translated on canvas.
 * Used to recalculate footprint shapes x,y positions
 * Default orogin is at (0,0)
 * @author Sergey Iliev
 */
public class CoordinateSystem extends Shape {

    private final Point origin;

    public CoordinateSystem(Unit owningUnit) {
        super(0,0);
        this.origin=new Point(0,0);
        setOwningUnit(owningUnit);
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
           return super.alignToGrid(isRequired);
        }else{
            return null;
        }
    }
    @Override
    public Box getBoundingShape() {
        return new Box(origin.x-this.selectionRectWidth/2,origin.y-this.selectionRectWidth/2,this.selectionRectWidth,this.selectionRectWidth);        
    }
    public Point getOrigin(){
        return origin;
    }
    
    @Override
    public void clear() {
    }

    @Override
    public CoordinateSystem clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clonning is not supported");
    }

    @Override
    public String getDisplayName() {
        return "Coordinate Origin";
    }

    @Override
    public void move(double xoffset, double yoffset) {
        origin.move(xoffset, yoffset);
    }

    /*
     * Reset origin to a new position
     * Validate input
     */

    public void reset(double x, double y) {
        if (x < 0) {
            x = 0;
        } else if (x > getOwningUnit().getWidth()) {
            x = getOwningUnit().getWidth();
        }
        if (y < 0) {
            y = 0;
        } else if (y > getOwningUnit().getWidth()) {
            y = getOwningUnit().getWidth();
        }
        origin.x=x;
        origin.y=y;
    }


    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        if (origin.x == 0 && origin.y == 0) {
            return;
        }
//        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(Line2D.class);
//        Line2D horizontalLine = (Line2D)provider.getShape();
//        Line2D verticalLine = (Line2D)provider.getShape();
//        
//        g2.setColor(Color.BLUE);
//        
//        //horizontal
//        horizontalLine.setLine(0, origin.x, getOwningUnit().getWidth(), origin.y);
//        Utilities.setScaleLine(horizontalLine, horizontalLine, scale);
//        //vertical
//        verticalLine.setLine(origin.x, 0, origin.x, getOwningUnit().getHeight());
//        Utilities.setScaleLine(verticalLine, verticalLine, scale);
//        
//        if ((!horizontalLine.intersects(viewportWindow))&&(!verticalLine.intersects(viewportWindow))) {
//            provider.reset();
//            return;
//        }
//        
//        horizontalLine.setLine(horizontalLine.getX1() - viewportWindow.x, horizontalLine.getY1() - viewportWindow.y,
//                       horizontalLine.getX2() - viewportWindow.x, horizontalLine.getY2() - viewportWindow.y);
//        g2.draw(horizontalLine);
//
//    
//        verticalLine.setLine(verticalLine.getX1() - viewportWindow.x, verticalLine.getY1() - viewportWindow.y,
//                       verticalLine.getX2() - viewportWindow.x, verticalLine.getY2() - viewportWindow.y);
//        g2.draw(verticalLine);
//
//        provider.reset();
    }

}
