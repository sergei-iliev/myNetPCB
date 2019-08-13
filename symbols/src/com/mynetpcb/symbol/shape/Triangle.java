package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
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
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import java.util.StringTokenizer;

import org.w3c.dom.Node;


public class Triangle extends ResizableShape implements Externalizable{
    public static final int DIRECTION_WEST = 0x01;

    public static final int DIRECTION_NORTH = 0x02;
        
    public static final int DIRECTION_EAST = 0x04;

    public static final int DIRECTION_SOUTH = 0x08;

    private int orientation;
    
    public Triangle(int orientation,int x,int y,int width,int height) {
        super(x,y,width,height, 1,0);
        this.orientation=orientation;    
    }
    
    public Triangle() {
       this(1,0,0,0,0);
    }
    
    public Triangle clone() throws CloneNotSupportedException {
       return (Triangle)super.clone(); 
    }
    @Override
    public GeneralPath calculateShape(){
        GeneralPath triangle=new GeneralPath(GeneralPath.WIND_EVEN_ODD,3);  
        switch(orientation){
         case DIRECTION_WEST:
            triangle.moveTo(getX(), getY()+getHeight()/2);
            triangle.lineTo(getX()+getWidth(), getY());
            triangle.lineTo(getX()+getWidth(), getY()+getHeight());
            break;
         case DIRECTION_NORTH:
            triangle.moveTo(getX()+getWidth()/2, getY());
            triangle.lineTo(getX()+getWidth(), getY()+getHeight());
            triangle.lineTo(getX(), getY()+getHeight());            
            break;              
         case DIRECTION_EAST:
            triangle.moveTo(getX()+getWidth(),getY()+getHeight()/2);
            triangle.lineTo(getX(),getY()+getHeight());
            triangle.lineTo(getX(),getY());            
            break;
         case DIRECTION_SOUTH:
            triangle.moveTo(getX()+getWidth()/2,getY()+getHeight());
            triangle.lineTo(getX(),getY());
            triangle.lineTo(getX()+getWidth(),getY());
            break; 
        }
        triangle.closePath();
        return triangle;
    }
    
    @Override
    public boolean isClicked(int x, int y) {
        GeneralPath shape = calculateShape();
        return shape.contains(x, y);
    }  
    public void Mirror(Point A,Point B) {
        super.Mirror(A,B);
          
        boolean isRightLeft;
        //***is this right-left mirroring
           if(A.x==B.x){
             //***which place in regard to x origine
             isRightLeft=true;  
           }else{    //***top-botom mirroring
             //***which place in regard to y origine    
             isRightLeft=false;  
           }           
        //***Tweak orientation
        switch(orientation){
        case DIRECTION_WEST:
        case DIRECTION_EAST:
          if(isRightLeft){
              orientation<<=1; if(orientation==0x10) orientation=0x01;     
              orientation<<=1; if(orientation==0x10) orientation=0x01;                   
          }                     
          break;
        case DIRECTION_NORTH:
        case DIRECTION_SOUTH:
          if(!isRightLeft){ 
           orientation<<=1; if(orientation==0x10) orientation=0x01;     
           orientation<<=1; if(orientation==0x10) orientation=0x01;     
          }
          break;               
        }        
    }


    public void Rotate(AffineTransform rotation) {
          super.Rotate(rotation);
          if(rotation.getShearY()>0){        
              orientation<<=1; if(orientation==0x10) orientation=0x01;    
          }else{
              orientation>>=1; if(orientation==0x00) orientation=0x08;    
          }                  
    }

    @Override
    public void Paint(Graphics2D g2,ViewportWindow viewportWindow, AffineTransform scale,int layermask) {        
        Rectangle2D scaledRect = Utilities.getScaleRect(this.getBoundingShape().getBounds(),scale);         
        if(!scaledRect.intersects(viewportWindow)){  
            return;   
        }
        
        GeneralPath triangle=calculateShape();
        triangle.transform(scale);
        
        GeneralPath scaledTriangle=new GeneralPath(GeneralPath.WIND_EVEN_ODD,3); 
        PathIterator pi = triangle.getPathIterator(new AffineTransform()); 
         while (pi.isDone() == false) {
          float[] coords = new float[6];
          switch (pi.currentSegment(coords)) {
              case PathIterator.SEG_MOVETO:
                  scaledTriangle.moveTo(coords[0]-viewportWindow.x,coords[1]-viewportWindow.y);     
                  break;
              case PathIterator.SEG_LINETO:
                  scaledTriangle.lineTo(coords[0]-viewportWindow.x,coords[1]-viewportWindow.y); 
                  break;
          }
         pi.next();
        } 
        scaledTriangle.closePath();
        
        //***set thickness      
        g2.setStroke(new BasicStroke((float)(thickness*scale.getScaleX())));  
        g2.setColor(isSelected()?Color.GRAY:fillColor); 
        if(fill == Fill.EMPTY)   //***empty
          g2.draw(scaledTriangle);
        if(this.getFill() == Fill.FILLED)  //***filled
          g2.fill(scaledTriangle);
        if(this.getFill() == Fill.GRADIENT){   //***gradual
            GradientPaint gp = 
                new GradientPaint(scaledTriangle.getBounds().x, scaledTriangle.getBounds().y, 
                                  Color.white, scaledTriangle.getBounds().x, 
                                  (scaledTriangle.getBounds().y+scaledTriangle.getBounds().height), Color.gray, true);
            g2.setPaint(gp);
            g2.fill(scaledTriangle);
            g2.setColor(Color.black);
            g2.draw(scaledTriangle);
        }        
        
        if(this.isSelected()){
              this.drawControlShape(g2,viewportWindow,scale);
        } 
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        GeneralPath triangle=calculateShape();
        //***set thickness      
        g2.setStroke(new BasicStroke(thickness));  
        g2.setColor(Color.BLACK); 
        if(fill == Fill.EMPTY)   //***empty
          g2.draw(triangle);
        if(this.getFill() == Fill.FILLED)  //***filled
          g2.fill(triangle);
        if(this.getFill() == Fill.GRADIENT){   //***gradual
            GradientPaint gp = 
                new GradientPaint(triangle.getBounds().x, triangle.getBounds().y, 
                                  Color.white, triangle.getBounds().x, 
                                  (triangle.getBounds().y+triangle.getBounds().height), Color.gray, true);
            g2.setPaint(gp);
            g2.fill(triangle);
            g2.setColor(Color.black);
            g2.draw(triangle);
        }     
    }
    
    public String getDisplayName() {
        return "Triangle";
    }

    public String toXML() {    
        return "<triangle>"+this.orientation+","+upperLeft.x+","+upperLeft.y+","+getWidth()+","+getHeight()+","+this.getThickness()+","+this.getFill().index+"</triangle>\r\n";
    }

    public void fromXML(Node node) {
           StringTokenizer st=new StringTokenizer(node.getTextContent(),",");
           orientation=Integer.parseInt(st.nextToken());
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
        memento.loadStateTo(this); 
    }
    
    public static class Memento extends ResizableShape.Memento {
        private int orientation;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(ResizableShape shape) {
            super.loadStateTo(shape);
            ((Triangle)shape).orientation=this.orientation;
        }
        
        @Override
        public void saveStateFrom(ResizableShape shape) {
            super.saveStateFrom(shape);
            this.orientation=((Triangle)shape).orientation;
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
                   (this.orientation==other.orientation);
        }
        
        @Override
        public int hashCode(){
           int hash=1; 
           hash=super.hashCode();
           hash+=this.orientation;
           return hash;  
        }
        
        public boolean isSameState(Symbol unit) {
            boolean flag= super.isSameState(unit);
            Triangle other=(Triangle)unit.getShape(this.getUUID());
            return other.orientation==this.orientation&&flag;
        }
    }

}

