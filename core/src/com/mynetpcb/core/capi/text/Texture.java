package com.mynetpcb.core.capi.text;

import com.mynetpcb.core.capi.Drawable;
import com.mynetpcb.core.capi.print.Printaware;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Node;


/*
 * Glyph and Font texture blueprint
 */
public interface Texture extends Drawable,Printaware,Cloneable{
    
    public boolean isEmpty();

    public Text.Alignment getAlignment();
    
    public void setAlignment(Text.Alignment alignment);
    
    public void setOrientation(Text.Orientation orientation);
    
    public Point getAnchorPoint();
    
    public String getTag();

    public void setTag(String tag);
    
    public int getID();
    
    public void setID(int id);
    
    public String getText();
    
    public void setText(String text);
    
    public void Move(int xoffset,int yoffset);
    
    public void Rotate(AffineTransform rotation);
    
    public void Mirror(Point A,Point B);
    
    public void Translate(AffineTransform transform);
    
    public Rectangle getBoundingShape();
    
    public void Clear(); 
    
    public int getSize();
    
    public void setSize(int size);
    
    public default boolean isTextLayoutVisible(){ return false;};

    public default void setTextLayoutVisible(boolean visible){};
    
    public Texture clone() throws CloneNotSupportedException;
    
    public void copy(Texture texture);
    
    public void fromXML(Node node);
    
    public String toXML();
    
    public int getLayermaskId();
    
    public void setLayermaskId(int layermaskId);
        
    public Memento createMemento();
    
    public interface Memento{
        
        public int getID();
        
        public void loadStateTo(Texture symbol);
            
        public void saveStateFrom(Texture symbol) ;
        
        public int hashCode();
            
    }
}
