package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Rectangular;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.ResizableShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import java.util.StringTokenizer;

import org.w3c.dom.Node;


public class Ellipse  extends ResizableShape implements Rectangular,Externalizable{
    
    private  Ellipse2D ellipse;
                      
    public Ellipse(int x,int y,int width,int height) {
        super(x,y,width,height, 1,0);
        this.ellipse=new Ellipse2D.Double(x,y,width,height);
    }
    public Ellipse(){
      this(0,0,0,0);  
    }
    @Override
    public Ellipse clone() throws CloneNotSupportedException{
        Ellipse copy= (Ellipse)super.clone();
        copy.ellipse=new Ellipse2D.Double(ellipse.getX(),ellipse.getY(),ellipse.getWidth(),ellipse.getHeight());
        return copy;
    }

    @Override
    public void Clear() {
    }


    @Override
    public void setLocation(int x, int y) {
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);         
        if(!scaledRect.intersects(viewportWindow)){  
            return;   
        }
        ellipse.setFrame(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getHeight());

        g2.setStroke(new BasicStroke((float)(thickness*scale.getScaleX()),1,1));  
        g2.setColor(isSelected()?Color.GRAY:fillColor); 
        if(fill == Fill.EMPTY)   //***empty
          g2.draw(ellipse);
        if(this.getFill() == Fill.FILLED)  //***filled
          g2.fill(ellipse);
        if(this.getFill() == Fill.GRADIENT){   //***gradual
            GradientPaint gp = 
                new GradientPaint(ellipse.getBounds().x, ellipse.getBounds().y, 
                                  Color.white, ellipse.getBounds().x, 
                                  (ellipse.getBounds().y+ellipse.getBounds().height), Color.gray, true);
            g2.setPaint(gp);
            g2.fill(ellipse);
            g2.setColor(Color.black);
            g2.draw(ellipse);
        } 
 
        if(this.isSelected()){
              this.drawControlShape(g2,viewportWindow,scale);
        } 
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        Rectangle2D rect = getBoundingShape().getBounds(); 
        ellipse.setFrame(rect.getX() ,rect.getY(),rect.getWidth(),rect.getHeight());
        
        if(thickness!=-1){    //framed   
          double wireWidth=thickness;       
          g2.setStroke(new BasicStroke((float)wireWidth,1,1));    
          g2.setPaint(Color.BLACK);        
          g2.draw(ellipse);
        }else{               //filled  
          g2.setColor(Color.BLACK);  
          g2.fill(ellipse);  
        }     
    }
    
    @Override
    public boolean isClicked(int x, int y) {
        ellipse.setFrame(getX(), getY(), getWidth(), getHeight());
        if(ellipse.contains(x,y))
         return true;
        else
         return false; 
    }


    @Override
    public String getDisplayName(){
        return "Ellipse";
    }

    public String toXML() {
        return "<ellipse>"+upperLeft.x+","+upperLeft.y+","+getWidth()+","+getHeight()+","+this.getThickness()+","+this.getFill().index+"</ellipse>\r\n";               
    }

    public void fromXML(Node node) {
        StringTokenizer st=new StringTokenizer(node.getTextContent(),",");             
        Initialize(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));
        setThickness(Byte.parseByte(st.nextToken()));
        setFill(Fill.byIndex(Byte.parseByte(st.nextToken())));      
    }

    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        ((Memento)memento).loadStateTo(this); 
    }
    public static class Memento extends ResizableShape.Memento {
         public Memento(MementoType mementoType) {
             super(mementoType);
         }
                 
     }
}

