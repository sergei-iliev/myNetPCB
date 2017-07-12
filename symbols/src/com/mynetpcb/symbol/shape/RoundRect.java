package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Rectangular;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.ResizableShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import java.util.StringTokenizer;

import org.w3c.dom.Node;


public class RoundRect extends ResizableShape implements Rectangular,Externalizable{
    
    private int arc;
    
    private RoundRectangle2D roundRect;
    
    public RoundRect(int x,int y, int width, int height) {
      super(x,y,width,height,1,0);         
      this.roundRect=new RoundRectangle2D.Double();
      this.arc=0;
    }
    public RoundRect() {
      this(0,0,0,0);         
    }  
    @Override
    public RoundRect clone() throws CloneNotSupportedException{
        RoundRect copy= (RoundRect)super.clone();
        copy.roundRect=new RoundRectangle2D.Double();
        return copy;
    }
    
    public void setArc(int arc) {
        this.arc = arc;
    }

    public int getArc() {
        return arc;
    }
    public String getDisplayName() {
        return "Rect";
    }
    
    @Override
    public int getDrawingOrder() {        
        return 99;
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds() ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }       
        roundRect.setRoundRect(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getHeight(),arc*scale.getScaleX(),arc*scale.getScaleY());
 
        g2.setStroke(new BasicStroke((float)(thickness*scale.getScaleX()),1,1));  
        g2.setColor(isSelected()?Color.GRAY:fillColor); 
        if(fill == Fill.EMPTY)   //***empty
          g2.draw(roundRect);
        if(this.getFill() == Fill.FILLED)  //***filled
          g2.fill(roundRect);
        if(this.getFill() == Fill.GRADIENT){   //***gradual
            GradientPaint gp = 
                new GradientPaint(roundRect.getBounds().x, roundRect.getBounds().y, 
                                  Color.white, roundRect.getBounds().x, 
                                  (roundRect.getBounds().y+roundRect.getBounds().height), Color.gray, true);
            g2.setPaint(gp);
            g2.fill(roundRect);
            g2.setColor(Color.black);
            g2.draw(roundRect);
        } 

        if(this.isSelected()){
              this.drawControlShape(g2,viewportWindow,scale);
        } 
    }
    
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        Rectangle2D rect = getBoundingShape().getBounds(); 
        roundRect.setRoundRect(rect.getX() ,rect.getY(),rect.getWidth(),rect.getHeight(),arc,arc);
        
        g2.setStroke(new BasicStroke(thickness,1,1));  
        g2.setColor(Color.BLACK); 
                
        if(fill == Fill.EMPTY)   //***empty
          g2.draw(roundRect);
        if(this.getFill() == Fill.FILLED)  //***filled
          g2.fill(roundRect);
        if(this.getFill() == Fill.GRADIENT){   //***gradual
            GradientPaint gp = 
                new GradientPaint(roundRect.getBounds().x, roundRect.getBounds().y, 
                                  Color.white, roundRect.getBounds().x, 
                                  (roundRect.getBounds().y+roundRect.getBounds().height), Color.gray, true);
            g2.setPaint(gp);
            g2.fill(roundRect);
            g2.setColor(Color.black);
            g2.draw(roundRect);
        } 
    }
    @Override
    public boolean isClicked(int x, int y) {
        roundRect.setFrame(getX(), getY(), getWidth(), getHeight());
        if(roundRect.contains(x,y))
         return true;
        else
         return false; 
    }
    @Override

    public String toString(){
        return getBoundingShape().toString(); 
    }
    public String toXML() {
        return "<rectangle>"+upperLeft.x+","+upperLeft.y+","+getWidth()+","+getHeight()+","+this.getThickness()+","+this.getFill().index +","+this.arc+"</rectangle>\r\n";
    }
    
    public void fromXML(Node node){
        StringTokenizer st=new StringTokenizer(node.getTextContent(),",");   
        Initialize(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));
        setThickness(Byte.parseByte(st.nextToken()));
        setFill(Fill.byIndex(Byte.parseByte(st.nextToken()))); 
        arc=Integer.parseInt(st.nextToken());
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this); 
    }
    
    public static class Memento extends ResizableShape.Memento {
        private int arc;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(ResizableShape shape) {
            super.loadStateTo(shape);
            ((RoundRect)shape).arc=arc;
        }
        
        @Override
        public void saveStateFrom(ResizableShape shape) {
            super.saveStateFrom(shape);
            this.arc=((RoundRect)shape).arc;
        }
        
        @Override
        public boolean equals(Object obj){
            if(this==obj){
              return true;  
            }
            if(!(obj instanceof Memento)){
              return false;  
            }
            Memento other=(Memento)obj;
            return super.equals(obj)&&
                   (this.arc==other.arc);
        }
        
        @Override
        public int hashCode(){
           int hash=1; 
           hash=super.hashCode();
           hash+=this.arc;
           return hash;  
        }

        public boolean isSameState(Symbol unit) {
            boolean flag= super.isSameState(unit);
            RoundRect other=(RoundRect)unit.getShape(this.getUUID());
            return other.arc==this.arc&&flag;
        }        
    }
}
