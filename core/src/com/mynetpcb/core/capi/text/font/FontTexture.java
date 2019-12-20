package com.mynetpcb.core.capi.text.font;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.FontText;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Node;

public class FontTexture implements Texture{
    private String tag;
    private FontText shape;
    private boolean isSelected;
    private Color fillColor;
    
    public FontTexture(String tag,String text, double x, double y, int size) {
        this.tag=tag;              
        this.shape=new FontText(x,y,text,size);       
        this.fillColor=Color.WHITE;
    }
    @Override
    public FontTexture clone() throws CloneNotSupportedException {
        FontTexture copy=(FontTexture)super.clone();
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
        shape.setText(text);
    }

    @Override
    public void move(double xoffset, double yoffset) {
        this.shape.move(xoffset, yoffset); 
    }
    
    public void setRotation(double alpha,Point pt){ 
      this.shape.rotate(alpha,pt);
    }
    
    @Override
    public void mirror(Line line) {
       
    }
    public void setSide(Layer.Side side, Line line, double angle) { 
        this.shape.mirror(line); 
        this.shape.rotate=angle;
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
    public void copy(Texture texture) {
        // TODO Implement this method
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
        
        this.shape.setText(tokens[0]);
        this.shape.setSize(Integer.parseInt(tokens[5]));
        try{
          this.shape.rotate(Double.parseDouble(tokens[6]));
        }catch(Exception e){
          this.shape.rotate(0);  
        }
    }

    @Override
    public String toXML() {
        return (this.shape.text=="" ? "" :
            this.shape.text + "," + this.shape.anchorPoint.x + "," + this.shape.anchorPoint.y +
            ",,,"+this.shape.fontSize+"," +this.shape.rotate);
    }

    @Override
    public Texture.Memento createMemento() {
        // TODO Implement this method
        return null;
    }
    @Override
    public boolean isClicked(int x, int y) {
            if (this.shape.text == null || this.shape.text.length() == 0){
                return false;
            } 
            return this.shape.contains(new Point(x,y));
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
        FontText t=this.shape.clone();
        t.scale(scale.getScaleX());
        t.move(-viewportWindow.getX(),- viewportWindow.getY());     
        t.paint(g2,true);

        if(this.isSelected){
            g2.setColor(Color.BLUE);
            t.anchorPoint.paint(g2,false);            
        }
        
    }
    
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        

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
      
         private int fontSize,fontStyle;
        
         private double rotate;
        
         private double x;

         private double y;

         private String tag;

         //private int id;
         
        

         public void loadStateTo(Texture _symbol) {
             FontTexture symbol=(FontTexture)_symbol;
             //symbol.id=this.id;
             symbol.shape.fontSize=this.fontSize;
             symbol.shape.fontStyle=this.fontStyle;
             symbol.shape.anchorPoint.set(x, y);
             symbol.tag = this.tag;   
             symbol.shape.rotate=this.rotate;
             symbol.setText(text);
         }
        @Override
         public void saveStateFrom(Texture _symbol) {
             FontTexture symbol=(FontTexture)_symbol;
             //this.id=symbol.id;
             x = symbol.shape.anchorPoint.x;
             y = symbol.shape.anchorPoint.y;
             this.tag = symbol.tag;
             this.text = symbol.shape.text;
             this.fontSize=symbol.shape.fontSize;
             this.fontStyle=symbol.shape.fontStyle;
             this.rotate=symbol.shape.rotate;
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
             if (!(obj instanceof FontTexture.Memento)) {
                 return false;
             }
             FontTexture.Memento other = (FontTexture.Memento)obj;
             return (
                     //other.id==this.id&&
                     other.tag.equals(this.tag) &&
                     other.text.equals(this.text) &&
                     Utils.EQ(other.x,this.x)  &&
                     Utils.EQ(other.y,this.y) &&
                     Utils.EQ(other.rotate,this.rotate) &&
                     other.fontSize==this.fontSize &&
                     other.fontStyle==this.fontStyle);
         }

         @Override
         public int hashCode() {
            int hash =
             31 +/*this.id*/+ + this.fontSize+fontStyle+
             this.tag.hashCode() + this.text.hashCode() + Double.hashCode(x) +Double.hashCode(y)+Double.hashCode(rotate);
             return hash;
         }
    }
}
