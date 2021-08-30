package com.mynetpcb.core.capi;

import com.mynetpcb.d2.shapes.Box;

/**
 *Keep track of the visual pcb portion of the screen. PCB is large in size 1mm=1000000px so 1000mm equals 1 000 000 000 pixel!
 * Accomodate faster drawing - no need to draw figures outside of viewable area.
 * @author Sergey Iliev
 */
public class ViewportWindow extends Box{                            
    
    
    public ViewportWindow(){
        super(0,0,0,0);
    }
    
    public ViewportWindow(double x,double y,double width,double height) {
        super(x, y, width, height);
    }
    /**
     *Change the viewable size as component window changes.
     * @param width
     * @param height
     */
    public void setSize(double width,double height){
      this.setRect(getX(), getY(), width, height);        
    }

    public void setLocation(double x,double y){
        this.setRect(x, y, getWidth(), getHeight());                
    }
    public void setX(double x){
        this.setRect(x, getY(), getWidth(), getHeight());                 
    }
    
    public void setY(double y){
        this.setRect(getX(), y, getWidth(), getHeight());                
    }
    
    /**
     *Scaling the viewport around a point.
     * @param xx
     * @param yy
     * @param scale
     */
    public void scaleIn(double xx,double yy,ScalableTransformation scale){            
        double a=(this.getX()+xx)*scale.getScaleRatio();
        double b=(this.getY()+yy)*scale.getScaleRatio();
        this.setX(a-xx);
        this.setY(b-yy);      
    }
    
    /**
     *Scaling the viewport around a point
     * @param xx
     * @param yy
     * @param scale
     */
    public void scaleOut(double xx,double yy,ScalableTransformation scale){         
            double a=(this.getX()+xx)*scale.getInverseScaleRatio();
            double b=(this.getY()+yy)*scale.getInverseScaleRatio();
            this.setX(a-xx);
            this.setY(b-yy);   
    }  
}


