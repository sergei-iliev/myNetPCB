package com.mynetpcb.core.capi.text;

import com.mynetpcb.core.capi.Drawable;
import com.mynetpcb.core.capi.print.Printable;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Font;

import org.w3c.dom.Node;


/*
 * Glyph and Font texture blueprint
 */
public interface Texture extends Drawable,Printable,Cloneable{
    public enum Alignment{
       RIGHT(Orientation.HORIZONTAL),
       TOP(Orientation.VERTICAL),
       LEFT(Orientation.HORIZONTAL),
       BOTTOM(Orientation.VERTICAL);
       
       private final Orientation orientation;
       
       private Alignment(Orientation orientation){
         this.orientation=orientation;    
       }
       public static Alignment from(int align){
           switch(align){
           case 0:
              return RIGHT;
           case 1:
              return TOP;           
           case 2:
              return LEFT;  
           default:
               return BOTTOM;
           }
       }
       public Alignment rotate(boolean isClockwise){       
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
       public static Orientation getOrientation(int alignment){
           if(alignment==0||alignment==2){
                 return  Orientation.HORIZONTAL; 
           }else{
                 return  Orientation.VERTICAL;  
           }
       }
        
       public Alignment mirror(boolean isHorizontal){
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
    public enum Style{
        PLAIN,
        BOLD,
        ITALIC;
        public static Style valueOf(int value){
            if(value==Font.PLAIN){
                return PLAIN;
            }else if(value==Font.BOLD){
                return BOLD;         
            }else if(value==Font.ITALIC){
                return ITALIC;
            }
            return PLAIN;
        }
    }
    
    public enum Orientation{
        HORIZONTAL,
        VERTICAL
    }
    
    public default Style getStyle(){
        return Style.PLAIN;
    }
    public default void setStyle(Style style){
        
    }
    public boolean isEmpty();
    
    public Point getAnchorPoint();
    
    public String getTag();

    public void setTag(String tag);
    
    public String getText();
    
    public void setText(String text);
    
    public void move(double xoffset,double yoffset);
    
    public void mirror(Line line);
    
    public void rotate(double alpha,Point pt);
    
    public Box getBoundingShape();
    
    public void clear(); 
    
    public int getSize();
    
    public void setSize(int size);
    
    public default boolean isTextLayoutVisible(){ return false;};

    public default void setTextLayoutVisible(boolean visible){};
    
    public Texture clone() throws CloneNotSupportedException;
    
    public void copy(Texture texture);
    
    public void fromXML(Node node);
    
    public String toXML();
    
    public default int getLayermaskId(){
        return -1;
    }
    
    //public void setLayermaskId(int layermaskId);
        
    public Memento createMemento();
    
    public void set(double x, double y);
    
    public interface Memento{
        
        public void loadStateTo(Texture symbol);
            
        public void saveStateFrom(Texture symbol) ;
        
        public int hashCode();
            
    }
}
