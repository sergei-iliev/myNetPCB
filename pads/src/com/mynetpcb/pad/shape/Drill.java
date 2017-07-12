package com.mynetpcb.pad.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Drill extends Shape implements Externalizable{

    public enum Type{
        CIRCULAR,OVAL,NONE
    }
    
    private  Ellipse2D ellipse;
    
    private Type type;
    
    public Drill(int width,int height) {
        super(0,0,width,height,-1,0);
        this.ellipse=new Ellipse2D.Double();
        this.enableCache(false);
        this.type = Type.CIRCULAR;
        setFillColor(Color.BLACK);
    }

    
    @Override
    public void Move(int xoffset, int yoffset) {
      setX(getX()+xoffset);
      setY(getY()+yoffset);
    }
    
    public Drill clone()throws CloneNotSupportedException{
        Drill copy= (Drill)super.clone();
        copy.ellipse=new Ellipse2D.Double(ellipse.getX(),ellipse.getY(),ellipse.getWidth(),ellipse.getWidth());
        return copy;
    }
    
    @Override
    public String toXML() {
        return "<drill type=\""+this.type+"\" x=\""+getX()+"\" y=\""+getY()+"\" width=\""+getWidth()+"\" height=\""+getHeight()+"\" />";
    }

    @Override
    public void fromXML(Node node) {
        Element  element= (Element)node;
        this.type=(com.mynetpcb.pad.shape.Drill.Type.valueOf(element.getAttribute("type")));
        this.setX(Integer.parseInt(element.getAttribute("x")));
        this.setY(Integer.parseInt(element.getAttribute("y")));
        this.setWidth(Integer.parseInt(element.getAttribute("width")));
        this.setHeight(Integer.parseInt(element.getAttribute("height")));
    }
    
    @Override
    public void Clear() {
    }

    @Override
    public void Mirror(Point A,Point B) {
        Point source=new Point(getX(),getY());
        Utilities.mirrorPoint(A,B, source); 
        setX(source.x);
        setY(source.y);
    }

    @Override
    public void Translate(AffineTransform translate) {
        Point dst = new Point();
        translate.transform(new Point(getX(),getY()), dst);
        setX(dst.x);
        setY(dst.y);
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        Point dst = new Point();
        rotation.transform(new Point(getX(),getY()), dst);
        setX(dst.x);
        setY(dst.y);
        int w=getWidth();
        setWidth(getHeight());
        setHeight(w);      
    }

    @Override
    public void setLocation(int x, int y) {
     setX(x);
     setY(y);
    }

    @Override
    public boolean isClicked(int x, int y) {
        return false;
    }
    @Override
    public boolean isInRect(Rectangle r){
        return false;
    }
    /**
     *Circular shapes only!!!!!!!!!!!!!!!!!!!
     * @return
     */
    @Override
    public Rectangle calculateShape() {
        return new Rectangle(getX()-getWidth()/2,getY()-getWidth()/2,getWidth(),getWidth());
    }
    
    @Override
    public String getDisplayName(){
        return "Drill";
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
                    Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale); 
                    ellipse.setFrame(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getHeight());
                   
                    //***Always thick
//                    if(thickness!=-1){    //framed   
//                      double wireWidth=thickness*scale.getScaleX();       
//                      g2.setStroke(new BasicStroke((float)wireWidth,1,1));    
//                      g2.setPaint(Color.BLACK);        
//                      g2.draw(ellipse);
//                    }else
                    {               //filled  
                      g2.setColor(Color.BLACK);  
                      g2.fill(ellipse);  
                    }    
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
                    Rectangle2D scaledRect = getBoundingShape().getBounds();
                    ellipse.setFrame(scaledRect.getX(),scaledRect.getY(),scaledRect.getWidth(),scaledRect.getHeight());
                                            
                    g2.setColor(Color.WHITE);  
                    g2.fill(ellipse);  
                               
    }
    
//    public static class CircularDrill extends Drill{
//      
//        public CircularDrill(Unit owningUnit,int width){
//          super(owningUnit, width,width);  
//        }
//        
//        public  Type getType(){
//            return Type.CIRCULAR;
//        }
//        
//        @Override
//        public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
//            Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingRect(),scale); 
//            ellipse.setFrame(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getHeight());
//           
//            
//            if(thickness!=-1){    //framed   
//              double wireWidth=thickness*scale.getScaleX();       
//              g2.setStroke(new BasicStroke((float)wireWidth,1,1));    
//              g2.setPaint(fillColor);        
//              g2.draw(ellipse);
//            }else{               //filled  
//              g2.setColor(fillColor);  
//              g2.fill(ellipse);  
//            }          
//        }  
//        
//        @Override
//        public void Print(Graphics2D g2) {
//            Rectangle2D scaledRect = getBoundingRect();
//            ellipse.setFrame(scaledRect.getX(),scaledRect.getY(),scaledRect.getWidth(),scaledRect.getHeight());
//            
//            if(thickness!=-1){    //framed   
//              double wireWidth=thickness;       
//              g2.setStroke(new BasicStroke((float)wireWidth,1,1));    
//              g2.setPaint(Color.WHITE);        
//              g2.draw(ellipse);
//            }else{               //filled  
//              g2.setColor(Color.WHITE);  
//              g2.fill(ellipse);  
//            }        
//        }
//    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }
    
     public static class Memento extends AbstractMemento<Footprint,Drill>{
        int x,y,width,height;

        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(Drill shape) {
            if(shape==null)
                return;
            super.loadStateTo(shape);
            shape.setX(x);
            shape.setY(y);
            shape.setWidth(width);    
            shape.setHeight(height);
        }
        
        @Override
        public void saveStateFrom(Drill shape) {
            if(shape==null)
                return;
            super.saveStateFrom(shape);
            x=shape.getX();
            y=shape.getY();
            width=shape.getWidth();
            height=shape.getHeight();
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }

            Memento other = (Memento)obj;
            
            //drills could be null
            if(this.uuid==null&&other.uuid!=null){
               return false; 
            }else if(this.uuid!=null&&other.uuid==null){   
               return false;
            }else if(this.uuid==null&&other.uuid==null){   
               return true; 
            }else            
                return getMementoType()==other.getMementoType()&&
                uuid.equals(other.uuid)
                &&x==other.x
                &&y==other.y
                &&width==other.width
                &&height==other.height;
        }

        @Override
        public int hashCode() {
            int hash=31;
            hash += getMementoType().hashCode()+ getUUID().hashCode();
            hash +=x+y+width+height;
            return hash;
        }
        @Override
        public boolean isSameState(Footprint unit) {
             throw new IllegalStateException("Drill can not exist by iself");
        }
    
        
    }
    
}
