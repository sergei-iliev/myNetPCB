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
