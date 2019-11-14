package com.mynetpcb.core.capi.text;

import com.mynetpcb.core.capi.Drawable;
import com.mynetpcb.core.capi.print.Printable;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import org.w3c.dom.Node;


/*
 * Glyph and Font texture blueprint
 */
public interface Texture extends Drawable,Printable,Cloneable{
    
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
    
    //public int getLayermaskId();
    
    //public void setLayermaskId(int layermaskId);
        
    public Memento createMemento();
    
    public void set(double x, double y);
    
    public interface Memento{
        
        public void loadStateTo(Texture symbol);
            
        public void saveStateFrom(Texture symbol) ;
        
        public int hashCode();
            
    }
}
