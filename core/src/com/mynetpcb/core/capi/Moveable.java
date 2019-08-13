package com.mynetpcb.core.capi;

import java.awt.Point;
import java.awt.geom.AffineTransform;


/**
 *Interface to describe the shapes' movement on the board.
 * @author Sergey Iliev
 */
public interface Moveable extends Drawable,Cloneable {
//    public enum Rotate{
//        LEFT,RIGHT
//    };
    
//    public enum Mirror{
//        HORIZONTAL,VERTICAL
//    };
    public void Move(int xoffset, int yoffset);
        
    public void Mirror(Point A,Point B);
    
    public void Translate(AffineTransform translate);

    public void Rotate(AffineTransform rotation) ;
      
    public void setLocation(int x,int y);

    public Point getCenter();
    
//    public int getCenterX();
//    
//    public int getCenterY();
/**
     *
     * @return the order of the shape in Z coordinate when a 
     * click over overlapping shapes occure.
     */
    public long getOrderWeight();
    
}

