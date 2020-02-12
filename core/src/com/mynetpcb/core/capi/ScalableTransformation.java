package com.mynetpcb.core.capi;

import com.mynetpcb.d2.shapes.Box;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 *Scalable processor keeps the scaling state and changes it on request.
 * 1. Increase mode scaleRatio{1,n}
 * 2. Decreasing mode scaleRatio{0,1}
 * @author Sergey Iliev
 */
public class ScalableTransformation implements Cloneable{
    public static int DEFAULT_MAX_SCALE_FACTOR=15;
    
    private double scaleRatio; 
    //{1:2:4:8} 1.25 - 0.8
    private int scaleFactor;
    
    private int maxScaleFactor;
    
    private int minScaleFactor;
    
    private AffineTransform currentTransformation;
    
    public ScalableTransformation() {
         this(1.2);
    }
    
    public ScalableTransformation(double scaleRatio) {
          this.reset(scaleRatio,0,0,DEFAULT_MAX_SCALE_FACTOR);
    }   
    
    public void reset(double scaleRatio,int scaleFactor,int minScaleFactor,int maxScaleFactor){
         this.scaleFactor=scaleFactor;
         this.maxScaleFactor=maxScaleFactor;
         this.minScaleFactor=minScaleFactor;
         this.scaleRatio=scaleRatio;
         currentTransformation=calculateTransformation();
    }
    
    public boolean scaleIn() {              
             scaleFactor++ ;
             if (scaleFactor == maxScaleFactor) {
                 scaleFactor = maxScaleFactor-1;
                 return false;
             }            
             currentTransformation=calculateTransformation();
        
             return true;        
    }
    public double getInverseScaleRatio(){
       return 1/this.scaleRatio;
    }  
    public double getScaleRatio(){
      return scaleRatio;  
    }
    
    public int getScaleFactor(){
      return scaleFactor;  
    }
    
    public void setScaleFactor(int newScaleFactor){
        this.reset(scaleRatio,newScaleFactor,minScaleFactor,maxScaleFactor); 
    }
    
    
    public boolean scaleOut() {
         scaleFactor --;
         if (scaleFactor == minScaleFactor-1) {
                 scaleFactor = minScaleFactor;
                 return false;
         }
         
         currentTransformation=calculateTransformation();
        
         return true;
     }
    
     public Point getInversePoint(Point scaledPoint){
         AffineTransform unscaledTransformation=null;
         try {
             unscaledTransformation = currentTransformation.createInverse();
         } catch (NoninvertibleTransformException e) {
            e.printStackTrace(System.out);
         }
         Point result=new Point();
         unscaledTransformation.transform(scaledPoint,result);    
         return new Point((int) Math.floor(result.getX()+0.5),(int) Math.floor(result.getY()+0.5));
     }

     public Point2D getInversePoint(Point2D scaledPoint){
         AffineTransform unscaledTransformation=null;
         try {
             unscaledTransformation = currentTransformation.createInverse();
         } catch (NoninvertibleTransformException e) {
            e.printStackTrace(System.out);
         }
         Point2D result=new Point2D.Double();
         unscaledTransformation.transform(scaledPoint,result);    
         return result;
     }

     public Box getInverseRect(Box box){
          int s=1;
          if(this.scaleFactor!=0){     
              for(int i=0;i<this.scaleFactor;i++){
                s*=2;
              }
          }
          Box copy=box.clone();
          copy.scale(s);
          return copy;
     }  
     
     private AffineTransform calculateTransformation(){
        AffineTransform currentTransformation;
        if(scaleFactor==0){
            currentTransformation=AffineTransform.getScaleInstance(1,1);
        }else{
            currentTransformation = AffineTransform.getScaleInstance(1,1);       
            for(int i=0;i<scaleFactor;i++)
              currentTransformation.scale(scaleRatio,scaleRatio);
        }
        return currentTransformation;
     }
    
     public AffineTransform getCurrentTransformation(){
       return currentTransformation;  
     }

     public ScalableTransformation clone() throws CloneNotSupportedException{
         ScalableTransformation copy=(ScalableTransformation)super.clone();
         copy.calculateTransformation();
         return copy;
     }
     
     @Override
     public String toString(){
       StringBuffer sb=new StringBuffer();
       sb.append("ScaleRatio="+scaleRatio);
       sb.append(", ScaleFactor="+scaleFactor);
       sb.append(", MinScaleFactor="+minScaleFactor);
       sb.append(", MaxScaleFactor="+maxScaleFactor);
       return sb.toString();
     }



 }
