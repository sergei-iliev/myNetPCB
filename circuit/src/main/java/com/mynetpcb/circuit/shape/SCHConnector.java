package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.pin.Pinable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polygon;
import com.mynetpcb.d2.shapes.Segment;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.d2.shapes.Vector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class SCHConnector extends Shape implements Pinable,Textable,Externalizable{

    public enum Type{
       INPUT,OUTPUT;  
    }
    
    public enum Style{
        BOX,ARROW,CIRCLE;  
    } 
    
    private SymbolFontTexture texture;
    private Type type;
    private Segment segment;
    private StyleShape shape;
    public SCHConnector() {
        super( 1,Layer.LAYER_ALL); 
        this.selectionRectWidth=4;
        this.texture=new SymbolFontTexture("label","name",-4,2,Texture.Alignment.RIGHT.ordinal(),8,Font.PLAIN);     
        this.type=Type.INPUT;
        this.displayName="Connector";
        this.segment=new Segment(new Point(0,0),new Point((Utilities.PIN_LENGTH / 2),0));            
        this.shape=new BoxShape(this);        
    }
    
    @Override
    public SCHConnector clone() throws CloneNotSupportedException {
        SCHConnector copy=(SCHConnector)super.clone();
        copy.type=this.type;
        copy.segment=this.segment.clone();
        copy.texture =this.texture.clone(); 
        switch(this.getStyle()){
        case BOX:
                copy.shape=new BoxShape(copy);
                break;
        case ARROW:
                copy.shape=new ArrowShape(copy);
                break;
        case CIRCLE:
                copy.shape=new CircleShape(copy);
        }
        return copy;
    }
    @Override
    public Point alignToGrid(boolean isRequired) {        
        Point point=getOwningUnit().getGrid().positionOnGrid(segment.ps); 
        move(point.x-segment.ps.x,point.y-segment.ps.y);
        return null;
    }    
    @Override
    public Point getPinPoint() {     
        return this.segment.ps;
    }
    @Override
    public Box getBoundingShape() {        
        return segment.box();
    }
    @Override
    public void setSelected (boolean selection) {
            super.setSelected(selection);
            this.texture.setSelected(selection);            
    }       
    public void setType(Type type){
            this.type=type;
            this.shape.calculatePoints();
    }
    public Type getType(){
        return this.type;
    }
    
    public void setText(String text){
        this.texture.setText(text);
        this.shape.calculatePoints();
    }   
    public Style getStyle(){
            if(this.shape instanceof ArrowShape){
                    return  Style.ARROW;
            }else if(this.shape instanceof BoxShape){
                    return  Style.BOX;
            }else{
                    return  Style.CIRCLE;
            }
    }
    public void setStyle(Style shape){
      switch(shape){
      case ARROW:
            this.shape=new ArrowShape(this);  
            break;
      case BOX:
            this.shape=new BoxShape(this);
            break;
      case CIRCLE:
            this.shape=new CircleShape(this);         
      }
      this.shape.calculatePoints();
    }
    @Override
    public Texture getTextureByTag(String string) {        
        return texture;
    }
    @Override
    public Texture getClickedTexture(double x, double y) {
        if(this.texture.isClicked(x, y))
            return this.texture;        
        else
        return null;
    }
    @Override
    public boolean isClickedTexture(double x, double y) {        
        return this.getClickedTexture(x, y)!=null;
    }
    @Override
    public long getClickableOrder() {           
            return 4;
    }    
    @Override
    public boolean isClicked(double x,double y){
            Box rect = Box.fromRect(x
                                    - (this.selectionRectWidth / 2), y
                                    - (this.selectionRectWidth / 2), this.selectionRectWidth,
                                    this.selectionRectWidth);
              
            if (Utils.intersectLineRectangle(
                            this.segment.ps,this.segment.pe, rect.min, rect.max)) {                 
                            return true;
            }else if(this.texture.isClicked(x,y)){
            return true;
        }else
         return   this.shape.contains(new Point(x,y));
     }
    
    @Override
    public void rotate(double angle,Point origin){    
        this.segment.rotate(angle,origin);
        this.texture.setRotation(angle,origin);
        this.shape.calculatePoints();                           
    }
    @Override
    public void mirror(Line line) {        
        this.segment.mirror(line);
        this.texture.setMirror(line);
        this.shape.calculatePoints();
    }
    @Override
    public void move(double xoff, double yoff) {        
           this.segment.move(xoff,yoff);        
           this.shape.move(xoff,yoff);
           this.texture.move(xoff,yoff);                
    }
    
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
        Box rect = this.segment.box();
        rect.scale(scale.getScaleX());           
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected()?Color.BLUE:fillColor); 

        //utilities.drawCrosshair(g2, viewportWindow, scale,null,2,[this.segment.ps.clone()]);
        Segment line=this.segment.clone();
        line.scale(scale.getScaleX());
        line.move(-viewportWindow.getX(),- viewportWindow.getY());
        g2.setStroke(new BasicStroke((float)(this.thickness * scale.getScaleX()))); 
        line.paint(g2,false);
        
        this.shape.paint(g2,viewportWindow, scale);
        this.texture.paint(g2,viewportWindow, scale,layersmask);
    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:fillColor); 

        g2.setStroke(new BasicStroke((float)(this.thickness))); 
        segment.paint(g2,false);
        
        this.shape.print(g2,printContext, layermask);
        this.texture.print(g2,printContext,layermask);  
        
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    private interface StyleShape{
        
        void calculatePoints();
        boolean contains(Point pt);
        void move(double xoff,double yoff);
        void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale);
        void print(Graphics2D g2, PrintContext printContext, int layermask);
    }
    private static class CircleShape implements StyleShape{
            private WeakReference<SCHConnector> connector;
            private Circle circle;
            
        public CircleShape(SCHConnector connector){
            this.connector=new WeakReference<>(connector);
            this.circle=new Circle(new Point(0,0),4);
            this.calculatePoints();            
        }
        @Override
        public void calculatePoints(){
            Vector v=new Vector(this.connector.get().segment.pe,this.connector.get().segment.ps);
            Vector norm=v.normalize();                         
            double x=this.connector.get().segment.ps.x +4*norm.x;
            double y=this.connector.get().segment.ps.y + 4*norm.y;                         
                          
            this.circle.pc.set(x,y);             
        }
        @Override
        public boolean contains(Point pt) {
            return this.circle.contains(pt);
        }
        @Override
        public void move(double xoff, double yoff) {
            this.circle.move(xoff,yoff);

        }
        @Override
        public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {              
            
            Circle c=this.circle.clone();
            c.scale(scale.getScaleX());
            c.move(-viewportWindow.getX(),- viewportWindow.getY());
            
            
            g2.setColor(connector.get().isSelected()?Color.BLUE:connector.get().getFillColor()); 
            c.paint(g2,false);            
        }
        @Override
        public void print(Graphics2D g2, PrintContext printContext, int layermask){

            g2.setColor(connector.get().getFillColor()); 
            circle.paint(g2,false);    
        }
    }
    /*
     * Fix
     */
    private void init(Pinable.Orientation orientation){        
        switch (orientation) {
        case EAST:        
            this.segment.pe.set(this.segment.ps.x + (Utilities.PIN_LENGTH / 2), this.segment.ps.y);
            break;
        case WEST:
            this.segment.pe.set(this.segment.ps.x - (Utilities.PIN_LENGTH / 2), this.segment.ps.y);           
            break;
        case NORTH:
            this.segment.pe.set(this.segment.ps.x, this.segment.ps.y - (Utilities.PIN_LENGTH / 2));           
            break;
        case SOUTH:     
            this.segment.pe.set(this.segment.ps.x, this.segment.ps.y + (Utilities.PIN_LENGTH / 2));
        }   
    }
    private Pinable.Orientation getOrientation(){
        if(this.segment.isHorizontal()){
            if(this.segment.ps.x<this.segment.pe.x){
                return Orientation.EAST;
            }else{
                return Orientation.WEST;           
            }
        }else{
            if(this.segment.ps.y<this.segment.pe.y){
                return Orientation.SOUTH;
            }else{
                return Orientation.NORTH;           
            }            
        }        
    }
    @Override
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<connector type=\""+type.ordinal()+"\" style=\""+getStyle().ordinal()+"\" >\r\n");        
        sb.append("<name>"+texture.toXML()+"</name>\r\n");
        sb.append("<a x=\""+Utilities.roundDouble(this.segment.ps.x,1)+"\" y=\""+Utilities.roundDouble(this.segment.ps.y,1)+"\"  orientation=\""+getOrientation().ordinal()+"\" />\r\n");        
        sb.append("</connector>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        org.w3c.dom.Element  element= ( org.w3c.dom.Element)node;
        if(element.hasAttribute("type")){
            String type=element.getAttribute("type");
            this.type=Type.values()[Integer.parseInt(type)];
            
            String style=element.getAttribute("style");
            setStyle(Style.values()[Integer.parseInt(style)]);

            Node n=element.getElementsByTagName("name").item(0);        
            texture.fromXML(n);
            
            n=element.getElementsByTagName("a").item(0); 
            this.segment.ps.set(Double.parseDouble(((org.w3c.dom.Element)n).getAttribute("x")),Double.parseDouble(((org.w3c.dom.Element)n).getAttribute("y")));
            init(Pinable.Orientation.values()[Byte.parseByte(((org.w3c.dom.Element)n).getAttribute("orientation"))]);            
        }else{   //old schema
            Node n=element.getElementsByTagName("type").item(0);
            this.type=Type.values()[Integer.parseInt(n.getTextContent())];

            String style=element.getAttribute("style");
            setStyle(Style.values()[Integer.parseInt(style)]);
        
            n=element.getElementsByTagName("name").item(0);        
            texture.fromXML(n);
        
            n=element.getElementsByTagName("pin").item(0); 
        
            n=(( org.w3c.dom.Element)n).getElementsByTagName("a").item(0);
            StringTokenizer stock=new StringTokenizer(n.getTextContent(),",");
            this.segment.ps.set(Double.parseDouble(stock.nextToken()),Double.parseDouble(stock.nextToken()));
            stock.nextToken();//crap
            init(Pinable.Orientation.values()[Byte.parseByte(stock.nextToken())]);
        }
                

        
        //points are calculated in BaseConnector constructor
        this.shape.calculatePoints();

    }    
    public static class ArrowShape implements StyleShape{
        private WeakReference<SCHConnector> connector;
        private Polygon polygon;
        
        public ArrowShape(SCHConnector connector){
            this.connector=new WeakReference<>(connector);
            this.polygon=new Polygon();
            this.calculatePoints();            
        }
            
        
        @Override
        public void calculatePoints() {
            this.polygon.points.clear();
            
            switch(this.connector.get().type) {
            case OUTPUT:
                    Vector v=new Vector(this.connector.get().segment.pe,this.connector.get().segment.ps);
                    Vector norm=v.normalize();                         
                    double x=this.connector.get().segment.ps.x +4*norm.x;
                    double y=this.connector.get().segment.ps.y + 4*norm.y;                                                         
                    this.polygon.points.add(new Point(x,y));
            
                    Vector v1=v.clone();
                    v1.rotate90CCW();
                    norm=v1.normalize();                    
                    x=this.connector.get().segment.ps.x +4*norm.x;
                    y=this.connector.get().segment.ps.y + 4*norm.y;                                                             
                    this.polygon.points.add(new Point(x,y));
            
                    Vector v2=v.clone();
                    v2.rotate90CW();
                    norm=v2.normalize();                    
                    x=this.connector.get().segment.ps.x +4*norm.x;
                    y=this.connector.get().segment.ps.y + 4*norm.y;                                                             
                    this.polygon.points.add(new Point(x,y));
                    break;
                    
            case INPUT:
                    v=new Vector(this.connector.get().segment.pe,this.connector.get().segment.ps);
                    norm=v.normalize();                         
                    double xx=this.connector.get().segment.ps.x +4*norm.x;
                    double yy=this.connector.get().segment.ps.y + 4*norm.y;                                                                
                    
                    v1=v.clone();
                    v1.rotate90CCW();
                    norm=v1.normalize();                    
                    x=xx +4*norm.x;
                    y=yy + 4*norm.y;                                                          
                    this.polygon.points.add(new Point(x,y));
                    
                    v1=v.clone();
                    v1.rotate90CW();
                    norm=v1.normalize();                    
                    x=xx +4*norm.x;
                    y=yy + 4*norm.y;                                                              
                    this.polygon.points.add(new Point(x,y));
                    
                    this.polygon.points.add(this.connector.get().segment.ps.clone());
            }        
        }

        @Override
        public boolean contains(Point pt) {
            return this.polygon.contains(pt);
        }

        @Override
        public void move(double xoff, double yoff) {
            this.polygon.move(xoff,yoff);
        }

        @Override
        public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
//            var rect = this.polygon.box;
//            rect.scale(scale.getScale());
//            if (!rect.intersects(viewportWindow)) {
//              return;
//            }               
            
            Polygon p=this.polygon.clone();
            p.scale(scale.getScaleX());
            p.move(-viewportWindow.getX(),- viewportWindow.getY());
            
            g2.setColor(connector.get().isSelected()?Color.BLUE:connector.get().getFillColor()); 
            p.paint(g2,false);

        }
        @Override
        public void print(Graphics2D g2, PrintContext printContext, int layermask){
            g2.setColor(connector.get().getFillColor()); 
            polygon.paint(g2,false);            
        }        
    }
    public static class BoxShape implements StyleShape{
        private WeakReference<SCHConnector> connector;
        private Polygon polygon;
        
        public BoxShape(SCHConnector connector){
            this.connector=new WeakReference<>(connector);
            this.polygon=new Polygon();
            this.calculatePoints();            
        }
        
        @Override
        public void calculatePoints() {
            this.polygon.points.clear();
            Box rect=this.connector.get().texture.shape.box();
            double width=2+rect.getWidth(); 
            double height=2+rect.getHeight();
            
            switch(this.connector.get().type) {
            case OUTPUT:
            if(this.connector.get().segment.isVertical()){
                    Vector v=new Vector(this.connector.get().segment.pe,this.connector.get().segment.ps);
                    Vector v1=v.clone();
                    v1.rotate90CCW();
                    Vector norm=v1.normalize();                        
                    double xx=this.connector.get().segment.ps.x +4*norm.x;
                    double yy=this.connector.get().segment.ps.y + 4*norm.y;                                                                
                    this.polygon.points.add(new Point(xx,yy));
                    
                    Vector v2=v.clone();
                    v2.rotate90CW();
                    norm=v2.normalize();                    
                    double x=this.connector.get().segment.ps.x +4*norm.x;
                    double y=this.connector.get().segment.ps.y + 4*norm.y;                                                         
                    this.polygon.points.add(new Point(x,y));
                    
                    v2.rotate90CCW();
                    norm=v2.normalize();                    
                    x=x +height*norm.x;
                    y=y +height*norm.y;                                                           
                    this.polygon.points.add(new Point(x,y));
                    
                    norm=v.normalize();                     
                    x=this.connector.get().segment.ps.x +(4+height)*norm.x;
                    y=this.connector.get().segment.ps.y +(4+height)*norm.y;                                                             
                    this.polygon.points.add(new Point(x,y));

                    
                    v1.rotate90CW();
                    norm=v1.normalize();                    
                    xx=xx +height*norm.x;
                    yy=yy +height*norm.y;                                                         
                    this.polygon.points.add(new Point(xx,yy));                          
            }else{
                    Vector v=new Vector(this.connector.get().segment.pe,this.connector.get().segment.ps);
                            
                            Vector v1=v.clone();
                            v1.rotate90CCW();
                            Vector norm=v1.normalize();                        
                            double xx=this.connector.get().segment.ps.x +4*norm.x;
                            double yy=this.connector.get().segment.ps.y + 4*norm.y;                                                                
                            this.polygon.points.add(new Point(xx,yy));
                            
                            Vector v2=v.clone();
                            v2.rotate90CW();
                            norm=v2.normalize();                    
                            double x=this.connector.get().segment.ps.x +4*norm.x;
                            double y=this.connector.get().segment.ps.y + 4*norm.y;                                                         
                            this.polygon.points.add(new Point(x,y));
                            
                            v2.rotate90CCW();
                            norm=v2.normalize();                    
                            x=x +width*norm.x;
                            y=y +width*norm.y;                                                            
                            this.polygon.points.add(new Point(x,y));
                            
                            norm=v.normalize();                     
                            x=this.connector.get().segment.ps.x +(4+width)*norm.x;
                            y=this.connector.get().segment.ps.y +(4+width)*norm.y;                                                              
                            this.polygon.points.add(new Point(x,y));

                            
                            v1.rotate90CW();
                            norm=v1.normalize();                    
                            xx=xx +width*norm.x;
                            yy=yy +width*norm.y;                                                          
                            this.polygon.points.add(new Point(xx,yy));                          
             }
            break;
            case INPUT:
            if(this.connector.get().segment.isVertical()){
                            Vector v=new Vector(this.connector.get().segment.pe,this.connector.get().segment.ps);                                                                                               
                            Vector norm=v.normalize();                         
                            double xx=this.connector.get().segment.ps.x +4*norm.x;
                            double yy=this.connector.get().segment.ps.y + 4*norm.y;                                                                
                            
                            
                            
                            Vector v1=v.clone();
                            v1.rotate90CCW();
                            norm=v1.normalize();                    
                            xx=(xx) +4*norm.x;
                            yy=(yy) +4*norm.y;                                                            
                            this.polygon.points.add(new Point(xx,yy));
                            
                            norm=v.normalize();                     
                            double x=xx +height*norm.x;
                            double y=yy +height*norm.y;                                                              
                            this.polygon.points.add(new Point(x,y));

                            v1=v.clone();
                            v1.rotate90CW();
                            norm=v1.normalize();                    
                            x=x +8*norm.x;
                            y=y +8*norm.y;                                                                
                            this.polygon.points.add(new Point(x,y));
                            
                            v1.rotate90CW();
                            norm=v1.normalize();                    
                            x=x +height*norm.x;
                            y=y +height*norm.y;                                                           
                            this.polygon.points.add(new Point(x,y));
                            
                            this.polygon.points.add(this.connector.get().segment.ps.clone());
                    }else{
                            Vector v=new Vector(this.connector.get().segment.pe,this.connector.get().segment.ps);                                                                                               
                            Vector norm=v.normalize();                         
                            double xx=this.connector.get().segment.ps.x +4*norm.x;
                            double yy=this.connector.get().segment.ps.y + 4*norm.y;                                                                
                            
                            
                            
                            Vector v1=v.clone();
                            v1.rotate90CCW();
                            norm=v1.normalize();                    
                            xx=(xx) +4*norm.x;
                            yy=(yy) +4*norm.y;                                                            
                            this.polygon.points.add(new Point(xx,yy));
                            
                            norm=v.normalize();                     
                            double x=xx +width*norm.x;
                            double y=yy +width*norm.y;                                                               
                            this.polygon.points.add(new Point(x,y));

                            v1=v.clone();
                            v1.rotate90CW();
                            norm=v1.normalize();                    
                            x=x +8*norm.x;
                            y=y +8*norm.y;                                                                
                            this.polygon.points.add(new Point(x,y));
                            
                            v1.rotate90CW();
                            norm=v1.normalize();                    
                            x=x +width*norm.x;
                            y=y +width*norm.y;                                                            
                            this.polygon.points.add(new Point(x,y));
                            
                            this.polygon.points.add(this.connector.get().segment.ps.clone()); 
                    }
                    break;
            }            
        }

        @Override
        public boolean contains(Point pt) {
            return this.polygon.contains(pt);
        }

        @Override
        public void move(double xoff, double yoff) {
            this.polygon.move(xoff,yoff);
        }

        @Override
        public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
            Polygon p=this.polygon.clone();
            p.scale(scale.getScaleX());
            p.move(-viewportWindow.getX(),- viewportWindow.getY());
            
            g2.setColor(connector.get().isSelected()?Color.BLUE:connector.get().getFillColor()); 
            p.paint(g2,false);

        }
        @Override
        public void print(Graphics2D g2, PrintContext printContext, int layermask){
            g2.setColor(connector.get().getFillColor()); 
            polygon.paint(g2,false);             
        }        
    }
    static class Memento extends AbstractMemento<Circuit,SCHConnector>{
        private double x1,x2,y1,y2;
        
        private Type type;
        
        private Style style;
        
        private Texture.Memento textureMemento;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
            textureMemento=new SymbolFontTexture.Memento();
        }

        @Override
        public void loadStateTo(SCHConnector shape) {
            super.loadStateTo(shape);            
            textureMemento.loadStateTo(shape.texture);  
            shape.segment.set(x1, y1, x2, y2);
            shape.type=type;
            shape.setStyle(style);
        }

        @Override
        public void saveStateFrom(SCHConnector shape) {
            super.saveStateFrom(shape);
            x1=shape.segment.ps.x;
            y1=shape.segment.ps.y;
            x2=shape.segment.pe.x;
            y2=shape.segment.pe.y;
            
            style=shape.getStyle();
            type=shape.type;
            textureMemento.saveStateFrom(shape.texture);
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
            return (super.equals(obj)&&textureMemento.equals(other.textureMemento)&&
                    Utils.EQ(x1, other.x1)&&Utils.EQ(x2, other.x2)&&Utils.EQ(y1, other.y1)&&Utils.EQ(y2, other.y2)&&
                    type==other.type&&style==other.style);

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode()+textureMemento.hashCode();
            hash+=Double.hashCode(x1)+Double.hashCode(x2)+Double.hashCode(y1)+Double.hashCode(y2);            
            hash += type.hashCode();
            hash += style.hashCode();
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            SCHConnector line = (SCHConnector) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        
        }

    }
    
}
