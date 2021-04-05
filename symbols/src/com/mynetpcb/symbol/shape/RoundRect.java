package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.ResizableShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

import java.util.StringTokenizer;

import org.w3c.dom.Node;


public class RoundRect extends ResizableShape implements Externalizable{
    
        
    private RoundRectangle2D roundRect;
    int rounding;
    private Point resizingPoint;
    
    public RoundRect() {
            super();
            this.setDisplayName("Rect");            
            this.selectionRectWidth=2;
            this.fillColor=Color.BLACK;
            this.roundRect=new RoundRectangle2D.Double();
            this.rounding=0;
    }
    
    @Override
    public RoundRect clone() throws CloneNotSupportedException{
        RoundRect copy= (RoundRect)super.clone();
        copy.roundRect=new RoundRectangle2D.Double();        
        copy.resizingPoint=null;                        
        return copy;
    }
    @Override
    public long getClickableOrder() {        
        return (long)getBoundingShape().area();
    }
    @Override
    public void setSelected(boolean selection) {
        super.setSelected(selection);
        if (!selection) {
            resizingPoint = null;
        }
    }
    
    @Override
    public Point getResizingPoint() {
        return resizingPoint;
    }

    @Override
    public void setResizingPoint(Point point) {
        this.resizingPoint = point;
    }
    
    public int getRounding(){
      return rounding;    
    }
    
    public void setRounding(int rounding){
      this.rounding=rounding;    
    }
    
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Box rect =getBoundingShape();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
               
        roundRect.setRoundRect(getX() ,getY(),getWidth(),getHeight(),rounding,rounding);
        
        
        g2.setStroke(new BasicStroke((this.thickness),1,1));  
        g2.setColor(isSelected()?Color.GRAY:fillColor); 

        AffineTransform old= g2.getTransform();
        AffineTransform tr= new AffineTransform();
        tr.translate(-viewportWindow.getX(),-viewportWindow.getY());  
        tr.scale(scale.getScaleX(),scale.getScaleY());
        g2.setTransform(tr);                
        if(fill == Fill.EMPTY)   //***empty
          g2.draw(roundRect);
        if(this.getFill() == Fill.FILLED)  //***filled
          g2.fill(roundRect);
        if(this.getFill() == Fill.GRADIENT){   //***gradual
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            GradientPaint gp = 
                new GradientPaint(roundRect.getBounds().x, roundRect.getBounds().y, 
                                  Color.white, roundRect.getBounds().x, 
                                  (roundRect.getBounds().y+roundRect.getBounds().height), isSelected()?Color.GRAY:fillColor, true);
            g2.setPaint(gp);
            g2.fill(roundRect);
            g2.setComposite(originalComposite);
            g2.setColor(isSelected()?Color.GRAY:fillColor);
            g2.draw(roundRect);
        }      
        g2.setTransform(old);

        if(this.isSelected()){
            this.drawControlShape(g2,viewportWindow,scale);
            Point pt=null;
            if(resizingPoint!=null){
               pt=resizingPoint.clone();
               pt.scale(scale.getScaleX());
               pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }           
            Point p=new Point(roundRect.getMinX(),roundRect.getMinY());     
            p.scale(scale.getScaleX());
            p.move(-viewportWindow.getX(),- viewportWindow.getY());              
            Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),p); 

            p.set(roundRect.getMinX()+roundRect.getWidth(),roundRect.getMinY());     
            p.scale(scale.getScaleX());
            p.move(-viewportWindow.getX(),- viewportWindow.getY());              
            Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),p); 

            p.set(roundRect.getMinX(),roundRect.getMinY()+roundRect.getHeight());     
            p.scale(scale.getScaleX());
            p.move(-viewportWindow.getX(),- viewportWindow.getY());              
            Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),p);

            p.set(roundRect.getMinX()+roundRect.getWidth(),roundRect.getMinY()+roundRect.getHeight());     
            p.scale(scale.getScaleX());
            p.move(-viewportWindow.getX(),- viewportWindow.getY());              
            Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),p);            
        } 

    }
    
    @Override
    public void print(Graphics2D g2,PrintContext printContext,int layermask) {              
        roundRect.setRoundRect(getX() ,getY(),getWidth(),getHeight(),rounding,rounding);
        
        
        g2.setStroke(new BasicStroke((this.thickness),1,1));  
        g2.setColor(isSelected()?Color.GRAY:fillColor); 

                 
        if(fill == Fill.EMPTY)   //***empty
          g2.draw(roundRect);
        if(this.getFill() == Fill.FILLED)  //***filled
          g2.fill(roundRect);
        if(this.getFill() == Fill.GRADIENT){   //***gradual
            GradientPaint gp = 
                new GradientPaint(roundRect.getBounds().x, roundRect.getBounds().y, 
                                  Color.white, roundRect.getBounds().x, 
                                  (roundRect.getBounds().y+roundRect.getBounds().height), isSelected()?Color.GRAY:fillColor, true);
            g2.setPaint(gp);
            g2.fill(roundRect);
            g2.setColor(isSelected()?Color.GRAY:fillColor);
            g2.draw(roundRect);
        }      
        
    }
    @Override
    public String toXML() {
        return "<rectangle>"+Utilities.roundDouble(upperLeft.x,1)+","+Utilities.roundDouble(upperLeft.y,1)+","+getWidth()+","+getHeight()+","+this.getThickness()+","+this.fill.index +","+this.rounding+"</rectangle>\r\n";
    }
    @Override
    public void fromXML(Node node){        
        StringTokenizer st=new StringTokenizer(node.getTextContent(),",");   
        init(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));
        setThickness(Byte.parseByte(st.nextToken()));
        setFill(Fill.byIndex(Byte.parseByte(st.nextToken())));         
        rounding=Integer.parseInt(st.nextToken());
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Symbol,RoundRect>{
            
            private int rounding;
            private double x1,x2,x3,x4;
            private double y1,y2,y3,y4;
            
            public Memento(MementoType mementoType) {
                super(mementoType);
            }

            @Override
            public void loadStateTo(RoundRect shape) {
                super.loadStateTo(shape);
                shape.upperLeft.set(x1,y1);
                shape.upperRight.set(x2,y2);
                shape.bottomLeft.set(x3,y3);
                shape.bottomRight.set(x4,y4);                                               
                shape.rounding=rounding;
            }

            @Override
            public void saveStateFrom(RoundRect shape) {
                super.saveStateFrom(shape);
                x1=shape.upperLeft.x;            
                y1=shape.upperLeft.y;
                x2=shape.upperRight.x;            
                y2=shape.upperRight.y;
                x3=shape.bottomLeft.x;            
                y3=shape.bottomLeft.y;
                x4=shape.bottomRight.x;            
                y4=shape.bottomRight.y; 
                this.rounding=shape.rounding;                
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof Memento)) {
                    return false;
                }
                Memento other = (Memento) obj;
                return super.equals(obj)&&(this.rounding==other.rounding)&&
                Utils.EQ(this.x1, other.x1)&&Utils.EQ(this.y1,other.y1)&&    
                Utils.EQ(this.x2, other.x2)&&Utils.EQ(this.y2,other.y2)&&    
                Utils.EQ(this.x3, other.x3)&&Utils.EQ(this.y3,other.y3)&&    
                Utils.EQ(this.x4, other.x4)&&Utils.EQ(this.y4,other.y4);   
            }

            @Override
            public int hashCode() {
                int hash = 1;
                hash = super.hashCode();
                hash += this.rounding+
                        Double.hashCode(this.x1)+Double.hashCode(this.y1)+
                        Double.hashCode(this.x2)+Double.hashCode(this.y2)+
                        Double.hashCode(this.x3)+Double.hashCode(this.y3)+
                        Double.hashCode(this.x4)+Double.hashCode(this.y4);
                return hash;
            }
            @Override
            public boolean isSameState(Unit unit) {
                RoundRect other = (RoundRect) unit.getShape(this.getUUID());
                return (other.getState(getMementoType()).equals(this));
            }

        } 
}
