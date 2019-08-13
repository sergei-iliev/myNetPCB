package com.mynetpcb.core.capi.text;

import java.awt.Font;


public interface Text{
    
    public static final String FONT_NAME=Font.MONOSPACED;//"SansSerif";
    
    public enum Orientation{
        HORIZONTAL,VERTICAL
    }
    
    public enum Style{
        PLAIN,
        BOLD,
        ITALIC;        
    }
    
    public enum Alignment{
       LEFT(Orientation.HORIZONTAL),
       RIGHT(Orientation.HORIZONTAL),
       TOP(Orientation.VERTICAL),
       BOTTOM(Orientation.VERTICAL);
       
       private final Orientation orientation;
       
       private Alignment(Orientation orientation){
         this.orientation=orientation;    
       }
       
       public Alignment Rotate(boolean isClockwise){       
           if(this==LEFT){
              if(isClockwise)
                return TOP;
              else
                return BOTTOM;
              }           
              else if(this==RIGHT){
                if(isClockwise)
                  return BOTTOM;
                else
                  return TOP;           
                }
               
              else if(this==TOP){
                if(isClockwise) 
                   return RIGHT;
                else
                   return LEFT;           
                }               
               else if(this==BOTTOM){
                if(isClockwise)
                    return LEFT;
                else
                   return RIGHT;
               }else
                  throw new IllegalArgumentException("Wrong alignment."); 
                      
       }
       
       public Orientation getOrientation(){
           return orientation;
       }
    
        
       public Alignment Mirror(boolean isHorizontal){
           if(isHorizontal){
            if(this==LEFT)
              return RIGHT;
            else if(this==RIGHT)
              return LEFT;
            else
              return this;
           }else{
            if(this==BOTTOM)
              return TOP;
            else if(this==TOP)
              return BOTTOM;
            else
              return this;  
           }          
       }
    }
}
