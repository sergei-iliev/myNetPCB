package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Rectangular;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
/*
 * Circle shape represented by center,radios and thicknes
 */
public class Circle extends Shape implements ArcGerberable,Resizeable,Externalizable{    
    
    public Circle(int x,int y,int r,int thickness,int layermask) {
        super(x, y, r, r, thickness,layermask);        
        this.selectionRectWidth=3000;
    }
    public Circle(){
      this(0,0,0,0,Layer.SILKSCREEN_LAYER_FRONT);  
    }
    
    @Override
    public Circle clone() throws CloneNotSupportedException{
        Circle copy= (Circle)super.clone();        
        return copy;
    }
    
    @Override
    public String getDisplayName(){
        return "Circle";
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
          return super.alignToGrid(isRequired);
        }else{
            return null;
        }
    }
    
    @Override
    public void alignResizingPointToGrid(Point point) {          
        width=getOwningUnit().getGrid().positionOnGrid(width);                
    }
    
    @Override
    public String toXML() {
    //old style ellipse notation - top left point and diameter
        return "<ellipse copper=\""+getCopper().getName()+"\" x=\""+(x-width)+"\" y=\""+(y-width)+"\" width=\""+(2*width)+"\" height=\""+(2*width)+"\" thickness=\""+this.getThickness()+"\" fill=\""+this.getFill().ordinal()+"\"/>\r\n";
    }

    @Override
    public void fromXML(Node node){
        Element  element= (Element)node;
        if(element.hasAttribute("copper")){
          this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));    
        }         
        int xx=(Integer.parseInt(element.getAttribute("x")));
        int yy=(Integer.parseInt(element.getAttribute("y")));        
        int diameter=(Integer.parseInt(element.getAttribute("width")));
        //center x
        setX(xx+((int)(diameter/2)));
        //center y
        setY(yy+((int)(diameter/2)));
        //radius
        setWidth((diameter/2));
        setHeight((diameter/2));        
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
        this.setFill(Fill.values()[(element.getAttribute("fill")==""?0:Integer.parseInt(element.getAttribute("fill")))]);
    }
    
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D temporal=(Ellipse2D)provider.getShape(); 
        
        Rectangle2D rect = getBoundingShape().getBounds(); 
        temporal.setFrame(rect.getX() ,rect.getY(),rect.getWidth(),rect.getWidth());
        
        if(thickness!=-1){    //framed   
          double wireWidth=thickness;       
          g2.setStroke(new BasicStroke((float)wireWidth,1,1));    
          g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:this.copper.getColor());               
          g2.draw(temporal);
        }else{               //filled  
          g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:this.copper.getColor());          
          g2.fill(temporal);  
        }  
        provider.reset();
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        //is this my layer mask
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);         
        if(!scaledRect.intersects(viewportWindow)){  
            return;   
        }
        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D temporal=(Ellipse2D)provider.getShape();  
        temporal.setFrame(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getWidth());

        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
        Composite originalComposite = g2.getComposite();                     
        g2.setComposite(composite ); 
        g2.setColor(isSelected()?Color.GRAY:copper.getColor()); 
        if(fill==Fill.EMPTY){    //framed   
          double wireWidth=thickness*scale.getScaleX();       
          g2.setStroke(new BasicStroke((float)wireWidth));           
          g2.draw(temporal);
        }else{               //filled   
          g2.fill(temporal);  
        }
        g2.setComposite(originalComposite);
        
        provider.reset();
        
        if(this.isSelected()){
              this.drawControlShape(g2,viewportWindow,scale);
        } 

    }
    

    public Point isControlRectClicked(int xx, int yy) {
        FlyweightProvider rectFlyweightProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D)rectFlyweightProvider.getShape();

        
        try{
            rect.setRect((x-width) - selectionRectWidth / 2, (y-width) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(xx,yy)) {
                return new Point((x-width),(y-width));
            }
            rect.setRect((x+width) - selectionRectWidth / 2, (y-width) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(xx,yy)){
                return new Point((x+width),(y-width));
            }
            rect.setRect((x-width) - selectionRectWidth / 2, (y+width) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(xx,yy)){
                return new Point((x-width),(y+width));
            }

            rect.setRect((x+width) - selectionRectWidth / 2, (y+width) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(xx,yy)){
                return new Point((x+width),(y+width));
            }

        }finally{
            rectFlyweightProvider.reset();
        }
        return null;
    }
    
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line = (Line2D) provider.getShape();

        g2.setStroke(new BasicStroke(1));

        g2.setColor(Color.BLUE);
        
        //top
            line.setLine((getX()-getWidth()) - selectionRectWidth, getY()-getWidth(), (getX()-getWidth()) + selectionRectWidth, getY()-getWidth());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()-getWidth()), getY()-getWidth() - selectionRectWidth, (getX()-getWidth()), getY()-getWidth() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);
        
            line.setLine((getX()+getWidth()) - selectionRectWidth, getY()-getWidth(), (getX()+getWidth()) + selectionRectWidth, getY()-getWidth());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()+getWidth()), getY()-getWidth() - selectionRectWidth, (getX()+getWidth()), getY()-getWidth() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);
        //bottom
            line.setLine((getX()-getWidth()) - selectionRectWidth, getY()+getWidth(), (getX()-getWidth()) + selectionRectWidth, getY()+getWidth());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()-getWidth()), getY()+getWidth() - selectionRectWidth, (getX()-getWidth()), getY()+getWidth() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);
        
            line.setLine((getX()+getWidth()) - selectionRectWidth, getY()+getWidth(), (getX()+getWidth()) + selectionRectWidth, getY()+getWidth());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()+getWidth()), getY()+getWidth() - selectionRectWidth, (getX()+getWidth()), getY()+getWidth() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);
      
//center
            line.setLine((getX()) - selectionRectWidth, getY(), (getX()) + selectionRectWidth, getY());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()), getY() - selectionRectWidth, getX(), getY() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);

        provider.reset();
    }
    
    @Override
    public Rectangle calculateShape() {    
      return new Rectangle(getX()-getWidth(),getY()-getWidth(),2*getWidth(),2*getWidth());
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

    @Override
    public Point getResizingPoint() {
        return null;
    }

    @Override
    public void setResizingPoint(Point point) {
        //this.resizingPoint = point;
    }

    @Override
    public void Resize(int xoffset, int yoffset, Point point) {    
        Utilities.QUADRANT quadrant= Utilities.getQuadrantLocation(point,x,y);
        switch(quadrant){
        case FIRST:case FORTH: 
            //uright
             if(xoffset<0){
               //grows             
                width+=Math.abs(xoffset);
             }else{
               //shrinks
                width-=Math.abs(xoffset);
             }             
            break;
        case SECOND:case THIRD:
            //uleft
             if(xoffset<0){
               //shrinks             
                width-=Math.abs(xoffset);
             }else{
               //grows
                width+=Math.abs(xoffset);
             }             
            break;        
        }

    }

    @Override
    public Point2D getStartPoint() {        
        return new Point(x-width,y);
    }

    @Override
    public Point2D getEndPoint() {
        return getStartPoint();
    }

    @Override
    public Point getCenterPoint() {        
        return new Point(x,y);
    }

    @Override
    public int getI() {
        return width;
    }

    @Override
    public int getJ() {
        return 0;
    }

    @Override
    public boolean isSingleQuadrant() {
        return false;
    }

    @Override
    public boolean isClockwise() {        
        return true;
    }

    public static class Memento extends AbstractMemento<Unit,Circle> {     
        private int x;
        private int radios;
        private int y;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void saveStateFrom(Circle shape) {
            super.saveStateFrom(shape);
            this.x=shape.getX();
            this.y=shape.getY();
            this.radios=shape.getWidth();
        }
        
        public void loadStateTo(Circle shape) {
            super.loadStateTo(shape);
            shape.setX(x);
            shape.setY(y);
            shape.setWidth(radios);
        }
        
        @Override
        public boolean isSameState(Unit unit) {
            Circle other=(Circle)unit.getShape(getUUID());
            return (other.getThickness()==this.thickness&&other.getFill().ordinal()==this.fill&&other.copper.ordinal()==this.layerindex&&
                    (other.getX()==this.x)&&(other.getY()==this.y)&&
                    (other.getWidth()==this.radios)
                   );
            
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
            return (other.getMementoType().equals(this.getMementoType())&&
                    other.getUUID().equals(this.getUUID())&&
                    other.thickness==this.thickness&&
                    other.fill==this.fill&&
                    other.layerindex==this.layerindex&&
                    x==other.x&&
                    y==other.y&&radios==other.radios                                  
                   );
              
        }
        
        @Override
        public int hashCode(){
           int hash=1; 
           hash=hash*31+getUUID().hashCode()+this.getMementoType().hashCode()+this.fill+this.thickness+this.layerindex;
           hash+=x;
           hash+=y;
           hash+=radios;
           return hash;  
        }
    }
}
