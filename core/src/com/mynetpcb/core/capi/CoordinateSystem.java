package com.mynetpcb.core.capi;


import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;

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

//    @Override
//    public Rectangle calculateShape() {
//      return new Rectangle(origin.x-this.selectionRectWidth/2,origin.y-this.selectionRectWidth/2,this.selectionRectWidth,this.selectionRectWidth);
//      
//    }
    
    @Override
    public void Clear() {
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
    public void move(int xoffset, int yoffset) {
        origin.move(xoffset, yoffset);
    }

    /*
     * Reset origin to a new position
     * Validate input
     */

    public void Reset(int x, int y) {
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
        origin.set(x,y);
    }


    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        if (origin.x == 0 && origin.y == 0) {
            return;
        }
        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D horizontalLine = (Line2D)provider.getShape();
        Line2D verticalLine = (Line2D)provider.getShape();
        
        g2.setColor(Color.BLUE);
        
        //horizontal
        horizontalLine.setLine(0, getY(), getOwningUnit().getWidth(), getY());
        Utilities.setScaleLine(horizontalLine, horizontalLine, scale);
        //vertical
        verticalLine.setLine(getX(), 0, getX(), getOwningUnit().getHeight());
        Utilities.setScaleLine(verticalLine, verticalLine, scale);
        
        if ((!horizontalLine.intersects(viewportWindow))&&(!verticalLine.intersects(viewportWindow))) {
            provider.reset();
            return;
        }
        
        horizontalLine.setLine(horizontalLine.getX1() - viewportWindow.x, horizontalLine.getY1() - viewportWindow.y,
                       horizontalLine.getX2() - viewportWindow.x, horizontalLine.getY2() - viewportWindow.y);
        g2.draw(horizontalLine);

    
        verticalLine.setLine(verticalLine.getX1() - viewportWindow.x, verticalLine.getY1() - viewportWindow.y,
                       verticalLine.getX2() - viewportWindow.x, verticalLine.getY2() - viewportWindow.y);
        g2.draw(verticalLine);

        provider.reset();
    }

}
