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
    public void scaleIn(int xx,int yy,ScalableTransformation scale){            
        double a=(this.getX()+xx)*scale.getScaleRatio();
        double b=(this.getY()+yy)*scale.getScaleRatio();
        this.setX((int)a-xx);
        this.setY((int)b-yy);      
    }
    
    /**
     *Scaling the viewport around a point
     * @param xx
     * @param yy
     * @param scale
     */
    public void scaleOut(int xx,int yy,ScalableTransformation scale){         
            double a=(this.getX()+xx)*scale.getInverseScaleRatio();
            double b=(this.getY()+yy)*scale.getInverseScaleRatio();
	    this.setX((int)a-xx);
	    this.setY((int)b-yy);   
    }  
}


