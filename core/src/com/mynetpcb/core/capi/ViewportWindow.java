package com.mynetpcb.core.capi;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

/**
 *Keep track of the visual pcb portion of the screen. PCB is large in size 1mm=1000000px so 1000mm equals 1 000 000 000 pixel!
 * Accomodate faster drawing - no need to draw figures outside of viewable area.
 * @author Sergey Iliev
 */
public class ViewportWindow extends Rectangle{                            
    
    
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
      this.width=width;
      this.height=height;
    }

    
    /**
     *Scaling the viewport around a point.
     * @param xx
     * @param yy
     * @param scale
     */
    public void scalein(int xx,int yy,ScalableTransformation scale){ 
        Point scaledPoint = new Point(this.x + xx, this.y + yy);  
        AffineTransform.getScaleInstance(scale.getScaleRatio(),scale.getScaleRatio()).transform(scaledPoint,scaledPoint);
        this.x=(int)scaledPoint.getX()-xx;
        this.y=(int)scaledPoint.getY()-yy;            
    }
    
    /**
     *Scaling the viewport around a point
     * @param xx
     * @param yy
     * @param scale
     */
    public void scaleout(int xx,int yy,ScalableTransformation scale){ 
      Point scaledPoint = new Point(this.x + xx, this.y + yy);    
        try {
            AffineTransform inverseTransform =
                AffineTransform.getScaleInstance(scale.getScaleRatio(),
                                                 scale.getScaleRatio()).createInverse();
            inverseTransform.transform(scaledPoint, scaledPoint);
        } catch (NoninvertibleTransformException f) {
            f.printStackTrace(System.out);
        }  
        this.x=(int)scaledPoint.getX()-xx;
        this.y=(int)scaledPoint.getY()-yy;       
    }  
}


