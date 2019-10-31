package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.shape.Shape.Fill;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import com.mynetpcb.d2.shapes.Utils;

import com.mynetpcb.pad.shape.RoundRect.Memento;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class Circle  extends Shape implements ArcGerberable,Resizeable,Externalizable{
    
    private com.mynetpcb.d2.shapes.Circle circle;
    private double rotate;
    private Point resizingPoint;
    
    public Circle(double x,double y,double r,int thickness,int layermaskId) {
		super( thickness,layermaskId);
                this.displayName="Circle";
		this.selectionRectWidth=3000;
		this.resizingPoint=null;
		this.circle=new com.mynetpcb.d2.shapes.Circle(new Point(x,y),r);
		this.rotate=0;
	}
    @Override
    public Circle clone() throws CloneNotSupportedException{
        Circle copy= (Circle)super.clone();        
        copy.circle=this.circle.clone();
        return copy;
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
            Point point=getOwningUnit().getGrid().positionOnGrid(circle.pc.x, circle.pc.y);
            circle.pc.set(point);            
            return null;                      
        }else{
          return null;
        }
    }
    
    public double getRadius(){
       return circle.r;    
    }
    @Override
    public void rotate(double angle, Point origin) {        
        //fix angle
        double alpha=this.rotate+angle;
        if(alpha>=360){
                alpha-=360;
        }
        if(alpha<0){
         alpha+=360; 
        }       
        this.rotate=alpha;
        this.circle.rotate(angle,origin);
    }
    public void setRadius(double r){
        circle.r=r;
    }
    @Override
    public Point getCenter() {
        return this.circle.pc;
    }
    @Override
    public Box getBoundingShape() {
        return this.circle.box();         
    }
    @Override
    public long getOrderWeight(){
        return (long)this.circle.area(); 
    }
    
    @Override
    public boolean isClicked(int x, int y) {        
        if (this.circle.contains(new Point(x, y)))
            return true;
         else
            return false;                
    }
    @Override
    public void mirror(Line line){
       this.circle.mirror(line);    
    }
    @Override
    public void move(double xoffset,double yoffset) {
            this.circle.move(xoffset,yoffset);
    } 
    @Override
    public Point getStartPoint() {
        // TODO Implement this method
        return null;
    }

    @Override
    public Point getEndPoint() {
        // TODO Implement this method
        return null;
    }

    @Override
    public int getI() {
        // TODO Implement this method
        return 0;
    }

    @Override
    public int getJ() {
        // TODO Implement this method
        return 0;
    }

    @Override
    public boolean isSingleQuadrant() {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean isClockwise() {
        // TODO Implement this method
        return false;
    }

    @Override
    public Point isControlRectClicked(int x, int y) {
        Point pt=new Point(x,y);
                       
        for(Point p:this.circle.vertices()){
            if(Utils.LE(pt.distanceTo(p),this.selectionRectWidth/2)){                                  
                return p;
             }
        }
        return null;               
    }

    @Override
    public Point getResizingPoint() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void setResizingPoint(Point point) {
        // TODO Implement this method
    }

    @Override
    public void resize(int xoffset, int yoffset, Point point) {
        
        double radius=this.circle.r;

        if(Utils.EQ(point.x,this.circle.pc.x)){
          if(point.y>this.circle.pc.y){
                  radius+=yoffset;
          }else{
                  radius-=yoffset;  
          }     
        }
        if(Utils.EQ(point.y,this.circle.pc.y)){
            if(point.x>this.circle.pc.x){
                  radius+=xoffset;
            }else{
                  radius-=xoffset;  
            }   
        }
        if(radius>0){ 
          this.circle.r=radius;
        }        

    }

    @Override
    public void alignResizingPointToGrid(Point point) {
        // TODO Implement this method
    }

    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void fromXML(Node node) {
        // TODO Implement this method

    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        //is this my layer mask
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        Box rect = this.circle.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        
        com.mynetpcb.d2.shapes.Circle  c=this.circle.clone();
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        if (fill == Fill.EMPTY) { //framed
            double wireWidth = thickness * scale.getScaleX();
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
            //transparent rect
            c.paint(g2, false);
        } else { //filled
            c.paint(g2,true);
        }
                
        if(this.isSelected()){            
            Utilities.drawCrosshair(g2, viewportWindow, scale,resizingPoint,this.selectionRectWidth,this.circle.vertices());
        } 

    }
    
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Footprint,Circle> {     
        private double x,y;
        private double r;
        
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void saveStateFrom(Circle shape) {
            super.saveStateFrom(shape);
            this.x=shape.circle.pc.x;
            this.y=shape.circle.pc.y;
            this.r=shape.circle.r;
        }
        
        public void loadStateTo(Circle shape) {
            super.loadStateTo(shape);
            shape.circle.pc.set(x,y);
            shape.circle.r=r;
        }
        
        @Override
        public boolean isSameState(Footprint unit) {
            boolean flag = super.isSameState(unit);
            Circle other=(Circle)unit.getShape(getUUID());
            return flag&&Utils.EQ(this.x,other.circle.pc.x)&&Utils.EQ(this.y,other.circle.pc.y)&&Utils.EQ(this.r,other.circle.r);
                        
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
            return super.equals(obj) && Utils.EQ(this.x, other.x)&&
            Utils.EQ(this.y, other.y)&&Utils.EQ(this.r,other.r);   
          
         
              
        }
        
        @Override
        public int hashCode(){
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.x)+
                    Double.hashCode(this.y)+Double.hashCode(this.r);
            return hash;
        }
    }    
}