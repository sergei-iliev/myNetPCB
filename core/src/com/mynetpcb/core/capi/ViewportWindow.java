package com.mynetpcb.core.capi;

import com.mynetpcb.d2.shapes.Box;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 *Keep track of the visual pcb portion of the screen. PCB is large in size 1mm=1000000px so 1000mm equals 1 000 000 000 pixel!
 * Accomodate faster drawing - no need to draw figures outside of viewable area.
 * @author Sergey Iliev
 */
public class ViewportWindow extends Box{                            
    
    
    public ViewportWindow(){
        super(0,0,0,0);
    }
    
    public ViewportWindow(int x,int y,int width,int height) {
        super(x, y, width, height);
    }
    /**
     *Change the viewable size as component window changes.
     * @param width
     * @param height
     */
    public void setSize(int width,int height){
      this.setRect(getX(), getY(), width, height);        
    }

    public void setLocation(int x,int y){
        this.setRect(x, y, getWidth(), getHeight());                
    }
    public void setX(int x){
        this.setRect(x, getY(), getWidth(), getHeight());                 
    }
    
    public void setY(int y){
        this.setRect(getX(), y, getWidth(), getHeight());                
    }
    
    /**
     *Scaling the viewport around a point.
     * @param xx
     * @param yy
     * @param scale
     */
    public void scalein(int xx,int yy,ScalableTransformation scale){ 
        Point2D scaledPoint = new Point2D.Double(this.getX() + xx, this.getY() + yy);  
        AffineTransform.getScaleInstance(scale.getScaleRatio(),scale.getScaleRatio()).transform(scaledPoint,scaledPoint);
        this.setRect(scaledPoint.getX()-xx, scaledPoint.getY()-yy, getWidth(), getHeight());
    }
    
    /**
     *Scaling the viewport around a point
     * @param xx
     * @param yy
     * @param scale
     */
    public void scaleout(int xx,int yy,ScalableTransformation scale){ 
      Point2D scaledPoint = new Point2D.Double(this.getX() + xx, this.getY() + yy);    
        try {
            AffineTransform inverseTransform =
                AffineTransform.getScaleInstance(scale.getScaleRatio(),
                                                 scale.getScaleRatio()).createInverse();
            inverseTransform.transform(scaledPoint, scaledPoint);
        } catch (NoninvertibleTransformException f) {
            f.printStackTrace(System.out);
        }  
        
        this.setRect(scaledPoint.getX()-xx, scaledPoint.getY()-yy, getWidth(), getHeight());        
    }  
}


