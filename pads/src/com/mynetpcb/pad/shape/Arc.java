package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.shape.Shape.Fill;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import com.mynetpcb.d2.shapes.Utils;

import com.mynetpcb.pad.shape.Circle.Memento;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class Arc  extends Shape implements ArcGerberable, Resizeable,Externalizable {
    
    private com.mynetpcb.d2.shapes.Arc arc;
    private double rotate;
    private Point resizingPoint;
    
    public Arc(double x,double y,double r,double startAngle,double endAngle,int thickness,int layermaskid)   {
        super(thickness,layermaskid);
        this.displayName="Arc";
        this.arc=new com.mynetpcb.d2.shapes.Arc(new Point(x,y),r,startAngle,endAngle); 
        this.selectionRectWidth=3000;
        this.rotate=0;
    }
    @Override
    public Arc clone() throws CloneNotSupportedException {        
        Arc copy= (Arc)super.clone();
        copy.arc=this.arc.clone();
        return copy;
    }
    @Override
    public void mirror(Line line) {
      this.arc.mirror(line);
    }
    public void setRadius(double r){
            this.arc.r=r;   
    }
    public void setExtendAngle(double extendAngle){
        this.arc.endAngle=Utilities.roundDouble(extendAngle);
    }
    public void setStartAngle(double startAngle){        
        this.arc.startAngle=Utilities.roundDouble(startAngle);
    }
    
    public double getRadius(){
       return arc.r;
    }
    public double getStartAngle(){
        return arc.startAngle;
    }
    public Point getCenter(){
        return arc.pc;
    }
    public double getExtendAngle(){
        return arc.getSweep();
    }


    @Override
    public Box getBoundingShape() {
        
        return this.arc.box();
    }
    @Override
    public void move(double xoffset,double yoffset) {
        this.arc.move(xoffset,yoffset);
    } 
    @Override
    public Point getStartPoint() {        
        return this.arc.getStart();
    }
    
    public Point getMiddlePoint() {
        return this.arc.getMiddle();        
    }
    
    @Override
    public Point getEndPoint() {
        return this.arc.getEnd();        
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

    public Point isControlRectClicked(int x,int y) {
          Point a=new Point(x,y);
          if(Utils.LT( a.distanceTo(this.arc.getStart()),selectionRectWidth/2)){
              return this.arc.getStart();
          }                     
          if(Utils.LT(a.distanceTo(this.arc.getEnd()),selectionRectWidth/2)){
              return this.arc.getEnd();
          }
          if(Utils.LT(a.distanceTo(this.arc.getMiddle()),selectionRectWidth/2)){
              return this.arc.getMiddle();      
          }
          return null;
    }
    public boolean isStartAnglePointClicked(int x,int y){  
        Point a=new Point(x,y);
        if(Utils.LT(a.distanceTo(this.arc.getStart()),selectionRectWidth/2)){
            return true;
        }else{
            return false;
        }
    }
    public boolean isMidPointClicked(int x,int y){
        Point a=new Point(x,y);
        if(Utils.LT(a.distanceTo(this.arc.getMiddle()),selectionRectWidth/2)){
            return true;
        }else{
            return false;
        }
    }
    public boolean isExtendAnglePointClicked(int x,int y){
        Point a=new Point(x,y);
        if(Utils.LT(a.distanceTo(this.arc.getEnd()),selectionRectWidth/2)){
            return true;
        }else{
            return false;
        }
    }    
    
    @Override
    public boolean isClicked(int x,int y) {
            if (this.arc.contains(new Point(x, y)))
                    return true;
            else
                    return false;
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
      this.arc.rotate(angle,origin); 
    }
    
    @Override
    public void resize(int xoffset, int yoffset, Point point) {
        Point pt=this.calculateResizingMidPoint(xoffset,yoffset);    
        double r=this.arc.pc.distanceTo(pt);
        this.arc.r=r;
    }
    private Point calculateResizingMidPoint(int x, int y){
            Line line=new Line(this.arc.getCenter(),this.arc.getMiddle());
            return line.projectionPoint(new Point(x,y)); 
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
    public void fromXML(Node node)  {

    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        Box rect = this.arc.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        com.mynetpcb.d2.shapes.Arc  a=this.arc.clone();
        a.scale(scale.getScaleX());
        a.move(-viewportWindow.getX(),- viewportWindow.getY());
        if (fill == Fill.EMPTY) { //framed
            double wireWidth = thickness * scale.getScaleX();
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
            //transparent rect
            a.paint(g2, false);
        } else { //filled
            a.paint(g2,true);
        }
        
        if(this.isSelected()){            
            Utilities.drawCrosshair(g2, viewportWindow, scale,resizingPoint,this.selectionRectWidth,this.arc.getStart(),this.arc.getEnd(),this.arc.getMiddle());
        } 
    }

    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }
    
    public static class Memento extends AbstractMemento<Footprint,Arc>{
        private double startAngle;        
        private double endAngle;
        private double x;
        private double r;
        private double y;
        
        
        public Memento(MementoType mementoType) {
           super(mementoType);            
        }
        @Override
        public void saveStateFrom(Arc shape) {
            super.saveStateFrom(shape);         
            this.startAngle=(shape).arc.startAngle;
            this.endAngle=(shape).arc.endAngle;

            this.x=shape.arc.pc.x;
            this.y=shape.arc.pc.y;
            this.r=shape.arc.r;
            
        }
        @Override
        public void loadStateTo(Arc shape) {
           super.loadStateTo(shape);
           shape.arc.r=r;
           shape.arc.pc.set(x, y);
           shape.arc.startAngle=startAngle;
           shape.arc.endAngle=endAngle;
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
            Utils.EQ(this.y, other.y)&&Utils.EQ(this.r,other.r)&&Utils.EQ(this.startAngle, other.startAngle)&&Utils.EQ(this.endAngle,other.endAngle);  
        }
        
        @Override
        public int hashCode(){            
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.x)+
                    Double.hashCode(this.y)+Double.hashCode(this.r)+Double.hashCode(this.startAngle)+Double.hashCode(this.endAngle);
            return hash;
        }
        
        @Override
        public boolean isSameState(Footprint unit) {
                        
            boolean flag = super.isSameState(unit);
            Arc other=(Arc)unit.getShape(getUUID());  
            return flag&&Utils.EQ(this.x,other.arc.pc.x)&&Utils.EQ(this.y,other.arc.pc.y)&&Utils.EQ(this.r,other.arc.r)&&Utils.EQ(this.startAngle,other.arc.startAngle)&&Utils.EQ(this.endAngle,other.arc.endAngle);                 
        }
    }    
}
