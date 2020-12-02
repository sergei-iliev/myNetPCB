package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Arcellipse;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Arc  extends Shape implements Resizeable, Externalizable{
    private  Arcellipse arc;
    private Point resizingPoint;
    
    public Arc(int thickness) {
        super(thickness,Layer.LAYER_ALL);
        this.setDisplayName("Arc");         
        this.arc=new Arcellipse(0,0,20,10);
        this.selectionRectWidth=2;
        this.fillColor=Color.BLACK;
    }
    @Override
    public Arc clone() throws CloneNotSupportedException {
        Arc copy=(Arc)super.clone();
        copy.resizingPoint=null;
        copy.arc=this.arc.clone();
        return copy;
    }
    @Override
    public long getClickableOrder() {        
       return (long)getBoundingShape().area();
    }
    public Arcellipse getShape(){
        return arc;
    }    
    @Override
    public Box getBoundingShape() {
        return this.arc.box();             
    }
    @Override
    public Point isControlRectClicked(int x, int y) {
        Point pt=new Point(x,y);
        
        for(Point v:this.arc.vertices()){
            if(Utils.LE(pt.distanceTo(v),this.selectionRectWidth)){
              return v;
            }                        
        };
        return null;
    }
    public void setExtendAngle(double extendAngle){
        this.arc.endAngle=extendAngle;
    }
    public void setStartAngle(double startAngle){        
        this.arc.startAngle=startAngle;
    }
    public double getStartAngle(){
        return arc.startAngle;
    }
    public Point getCenter(){
        return arc.pc;
    }
    public double getExtendAngle(){
        return arc.endAngle;
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
        this.arc.pc.rotate(angle,origin);
        double w=this.arc.width;
        this.arc.width=this.arc.height;
        this.arc.height=w;
        this.arc.startAngle+=angle;
        if(this.arc.startAngle>=360){
            this.arc.startAngle-=360;
        }
        if(this.arc.startAngle<0){
            this.arc.startAngle+=360;
        }
    }
    @Override
    public void resize(int xoffset, int yoffset, Point clickedPoint) {        
        this.arc.resize(xoffset, yoffset,clickedPoint);
    }

    @Override
    public void alignResizingPointToGrid(Point point) {        
               
        
    }
    public boolean isStartAnglePointClicked(double x,double y){  
        Point p=this.arc.getStart();
        Box box=Box.fromRect(p.x - this.selectionRectWidth / 2, p.y - this.selectionRectWidth / 2,
                     this.selectionRectWidth, this.selectionRectWidth);
        if (box.contains((int)x,(int)y)) {
            return true;
        }else{                   
            return false;
            }
    }       
    public boolean isExtendAnglePointClicked(double x,double y){
        Point p=this.arc.getEnd();
        Box box=Box.fromRect(p.x - this.selectionRectWidth / 2, p.y - this.selectionRectWidth / 2,
                     this.selectionRectWidth, this.selectionRectWidth);
        if (box.contains((int)x,(int)y)) {
            return true;
        }else{                   
            return false;
            }
    }       
    @Override
    public String toXML() {
        return "<arc  x=\""+Utilities.roundDouble(this.arc.pc.x,1)+"\" y=\""+Utilities.roundDouble(this.arc.pc.y,1)+"\" width=\""+Utilities.roundDouble(this.arc.width,1)+"\" height=\""+Utilities.roundDouble(this.arc.height,1)+ "\"  thickness=\""+this.thickness+"\" start=\""+Utilities.roundDouble(this.arc.startAngle,1)+"\" extend=\""+Utilities.roundDouble(this.arc.endAngle,1)+"\" fill=\""+this.fill.ordinal()+"\"/>\r\n";
    }

    @Override
    public void fromXML(Node node) {
        StringTokenizer st=new StringTokenizer(node.getTextContent(),","); 
        if(st.hasMoreTokens()){    //old schema
            double x=Double.parseDouble(st.nextToken());
            double y=Double.parseDouble(st.nextToken());
            double w=Double.parseDouble(st.nextToken());
            double h=Double.parseDouble(st.nextToken());
        
            this.arc.pc.set(x+w/2,y+h/2);
            this.arc.width=w/2;
            this.arc.height=h/2;
        
            this.arc.endAngle = Double.parseDouble(st.nextToken());       
            this.arc.startAngle = Double.parseDouble(st.nextToken());
        
            this.thickness = Integer.parseInt(st.nextToken());
            setFill(Fill.byIndex(Byte.parseByte(st.nextToken())));  
        }else{
            Element element = (Element) node;
            double x=Double.parseDouble(element.getAttribute("x"));
            double y=Double.parseDouble(element.getAttribute("y"));
            double w=Double.parseDouble(element.getAttribute("width"));
            double h=Double.parseDouble(element.getAttribute("height"));
            
            this.arc.pc.set(x,y);
            this.arc.width=w;
            this.arc.height=h;
            
            this.arc.startAngle = Double.parseDouble(element.getAttribute("start"));       
            this.arc.endAngle = Double.parseDouble(element.getAttribute("extend"));
            
            this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
            this.setFill(Fill.values()[Integer.parseInt(element.getAttribute("fill"))]);
        }
        
        
    }

    @Override
    public void move(double xoffset, double yoffset) {
        this.arc.move(xoffset, yoffset);        
    }
    @Override
    public void mirror(Line line) {        
        this.arc.mirror(line);
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Box rect = this.arc.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : this.fillColor);
        
        Arcellipse e=this.arc.clone();   
        e.scale(scale.getScaleX());
        e.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        if (fill == Fill.EMPTY) { //framed
            double wireWidth = thickness * scale.getScaleX();
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
            //transparent rect
            e.paint(g2, false);
        } else { //filled
            e.paint(g2,true);
        }
        
        if (this.isSelected()) {            
                Point pt=null;
                if(resizingPoint!=null){
                    pt=resizingPoint.clone();
                    pt.scale(scale.getScaleX());
                    pt.move(-viewportWindow.getX(),- viewportWindow.getY());
                }
                for(Point p:e.vertices()){
                  Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),p); 
                }                      
        }

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) { 
        if(fill == Fill.EMPTY){    //framed         
          g2.setStroke(new BasicStroke(thickness,1,1));    
          g2.setPaint(Color.BLACK);        
          this.arc.paint(g2,false);
        }else{               //filled  
          g2.setColor(Color.BLACK);  
          this.arc.paint(g2,true);
        }        
        
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }
    public static class Memento extends AbstractMemento<Symbol,Arc> {
        
        private double rotate;
        private double x,y;
        private double width,height;
        private double startAngle,endAngle;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(Arc shape) {
            super.loadStateTo(shape);
            shape.arc.pc.set(x,y);
            shape.arc.width=width;
            shape.arc.height=height;
            shape.arc.rotate=rotate;
            shape.arc.startAngle=startAngle;
            shape.arc.endAngle=endAngle;
        }

        @Override
        public void saveStateFrom(Arc shape) {
            super.saveStateFrom(shape);
            this.x=shape.arc.pc.x;
            this.y=shape.arc.pc.y;
            this.width=shape.arc.width;
            this.height=shape.arc.height;
            this.rotate=shape.arc.rotate;    
            this.startAngle=shape.arc.startAngle;
            this.endAngle=shape.arc.endAngle;
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
            return super.equals(obj) && Utils.EQ(this.rotate, other.rotate)&&Utils.EQ(this.startAngle, other.startAngle)&&Utils.EQ(this.endAngle, other.endAngle)&&
            Utils.EQ(this.x, other.x)&&Utils.EQ(this.y,other.y)&&    
            Utils.EQ(this.width, other.width)&&Utils.EQ(this.height,other.height);                
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.rotate)+Double.hashCode(this.startAngle)+Double.hashCode(this.endAngle)+
                    Double.hashCode(this.x)+Double.hashCode(this.y)+                   
                    Double.hashCode(this.width)+Double.hashCode(this.height);                    
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            boolean flag = super.isSameState(unit);
            Arc other = (Arc) unit.getShape(this.getUUID());
            return flag&&
            Utils.EQ(this.rotate, other.arc.rotate)&&Utils.EQ(this.startAngle, other.arc.startAngle)&& Utils.EQ(this.endAngle, other.arc.endAngle)&&    
            Utils.EQ(this.x, other.arc.pc.x)&& Utils.EQ(this.y, other.arc.pc.y)&&Utils.EQ(this.width, other.arc.width)&&Utils.EQ(this.height, other.arc.height);             
        }

    }    
}
