package com.mynetpcb.core.capi.text.font;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.BaseFontText;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.NoSuchElementException;

import org.w3c.dom.Node;

public class SymbolFontTexture implements Texture{
    private String tag;
    public BaseFontText shape;
    private boolean isSelected;
    private Color fillColor;
    private boolean isTextLayoutVisible;
    
    public SymbolFontTexture(String text,String tag, double x, double y, int alignment,int fontSize,int fontStyle){
        this.tag=tag;
        this.shape=new BaseFontText(x,y,text,alignment,fontSize,fontStyle);    
        this.fillColor=Color.BLACK;
    }
    @Override
    public SymbolFontTexture clone() throws CloneNotSupportedException {
        SymbolFontTexture copy=(SymbolFontTexture)super.clone();
        copy.shape=this.shape.clone();
        return copy;
    }    
    @Override
    public boolean isEmpty() {
        return this.shape.text == null || this.shape.text.length() == 0;
    }

    @Override
    public Point getAnchorPoint() {
        return shape.anchorPoint;
    }

    @Override
    public String getTag() {        
        return tag;
    }

    @Override
    public void setTag(String tag) {
       this.tag=tag;
    }

    @Override
    public String getText() {        
        return shape.text;
    }

    @Override
    public void setText(String text) {
        this.shape.setText(text);
    }
    
    public Alignment getAlignment() {        
        return Alignment.from(shape.alignment);
    }

    public void setAlignment(Alignment alignment) {
        this.shape.alignment=alignment.ordinal();
    }
    @Override
    public void setStyle(Texture.Style style) {
        this.shape.setStyle(style.ordinal());
    }
    @Override
    public Texture.Style getStyle() {        
        return Style.valueOf(this.shape.fontStyle);
    }
    @Override
    public void move(double xoffset, double yoffset) {        
        this.shape.move(xoffset, yoffset);
    }
    @Override
    public void rotate(double alpha, Point pt) {                   
        this.shape.anchorPoint.rotate(alpha,pt);
        Alignment alignment=Texture.Alignment.from(this.shape.alignment);            
        if(alpha<0){  //clockwise                        
            this.shape.alignment=alignment.rotate(true).ordinal();
        }else{           
           this.shape.alignment=alignment.rotate(false).ordinal(); 
        }
    }
    /*
     * Take into account text offset from anchro point when rotating
     */
    public void setRotation(double angle, Point center){
       Orientation oldorientation=Alignment.getOrientation(this.shape.alignment);       
       this.rotate(angle, center);
       if(angle<0){  //clockwise              
               if(oldorientation == Orientation.HORIZONTAL){
                       this.shape.anchorPoint.set(this.shape.anchorPoint.x+(this.shape.metrics.ascent-this.shape.metrics.descent),this.shape.anchorPoint.y);            
               }
            }else{              
               if(oldorientation == Orientation.VERTICAL){
                       this.shape.anchorPoint.set(this.shape.anchorPoint.x,this.shape.anchorPoint.y+(this.shape.metrics.ascent-this.shape.metrics.descent));                   
               }
            }               
    }    
    @Override
    public void mirror(Line line) {        
        Alignment alignment=Texture.Alignment.from(this.shape.alignment);  
        this.shape.mirror(line);
        if (line.isVertical()) { //right-left mirroring
                this.shape.alignment = alignment.mirror(true).ordinal();
        } else { //***top-botom mirroring
                this.shape.alignment = alignment.mirror(false).ordinal();            
        }        
    }
    /*
     * Take into account alignment changes 
     */
    public void setMirror(Line line) {  
        Alignment alignment=Texture.Alignment.from(this.shape.alignment); 
        this.mirror(line);      
        if (line.isVertical()) { //right-left mirroring
            if (this.shape.alignment == alignment.ordinal()) {
                this.shape.anchorPoint.set(this.shape.anchorPoint.x +
                                        (this.shape.metrics.ascent - this.shape.metrics.descent),this.shape.anchorPoint.y);
            }
        } else { //***top-botom mirroring          
            if (this.shape.alignment == alignment.ordinal()) {
                this.shape.anchorPoint.set(this.shape.anchorPoint.x,this.shape.anchorPoint.y +(this.shape.metrics.ascent - this.shape.metrics.descent));
            }
        }         
    }

    @Override
    public Box getBoundingShape() {        
        return this.shape.box();
    }

    @Override
    public void clear() {
       
    }

    @Override
    public int getSize() {
        return shape.fontSize;
    }

    @Override
    public void setSize(int size) {
        shape.setSize(size);
    }

    @Override
    public void copy(Texture _copy) {
        SymbolFontTexture copy=(SymbolFontTexture)_copy;
        this.tag=copy.tag;
        this.shape.anchorPoint.set(copy.shape.anchorPoint.x,copy.shape.anchorPoint.y); 
        this.shape.alignment = copy.shape.alignment;
        this.shape.text=copy.shape.text;
        this.shape.fontStyle=copy.shape.fontStyle;
        this.fillColor=copy.fillColor;
        this.shape.setSize(copy.shape.fontSize);  
    }

    @Override
    public void fromXML(Node node) {
        if (node == null || node.getTextContent().length()==0) {
            this.shape.text = "";
            return;
        }
        
        String[] tokens=node.getTextContent().split(",");
        this.shape.anchorPoint.set(Double.parseDouble(tokens[1]),
                Double.parseDouble(tokens[2]));     
        this.shape.alignment=Texture.Alignment.valueOf(tokens[3]).ordinal();
        try{
            this.shape.fontStyle=Texture.Style.valueOf(tokens[4].toUpperCase()).ordinal();        
        }catch(ArrayIndexOutOfBoundsException|NoSuchElementException e){
        }
        this.shape.text=(tokens[0]);
        try{
          this.shape.setSize(Integer.parseInt(tokens[5]));
        }catch(Exception e){
          this.shape.setSize(8);  
        }

    }

    @Override
    public String toXML() {
        return (this.isEmpty() ? "" :
                this.shape.text + "," + Utilities.roundDouble(this.shape.anchorPoint.x,1) + "," + Utilities.roundDouble(this.shape.anchorPoint.y,1) +
                "," + Texture.Alignment.from(this.shape.alignment)+","+Texture.Style.valueOf(this.shape.fontStyle)+","+this.shape.fontSize);
    }

    @Override
    public Texture.Memento createMemento() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if(this.isEmpty()){
                  return;       
        }
        if(shape.fontSize*scale.getScaleX()<7){            
           return;
        }
        
        g2.setColor(fillColor);
        BaseFontText t=this.shape.clone();
        t.scale(scale.getScaleX());
        t.move(-viewportWindow.getX(),- viewportWindow.getY());     
        t.paint(g2,true);                                       
        if(this.isTextLayoutVisible){
               g2.setColor(Color.blue);
               Box box=this.shape.box();
               box.scale(scale.getScaleX());
               box.move(-viewportWindow.getX(),- viewportWindow.getY());                            
               box.paint(g2,false);
        }
        if(this.isSelected){
            g2.setColor(Color.BLUE);
            t.anchorPoint.paint(g2,false);            
        }

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        Color color=printContext.isBlackAndWhite()?Color.BLACK:this.getFillColor(); 
        g2.setColor(color);
        this.shape.paint(g2, false);
    }
    @Override
    public boolean isClicked(int x, int y) {
        if (this.shape.text == null || this.shape.text.length() == 0){
            return false;
        } 
        return this.shape.box().contains(x,y);
    }

    @Override
    public boolean isInRect(Box r) {
        // TODO Implement this method
        return false;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected=isSelected;        
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public  void setTextLayoutVisible(boolean visible){
        this.isTextLayoutVisible = visible;
    };
    @Override
    public Color getFillColor() {
        return fillColor;
    }
    
    @Override
    public void set(double x, double y){
      this.shape.anchorPoint.set(x,y);     
    }
    
    @Override
    public void setFillColor(Color color) {
       this.fillColor=color;
    }

    
    public static class Memento  implements Texture.Memento{
         private String text;
      
         private int alignment,fontSize,fontStyle;                 
        
         private double x;

         private double y;

         private String tag;

         //private int id;
         
        

         public void loadStateTo(Texture _symbol) {
             SymbolFontTexture symbol=(SymbolFontTexture)_symbol;
             symbol.shape.alignment=this.alignment;
             symbol.shape.fontSize=this.fontSize;
             symbol.shape.fontStyle=this.fontStyle;
             symbol.shape.anchorPoint.set(x, y);
             symbol.tag = this.tag;   
             symbol.setText(text);
         }
        @Override
         public void saveStateFrom(Texture _symbol) {
             SymbolFontTexture symbol=(SymbolFontTexture)_symbol;
             //this.id=symbol.id;
             x = symbol.shape.anchorPoint.x;
             y = symbol.shape.anchorPoint.y;
             this.tag = symbol.tag;
             this.text = symbol.shape.text;
             this.fontSize=symbol.shape.fontSize;
             this.fontStyle=symbol.shape.fontStyle;
             this.alignment=symbol.shape.alignment;
         }
         
    //         @Override
    //         public int getId() {
    //             return id;
    //         }

         @Override
         public boolean equals(Object obj) {
             if (this == obj) {
                 return true;
             }
             if (!(obj instanceof SymbolFontTexture.Memento)) {
                 return false;
             }
             SymbolFontTexture.Memento other = (SymbolFontTexture.Memento)obj;
             return (                     
                     other.tag.equals(this.tag) &&
                     other.text.equals(this.text) &&
                     Utils.EQ(other.x,this.x)  &&
                     Utils.EQ(other.y,this.y) &&
                     other.alignment==this.alignment &&
                     other.fontSize==this.fontSize &&
                     other.fontStyle==this.fontStyle);
         }

         @Override
         public int hashCode() {
            int hash =
             31 +/*this.id*/+ + this.fontSize+this.fontStyle+this.alignment+
             this.tag.hashCode() + this.text.hashCode() + Double.hashCode(x) +Double.hashCode(y);
             return hash;
         }
    }    
}
