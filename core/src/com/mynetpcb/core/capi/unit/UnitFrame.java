package com.mynetpcb.core.capi.unit;


import com.mynetpcb.core.capi.Frameable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.print.Printaware;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class UnitFrame implements Frameable,Printaware{
    
    private Rectangle rectangle;
    
    private Color fillColor;
    
    private int width,height,offset;
    
    public UnitFrame(int width,int height) {
        rectangle=new Rectangle();
        setSize(width,height);
        this.fillColor=Color.BLACK;
    }

    @Override
    public void setSize(int width, int height) {
       this.width=width;
       this.height=height;
       rectangle.setRect(offset,offset,width-(2*offset),height-(2*offset));
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
          Rectangle2D scaledRect = Utilities.getScaleRect(rectangle ,scale); 
          if(scaledRect.contains(viewportWindow)){
              return;
          }
          g2.setColor(fillColor); 
          scaledRect.setRect(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getHeight());   
          g2.draw(scaledRect);
    }

    @Override
    public boolean isClicked(int x, int y) {
        return false;
    }

    @Override
    public boolean isInRect(Rectangle r) {
        return false;
    }

    @Override
    public Shape getBoundingShape() {
        return rectangle;
    }

    @Override
    public void setSelected(boolean isSelected) {
    }

    @Override
    public boolean isSelected() {
        return false;
    }
    @Override
    public Color getFillColor(){
        return fillColor;
    }
    @Override
    public void setFillColor(Color color){
        this.fillColor=color;
    }

    @Override
    public void setOffset(int offset) {
       this.offset=offset;
       rectangle.setRect(offset,offset,width-(2*offset),height-(2*offset));
    }
    
    @Override
    public int getOffset(){
      return offset;    
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext, int layermask) {
        g2.setStroke(new BasicStroke());
        g2.setColor(Color.BLACK);
        g2.draw(rectangle);       

    }
}
