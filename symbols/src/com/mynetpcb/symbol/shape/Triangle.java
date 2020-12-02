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
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polygon;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Triangle extends Shape implements Resizeable, Externalizable{
    private Polygon shape;
    private Point resizingPoint;
    
    public Triangle(int thickness) {
        super(thickness,Layer.LAYER_ALL);
        this.setDisplayName("Triangle");                
        this.selectionRectWidth=2;       
        this.shape=new Polygon();        
        this.shape.points.add(new Point(0,0));
        this.shape.points.add(new Point(20,20));
        this.shape.points.add(new Point(0,40));
    }
    
    @Override
    public Triangle clone() throws CloneNotSupportedException {        
        Triangle copy=(Triangle)super.clone(); 
        copy.shape=this.shape.clone();  
        copy.fill = this.fill;
        return copy;
    }
    @Override
    public long getClickableOrder() {        
        return (long)getBoundingShape().area();
    }
    @Override
    public Box getBoundingShape(){
      return this.shape.box();                
    }
    @Override
    public Point getCenter(){
       return this.shape.box().getCenter();
    }
    
    @Override
    public void move(double xoffset,double yoffset) {
       this.shape.move(xoffset,yoffset);       
    }
    @Override
    public void mirror(Line line) {
        this.shape.mirror(line);                
    }
    @Override
    public boolean isClicked(int x,int y) {
      return this.shape.contains(new Point(x, y));       
    }
    @Override
    public Point isControlRectClicked(int x, int y) {
        Box rect = Box.fromRect(x
                        - this.selectionRectWidth / 2, y - this.selectionRectWidth
                        / 2, this.selectionRectWidth, this.selectionRectWidth);

        
        Optional<Point> opt= this.shape.points.stream().filter(( wirePoint)->rect.contains(wirePoint)).findFirst();                  
                  
        
        return opt.orElse(null);
    }

    @Override
    public Point getResizingPoint() {     
        return this.resizingPoint;
    }

    @Override
    public void setResizingPoint(Point pt) {
        this.resizingPoint=pt;
    }
    @Override
    public void rotate(double angle, Point origin) {                
        this.shape.rotate(angle,origin);    
    }    
    @Override
    public void resize(int xoffset, int yoffset, Point clickedPoint) {
        clickedPoint.set(clickedPoint.x+xoffset, clickedPoint.y+yoffset);
    }

    @Override
    public void alignResizingPointToGrid(Point point) {
        getOwningUnit().getGrid().snapToGrid(point); 
    }

    @Override
    public String toXML() {
        StringBuffer points=new StringBuffer();
        this.shape.points.forEach(point->{
           points.append(Utilities.roundDouble(point.x,1));
           points.append(",");
           points.append(Utilities.roundDouble(point.y,1));
           points.append(",");
        });     
        return "<triangle thickness=\"" + this.thickness + "\" fill=\"" + this.fill.ordinal() + "\">"+points.toString()+"</triangle>\r\n";
    }

    @Override
    public void fromXML(Node node) {
        Element element =(Element)node;
        if(element.hasAttribute("thickness")){        
            
            this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
            this.setFill(Fill.values()[(element.getAttribute("fill")==""?0:Integer.parseInt(element.getAttribute("fill")))]);  
            StringTokenizer st=new StringTokenizer(node.getTextContent(),",");
            
            this.shape.points.get(0).set(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));
            this.shape.points.get(1).set(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));
            this.shape.points.get(2).set(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));
            
        }else{
            StringTokenizer st=new StringTokenizer(node.getTextContent(),",");
            int orientation=Integer.parseInt(st.nextToken());
            this.initPoints(orientation,Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));           
            setThickness(Byte.parseByte(st.nextToken()));
            setFill(Fill.byIndex(Byte.parseByte(st.nextToken())));                 
        }
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Box rect = this.shape.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        
        g2.setColor(isSelected() ? Color.GRAY : this.fillColor);
        double wireWidth = thickness * scale.getScaleX();
        g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
        
         
        Polygon a=this.shape.clone();       
        a.scale(scale.getScaleX());
        a.move(-viewportWindow.getX(),- viewportWindow.getY());
        if (fill == Fill.EMPTY) { //framed
            //transparent rect
            a.paint(g2,false);
        } else { //filled
            a.paint(g2,true);
        } 
        if (this.isSelected()) {
            Point pt=null;
            if(resizingPoint!=null){
                pt=resizingPoint.clone();
                pt.scale(scale.getScaleX());
                pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }
            for(Point p:a.points){
              Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),p); 
            }            
        }

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(Color.BLACK);  
        if (fill == Fill.EMPTY) { //framed            
            shape.paint(g2, false);
        } else { //filled
            shape.paint(g2,true);
        }        
    }
    //***old schema
    //DIRECTION_WEST = 0x01;
    //DIRECTION_NORTH = 0x02;
    //DIRECTION_EAST = 0x04;
    //DIRECTION_SOUTH = 0x08;
    private void initPoints(int orientation,double x,double y,double width,double height){     
        if(orientation==0x01){   
            this.shape.points.get(0).set(x,y+height/2);        
            this.shape.points.get(1).set(x+width,y);            
            this.shape.points.get(2).set(x+width,y+height);            
        }else if(orientation==0x02){        
            this.shape.points.get(0).set(x+width/2, y);
            this.shape.points.get(1).set(x+width, y+height);
            this.shape.points.get(2).set(x, y+height);            
        }else if(orientation==0x04){                  
            this.shape.points.get(0).set(x+width,y+height/2);
            this.shape.points.get(1).set(x,y+height);
            this.shape.points.get(2).set(x,y);            
        }else{      
            this.shape.points.get(0).set(x+width/2,y+height);
            this.shape.points.get(1).set(x,y);
            this.shape.points.get(2).set(x+width,y);
          
        }
        
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }
        
    public static class Memento extends AbstractMemento<Symbol, Triangle> {

        private double Ax[];

        private double Ay[];
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(Triangle shape) {
            super.loadStateTo(shape);            
            for (int i = 0; i < Ax.length; i++) {
                shape.shape.points.get(i).x=Ax[i];
                shape.shape.points.get(i).y=Ay[i];                
            }
        }

        @Override
        public void saveStateFrom(Triangle shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.shape.points.size()];
            Ay = new double[shape.shape.points.size()];
            for (int i = 0; i < shape.shape.points.size(); i++) {
                Ax[i] = (shape.shape.points.get(i)).x;
                Ay[i] = (shape.shape.points.get(i)).y;
            }
        }

        @Override
        public void clear() {
            super.clear();
            Ax = null;
            Ay = null;
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
            return (super.equals(obj)&&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode();
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            Triangle line = (Triangle) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }     
}
