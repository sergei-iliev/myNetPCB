package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.layer.Layer.Side;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.core.pad.shape.PadShape.Shape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.pad.shape.pad.CircularShape;
import com.mynetpcb.pad.shape.pad.OvalShape;
import com.mynetpcb.pad.shape.pad.PolygonShape;
import com.mynetpcb.pad.shape.pad.RectangularShape;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *Pad is a composite shape consisting of circle,ellipse,rectangle combination
 */
public class Pad extends PadShape{
    
    private Drill drill;

    private PadDrawing shape;

    private PadShape.Type type;

    private double width,height;
    
    private FontTexture number,netvalue; 
    
    public Pad(double width,double height) { 
        this.width=width;
        this.height=height;
        this.rotate=0;
        this.displayName="Pad";
        this.shape=new CircularShape(0,0,width,this);
        this.setType(PadShape.Type.THROUGH_HOLE);  
        
        this.number=new FontTexture("number","1",0,0,4000);
        this.netvalue=new FontTexture("netvalue","",0,0,4000);  
    }
    public Pad clone() throws CloneNotSupportedException {
        Pad copy = (Pad) super.clone();
        copy.setType(this.getType());
        copy.rotate=this.rotate;
        copy.shape=this.shape.copy(copy);        
        //copy.offset=new Point(this.offset.x,this.offset.y);

        copy.number = this.number.clone();
        copy.netvalue = this.netvalue.clone();
        if (drill != null) {
            copy.drill = drill.clone();
        }
        return copy;
    }   
    
    @Override
    public Point alignToGrid(boolean isRequired) {        
        Point center=this.getCenter();
        Point point=this.getOwningUnit().getGrid().positionOnGrid(this.getCenter());
        this.move(point.x - center.x,point.y - center.y);
        return null;                 
    }
    
    @Override
    public Point getPinPoint() {        
        return this.shape.getCenter();
    }
    
    @Override
    public Point getCenter(){
        return this.shape.getCenter();
    }
    
    @Override
    public PadDrawing getPadDrawing(){
        return shape;
    }
    
    public void setType(PadShape.Type type) {
                    this.type = type;
                    switch(type){
                    case THROUGH_HOLE:
                        if(this.drill==null){
                            this.drill=new Drill(this.shape.getCenter().x,this.shape.getCenter().y,Grid.MM_TO_COORD(0.8));                                            
                        }
                        break;
                    case SMD:
                            this.drill=null;
                        break;
                            }
    }
    private void setShape(double x,double y,Shape shape) {
        switch (shape) {
        case CIRCULAR:
            this.shape = new CircularShape(x,y,this.width,this);
            break;
        case OVAL:
           this.shape=new OvalShape(x,y,this.width,this.height,this);
            break;
        case RECTANGULAR:
            this.shape = new RectangularShape(x,y,this.width,this.height,this);
            break;
        case POLYGON:
            this.shape = new PolygonShape(x,y,this.width,this);
            break;
        }
        //restore rotation
        if(this.rotate!=0){
            this.shape.rotate(this.rotate,this.shape.getCenter());
        }        
    } 
    public void setShape(Shape shape) {
        switch (shape) {
        case CIRCULAR:
            this.shape = new CircularShape(this.shape.getCenter().x,this.shape.getCenter().y,this.width,this);
            break;
        case OVAL:
           this.shape=new OvalShape(this.shape.getCenter().x,this.shape.getCenter().y,this.width,this.height,this);
            break;
        case RECTANGULAR:
            this.shape = new RectangularShape(this.shape.getCenter().x,this.shape.getCenter().y,this.width,this.height,this);
            break;
        case POLYGON:
            this.shape = new PolygonShape(this.shape.getCenter().x,this.shape.getCenter().y,this.width,this);
            break;
        }
        //restore rotation
        if(this.rotate!=0){
            this.shape.rotate(this.rotate,this.shape.getCenter());
        }         
    }  
    public double getWidth(){
        return width;
    }
    public double getHeight(){
        return height;
    }
    
    public void setWidth(double width){
                    this.width=width;
                    this.shape.setSize(width,height);    
    }
    
    public void setHeight(double height){
                    this.height=height;
                    this.shape.setSize(width,height);   
    }   
    @Override
    public void setRotation(double angle,Point center){
          double alpha=angle-this.rotate;   
        
          this.shape.rotate(alpha,center);
          
          this.number.setRotation(angle,center);
          this.netvalue.setRotation(angle,center);
          
          if(this.drill!=null){
                this.drill.rotate(alpha,center);
          }               
        
        this.rotate=angle;        
    }
    @Override
    public void rotate(double angle, Point pt) {
        //fix angle
        double alpha=this.rotate+angle;
        if(alpha>=360){
                alpha-=360;
        }
         if(alpha<0){
                 alpha+=360; 
         }
        this.shape.rotate(angle,pt);      
        if(this.drill!=null){
           this.drill.rotate(angle,pt);
        }
        this.number.setRotation(alpha,pt);
        this.netvalue.setRotation(alpha,pt);
        this.rotate=alpha;
    }
  
    @Override
    public void setSide(Layer.Side side, Line line, double angle) {
        this.setCopper(Layer.Side.change(this.getCopper().getLayerMaskID()));
        this.netvalue.setSide(side,line,angle);
        this.number.setSide(side,line,angle);
        this.shape.mirror(line);
        if(this.drill!=null){
           this.drill.mirror(line);
        }
        this.rotate=angle;
    }
    public void move(double xoffset,double yoffset){
               this.shape.move(xoffset, yoffset);
               
               if(this.drill!=null){
                 this.drill.move(xoffset, yoffset);
               }
               this.number.move(xoffset,yoffset);
               this.netvalue.move(xoffset,yoffset);
    }
    
    @Override
    public void mirror(Line line) {
        this.netvalue.mirror(line);
        this.number.mirror(line);
        this.shape.mirror(line);
        if(this.drill!=null){
           this.drill.mirror(line);
        }                
    }
    
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        switch (type) {
        case THROUGH_HOLE:case CONNECTOR:
            shape.print(g2, printContext, layermask);
            if (drill != null) {
                drill.print(g2, printContext, layermask);
            }
            break;
        case SMD:
            shape.print(g2, printContext, layermask);
            break;
        }
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        switch(this.type){
            case THROUGH_HOLE:
                if(this.shape.paint(g2, viewportWindow, scale)){
                 if(this.drill!=null){
                    this.drill.paint(g2, viewportWindow, scale,layermask);
                 }
                }
                break;
            case SMD:
                this.shape.paint(g2, viewportWindow, scale);
                break;
            
            }
            this.number.paint(g2, viewportWindow, scale,0);
            this.netvalue.paint(g2, viewportWindow, scale,0);
     } 
    public Drill getDrill(){
        return drill;
    }
    @Override
    public Box getBoundingShape() {        
        return this.shape.getBoundingShape();
    }
    
    @Override
    public boolean isClicked(int x,int y){
        return this.shape.contains(new Point(x,y));         
    }
    public void setSelected (boolean selection) {
        super.setSelected(selection);
        this.number.setSelected(selection);
        this.netvalue.setSelected(selection);
    }
    @Override
    public PadShape.Shape getShapeType() {

            if(this.shape instanceof CircularShape)
                return PadShape.Shape.CIRCULAR;
            if(this.shape instanceof RectangularShape)
                return PadShape.Shape.RECTANGULAR;
            if(this.shape instanceof OvalShape)
                return PadShape.Shape.OVAL;
            if(this.shape instanceof PolygonShape)
                return PadShape.Shape.POLYGON;                
        return null;
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    @Override
    public PadShape.Type getType() {        
        return type;
    }

    @Override
    public Texture getClickedTexture(int x, int y) {
        if(number.isClicked(x, y))
            return number;
        else if(netvalue.isClicked(x, y))
            return netvalue;
        else
        return null;
    }

    @Override
    public boolean isClickedTexture(int x, int y) {
        return getClickedTexture(x, y)!=null;
    }

    @Override
    public Texture getTextureByTag(String tag) {
        if(tag.equals(number.getTag()))
            return number;
        else if(tag.equals(netvalue.getTag()))
            return netvalue;
        else
        return null;
    }
    @Override
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<pad copper=\"" + getCopper().getName() + "\" type=\"" + getType() + "\" shape=\"" + getShapeType() +
                      "\" x=\"" + Utilities.roundDouble(shape.getCenter().x) + "\" y=\"" + Utilities.roundDouble(shape.getCenter().y) + "\" width=\"" + getWidth() + "\" height=\"" +
                      getHeight() + "\" rt=\"" + this.rotate + "\">\r\n");
       // sb.append("<offset x=\"" + offset.x + "\" y=\"" + offset.y + "\" />\r\n");

        if (!number.isEmpty())
            sb.append("<number>" + number.toXML() + "</number>\r\n");
        if (!netvalue.isEmpty())
            sb.append("<netvalue>" + netvalue.toXML() + "</netvalue>\r\n");
        if (drill != null) {
            sb.append(drill.toXML() + "\r\n");
        }
        sb.append("</pad>\r\n");        
        return sb.toString();
    }

    @Override
    public void fromXML(Node node){
        Element element = (Element) node;
        this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));
        //fix copper All->copper Cu
        if(this.copper.getLayerMaskID()==Layer.LAYER_ALL){
            this.setCopper(Layer.Copper.Cu);
        }
        this.setType(Pad.Type.valueOf(element.getAttribute("type")));
        
        double x=Double.parseDouble(element.getAttribute("x"));
        double y=Double.parseDouble(element.getAttribute("y"));
        
        this.width=Double.parseDouble(element.getAttribute("width"));
        this.height=Double.parseDouble(element.getAttribute("height"));
        
        if(element.getAttribute("rt").length()>0){
          this.rotate=Double.parseDouble(element.getAttribute("rt"));
        }
        this.setShape(x,y,Pad.Shape.valueOf(element.getAttribute("shape")));

        //Element offset = (Element) element.getElementsByTagName("offset").item(0);
        //this.offset.x = (Integer.parseInt(offset.getAttribute("x")));
        //this.offset.y = (Integer.parseInt(offset.getAttribute("y")));
        if (drill != null) {
            drill.fromXML(element.getElementsByTagName("drill").item(0));
        }

        Element number = (Element) element.getElementsByTagName("number").item(0);
        if (number == null) {
            this.number.move(x, y);
        } else {
            this.number.fromXML(number);
        }
        Element netvalue = (Element) element.getElementsByTagName("netvalue").item(0);
        if (netvalue == null) {
            this.netvalue.move(x, y);
        } else {
            this.netvalue.fromXML(netvalue);
        }

    }
    

    
    public static class Memento extends AbstractMemento<Footprint, Pad> {

        private Texture.Memento number,netvalue;

        private Drill.Memento drill;

        private PadDrawing.Memento drawing;
        
        private double x, y, width, height,rotate;

        private int type;

    
 

        public Memento(MementoType mementoType) {
            super(mementoType);
            number=new FontTexture.Memento();
            netvalue=new FontTexture.Memento();        
            drill = new Drill.Memento(mementoType);        
        }

        @Override
        public void clear() {
            super.clear();            
            drill.clear();
        }

        @Override
        public void loadStateTo(Pad pad) {
            super.loadStateTo(pad);

            pad.width=width;
            pad.height=height;
            pad.rotate=rotate;
            pad.setType(Type.values()[type]);

            //could be different shape
            if(drawing.getClass()!=pad.shape.getClass()){
                pad.setShape(getShape());           
            }            
            drawing.loadStateTo(pad.shape);
            
            number.loadStateTo(pad.number);
            netvalue.loadStateTo(pad.netvalue);
            drill.loadStateTo(pad.drill);
        }

        @Override
        public void saveStateFrom(Pad pad) {
            super.saveStateFrom(pad);
            x = pad.shape.getCenter().x;
            y = pad.shape.getCenter().y;
            width = pad.getWidth();
            height = pad.getHeight();
            rotate = pad.rotate;
            type = pad.getType().ordinal();
            
            drawing=pad.shape.getState();
            number.saveStateFrom(pad.number);
            netvalue.saveStateFrom(pad.netvalue);
            drill.saveStateFrom(pad.drill);
        }
        private PadShape.Shape getShape(){
            if(drawing instanceof CircularShape.Memento){
                return Shape.CIRCULAR;           
            }else if(drawing instanceof OvalShape.Memento){
               return Shape.OVAL; 
            }else if(drawing instanceof PolygonShape.Memento){
                return Shape.POLYGON;                     
            }else{
                return Shape.RECTANGULAR;                     
            } 
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
            
            return super.equals(obj)&&
                   Utils.EQ(x,other.x)&&Utils.EQ(y,other.y)&&Utils.EQ(width,other.width)&&Utils.EQ(height,other.height)&&
                   type==other.type&&getShape().ordinal()==other.getShape().ordinal()&&drill.equals(other.drill)&&number.equals(other.number)&&netvalue.equals(other.netvalue);
                   

        }

        @Override
        public int hashCode() {
            int hash = super.hashCode()+            
            Double.hashCode(x)+Double.hashCode(y)+Double.hashCode(width)+Double.hashCode(height)+
            type+getShape().ordinal()+drill.hashCode()+number.hashCode()+netvalue.hashCode();    
            return hash;
        }

        @Override
        public boolean isSameState(Unit unit) {
            Pad pad = (Pad) unit.getShape(getUUID());
            return (pad.getState(getMementoType()).equals(this));
        }

    }
}
