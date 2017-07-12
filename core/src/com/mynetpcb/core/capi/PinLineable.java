package com.mynetpcb.core.capi;


import com.mynetpcb.core.capi.text.Texture;

import java.awt.Point;

import java.util.Collection;

/**
 *Single pin manipulation for Symbol and Circuit
 * @author Sergey Iliev
 */
public interface PinLineable extends Pinable{
    public static class Pair{
       private  Point A=new Point();
       private  Point B=new Point();


        public Point getA() {
            return A;
        }

        public Point getB() {
            return B;
        }

    }
   /**
     *Each pin is represented by a line
     * @return
     */
   public  Pair getPinPoints();
   
   /**
     *Each pin has text - reference and unit
     * @return
     */
   public Collection<Texture> getPinText();
   
   /**
    * Each pin has orientation
    */
   public Orientation getOrientation();
   
}
