package com.mynetpcb.core.capi.unit;


import com.mynetpcb.core.capi.Frameable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.print.Printaware;
import com.mynetpcb.core.utils.Utilities;

import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Rectangle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class UnitFrame implements Frameable,Printaware{
    
    private Box box;
    
    private Color fillColor;
    
    private int width,height,offset;
    
    public UnitFrame(int width,int height) {
        box=new Box(0,0,0,0);
        setSize(width,height);
        this.fillColor=Color.BLACK;
    }

    @Override
    public void setSize(int width, int height) {
       this.width=width;
       this.height=height;
       box.setRect(offset,offset,width-(2*offset),height-(2*offset));
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
         Box box=this.box.clone();        
         box.scale(scale.getScaleX());


         g2.setColor(fillColor); 
         box.setRect(box.getX()-viewportWindow.getX() ,box.getY()-viewportWindow.getY(),box.getWidth(),box.getHeight());   
         box.paint(g2, false);
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
    public Box getBoundingShape() {
        return box;
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
       box.setRect(offset,offset,width-(2*offset),height-(2*offset));
    }
    
    @Override
    public int getOffset(){
      return offset;    
    }

    @Override
    public void print(Graphics2D g2,PrintContext printContext, int layermask) {
        g2.setStroke(new BasicStroke());
        g2.setColor(Color.BLACK);
        box.paint(g2, false);

    }
}
