package com.mynetpcb.core.capi;


import com.mynetpcb.d2.shapes.Point;
import java.awt.geom.AffineTransform;


/**
 *Interface to describe the shapes' movement on the board.
 * @author Sergey Iliev
 */
public interface Moveable extends Drawable,Cloneable {

    public void move(int xoffset, int yoffset);
        
    public void mirror(Point A,Point B);
    
    public void translate(AffineTransform translate);

    public void rotate(AffineTransform rotation) ;
      
    public void setLocation(int x,int y);

    public Point getCenter();
    
/**
     *
     * @return the order of the shape in Z coordinate when a 
     * click over overlapping shapes occure.
     */
    public long getOrderWeight();
    
}
