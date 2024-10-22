package com.mynetpcb.core.capi.shape;


import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


/**
 *Represent a coordinate system origin.Could be shifted/translated on canvas.
 * Used to recalculate footprint shapes x,y positions
 * Default orogin is at (0,0)
 * @author Sergey Iliev
 */
public class CoordinateSystem extends Shape {
    private final Line line;
    private final Point origin;

    public CoordinateSystem(Unit owningUnit) {
        super(0,0);
        this.origin=new Point(0,0);
        this.line=new Line(0, 0, 0, 0);
        setOwningUnit(owningUnit);
        this.selectionRectWidth=Utilities.DISTANCE;
    }
    public CoordinateSystem(Unit owningUnit,int selectionRectWidth) {
        this(owningUnit);
        this.selectionRectWidth=selectionRectWidth;
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
           this.getOwningUnit().getGrid().snapToGrid(origin);        
        }
        return null;
        
    }
    @Override
    public Box getBoundingShape() {
        return Box.fromRect(origin.x-this.selectionRectWidth/2,origin.y-this.selectionRectWidth/2,this.selectionRectWidth,this.selectionRectWidth);        
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
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke());
        line.setLine(0, this.origin.y, this.getOwningUnit().getWidth(),
                        this.origin.y);
        line.scale(scale.getScaleX());
        line.move(-viewportWindow.getX(),- viewportWindow.getY());
        line.paint(g2,true);
        
        
        line.setLine(this.origin.x, 0, this.origin.x, this.getOwningUnit().getHeight());
        line.scale(scale.getScaleX());
        line.move(-viewportWindow.getX(),- viewportWindow.getY());                
        line.paint(g2,true);

    }

}
