package com.mynetpcb.core.capi.text.font;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.FontText;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Node;

public class FontTexture implements Texture{
    private String tag;
    private int id;
    private FontText shape;
    private boolean isSelected;
    
    public FontTexture(String tag,String text, double x, double y, int size) {
        this.tag=tag;        
        this.id=1;        
        this.shape=new FontText(x,y,text,size);       
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
    public int getID() {        
        return id;
    }

    @Override
    public void setID(int id) {
        this.id=id;
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

    @Override
    public Box getBoundingShape() {
        return this.shape.box();
    }

    @Override
    public void clear() {
        // TODO Implement this method
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
        // TODO Implement this method
    }

    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
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
        g2.setColor(Color.WHITE);
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
    public boolean isInRect(Rectangle r) {
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
        return null;
    }

    @Override
    public void setFillColor(Color color) {
        // TODO Implement this method
    }

}
