package com.mynetpcb.core.capi;


import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import java.awt.geom.AffineTransform;


/**
 *Interface to describe the shapes' movement on the board.
 * @author Sergey Iliev
 */
public interface Moveable extends Drawable,Cloneable {

    public void move(double xoffset, double yoffset);
        
    public void mirror(Point A,Point B);
    
    public void mirror(Line line);
    
    public void translate(AffineTransform translate);

    public void rotate(AffineTransform rotation) ;
      
    public void setLocation(double x,double y);

    public Point getCenter();
    
/**
     *
     * @return the order of the shape in Z coordinate when a 
     * click over overlapping shapes occure.
     */
    public long getOrderWeight();
    
}

