package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.gerber.Fillable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.shape.Shape.Fill;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Circle  extends Shape implements ArcGerberable,Fillable,Resizeable,Externalizable{
    
    protected com.mynetpcb.d2.shapes.Circle circle;
    protected Point resizingPoint;
    
    public Circle(double x,double y,double r,int thickness,int layermaskId) {
		super( thickness,layermaskId);
        this.displayName="Circle";
		this.resizingPoint=null;
		this.circle=new com.mynetpcb.d2.shapes.Circle(new Point(x,y),r);
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
    public void setRotation(double rotate, Point center) {
        double alpha=rotate-this.rotate;
        this.circle.rotate(alpha,center);                       
        this.rotate=rotate;     
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
    public long getClickableOrder(){
        return (long)this.circle.area(); 
    }
    
    @Override
    public boolean isClicked(double x,double y) {  
    	if(this.fill==Fill.EMPTY) {
        	  return (this.circle.isPointOn(new Point(x,y),this.thickness/2));
        }else {    		
        	  return this.circle.contains(new Point(x, y));	
        }
    	               
    }
    @Override
    public void setSide(Layer.Side side, Line line,double angle) {
        this.setCopper(Layer.Side.change(this.getCopper().getLayerMaskID()));
        this.mirror(line);
        this.rotate=angle;
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
        return new Point(circle.pc.x-circle.r,circle.pc.y);
    }

    @Override
    public Point getEndPoint() {
        return getStartPoint();
    }

    @Override
    public double getI() {        
        return circle.r;
    }

    @Override
    public double getJ() {       
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
    @Override
    public Point isControlRectClicked(double x, double y,ViewportWindow viewportWindow) {
        Point pt=new Point(x,y);
		pt.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		pt.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        for(Point v:this.circle.vertices()){
        	var tmp=v.clone();
        		tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
        		tmp.move(-viewportWindow.getX(),- viewportWindow.getY());
            if(Utils.LE(pt.distanceTo(tmp),selectionRectWidth/2)){
              return v;
            }                        
        };
        return null;
    }
    @Override
    public Point getResizingPoint() {       
        return resizingPoint;
    }

    @Override
    public void setResizingPoint(Point point) {
    	this.resizingPoint=point;
    }

    @Override
    public void resize(double xoffset, double yoffset, Point point) {    	
    	this.resizingPoint=this.circle.resize(xoffset, yoffset,point);     
    }

    @Override
    public void alignResizingPointToGrid(Point point) {
        // TODO Implement this method
    }

    @Override
    public String toXML() {
        return "<circle copper=\""+getCopper().getName()+"\" x=\""+Utilities.roundDouble(this.circle.pc.x)+"\" y=\""+Utilities.roundDouble(this.circle.pc.y)+"\" radius=\""+Utilities.roundDouble(this.circle.r)+"\"  thickness=\""+this.getThickness()+"\" fill=\""+this.getFill().index+"\"/>\r\n";

    }

    @Override
    public void fromXML(Node node) {
        Element  element= (Element)node;
        this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));    
             
        double xx=(Double.parseDouble(element.getAttribute("x")));
        double yy=(Double.parseDouble(element.getAttribute("y")));        

        if(element.getAttribute("width").length()>0){  
            int diameter=(Integer.parseInt(element.getAttribute("width")));
            this.circle.pc.set(xx+((diameter/2)),yy+((diameter/2)));
            this.circle.r=(diameter/2); 
        }else{
            double radius=(Double.parseDouble(element.getAttribute("radius"))); 
            this.circle.pc.set(xx,yy);
            this.circle.r=radius;   
        }
        
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
        
        this.setFill(Fill.byIndex(Integer.parseInt(element.getAttribute("fill"))==0?1:Integer.parseInt(element.getAttribute("fill"))));     

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
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
            Composite originalComposite = g2.getComposite();                     
            g2.setComposite(composite );             
            c.paint(g2,true);
            g2.setComposite(originalComposite);
        }                

    }
    
    @Override
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {        
        Point pt=null;
        if(resizingPoint!=null){
            pt=resizingPoint.clone();
            pt.scale(scale.getScaleX());
            pt.move(-viewportWindow.getX(),- viewportWindow.getY());
        }        
        com.mynetpcb.d2.shapes.Circle c=this.circle.clone();
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());

        for(Point p:c.vertices()){
            Utilities.drawCircle(g2,  pt,p);
        }                    
    }
    
    @Override
    public void print(Graphics2D g2,PrintContext printContext,int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }

        g2.setStroke(new BasicStroke(thickness,1,1));    
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());        
        if(this.fill==Fill.EMPTY){
          this.circle.paint(g2, false);      
        }else{
          this.circle.paint(g2, true);      
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
        public boolean isSameState(Unit unit) {
            Circle other = (Circle) unit.getShape(getUUID());
            return (other.getState(getMementoType()).equals(this));            
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
