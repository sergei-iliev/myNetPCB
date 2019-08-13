package com.mynetpcb.circuit.shape;


import com.mynetpcb.circuit.popup.CircuitPopupMenu;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Moveable;
import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.ChipText;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.shape.Pin;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;


public class SCHConnector extends Shape implements Pinaware<Pin>,Ownerable<Shape>,Textable,Externalizable{

    public enum Type{
       INPUT,OUTPUT;  
    }
    
    public enum Style{
        BOX,ARROW,CIRCLE;  
    } 
    
    private ChipText text;
 
    private WeakReference<Shape> weakParentRef;
    
    private Pin pin;
    
    private Type type;
    
    private BasicStyle style;
    
    public SCHConnector() {
      super(0,0,0,0,1,0);              
      text=new ChipText();
      text.Add(new FontTexture("connector","???",0,0, Text.Alignment.LEFT,Utilities.POINT_TO_POINT));
      text.setFillColor(Color.BLACK);
      this.pin=new Pin(0,0);
      this.pin.setType(Pin.Type.SIMPLE);
      this.pin.setOrientation(Pin.Orientation.WEST);
      this.pin.setSelected(false);
      this.type=Type.OUTPUT;
      style=new BoxStyle();      
    }
    public SCHConnector clone() throws CloneNotSupportedException {
       SCHConnector copy=(SCHConnector)super.clone();
       copy.weakParentRef=null;
       copy.text=text.clone();
       copy.pin=pin.clone();
       copy.setStyle(style.getStyle());
       return copy;
    }
    
    @Override
    public Method showContextPopup()throws NoSuchMethodException, SecurityException{
        return CircuitPopupMenu.class.getDeclaredMethod("registerTextureMethod",new Class[] {MouseScaledEvent.class,Shape.class});        
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {        
        Point point=getOwningUnit().getGrid().positionOnGrid(pin.getX(),pin.getY()); 
        Move(point.x-pin.getX(),point.y-pin.getY());
        return null;
    }
    public void Clear() {
      pin.Clear();
      text.clear();
      style=null;
      setOwningUnit(null);
      setOwner(null);
    }
    public String getName(){
      return text.get(0).getText();  
    }
    
    public void setName(String text){
       this.text.get(0).setText(text);
       style.calculatePoints();
    } 
    public Pin.Orientation getOrientation(){
        return pin.getOrientation();
    }
    
    public void setOrientation(Pin.Orientation orientation){
        this.pin.setOrientation(orientation); 
        switch(orientation){
        case NORTH:
            text.get(0).setOrientation(Text.Orientation.VERTICAL);
            break;
        case SOUTH:      
            text.get(0).setOrientation(Text.Orientation.VERTICAL);
            break;
        case EAST:  
            text.get(0).setOrientation(Text.Orientation.HORIZONTAL);
            break;
        case WEST:                       
            text.get(0).setOrientation(Text.Orientation.HORIZONTAL);            
            break;
        }
        style.calculatePoints();
    }
    
    public void setSelected(boolean isSelected) {
        super.setSelected(isSelected);
        text.setSelected(isSelected);
    } 
    
    public Shape getOwner() {
        if(weakParentRef!=null&&weakParentRef.get()!=null){
          return weakParentRef.get();       
        }
        return null;
    }

    public void setOwner(Shape parent) {
        if(parent==null){
         /*
          * nulify
          */
            if(this.weakParentRef!=null&&this.weakParentRef.get()!=null){
                this.weakParentRef.clear();  
                this.weakParentRef=null;
            }
        }else{
          /*
           * assign
           */
          if(this.weakParentRef!=null&&this.weakParentRef.get()!=null){
              this.weakParentRef.clear();  
          }
          this.weakParentRef=new WeakReference<Shape>(parent);  
        } 
    }
    public Style getStyle(){
        return style.getStyle();
    }
    
    public void setStyle(Style style){
        switch(style){
            case BOX:
                 this.style= new BoxStyle(); 
                 break;
            case ARROW:
                 this.style= new ArrowStyle();      
                 break;
            case CIRCLE:
                 this.style= new CircleStyle();                 
        }
    }
    
    @Override
    public java.awt.Shape calculateShape() {
        return style.getBoundingRect();
    }
    public void Move(int xoffset, int yoffset) {
       pin.Move(xoffset,yoffset);
       style.Move(xoffset, yoffset);    
       text.Move(xoffset,yoffset);
    }
    public void Rotate(AffineTransform rotation) {
        pin.Rotate(rotation);       
        text.Rotate(rotation);       
        style.calculatePoints();
    }
    
    @Override
    public Point getCenter() {    
        return new Point(pin.getX(),pin.getY());
    }
    
//    @Override
//    public void Rotate(Moveable.Rotate type) {
//        switch(type){
//        case LEFT:
//            Rotate(AffineTransform.getRotateInstance(Math.PI/2,pin.getX(),pin.getY()));
//            break;
//        case RIGHT:
//            Rotate(AffineTransform.getRotateInstance(-Math.PI/2,pin.getX(),pin.getY()));
//        }
//    }
    
    public void Mirror(Point A,Point B) {
      pin.Mirror(A,B);
      text.Mirror(A,B);
      style.calculatePoints();
    }
    
//    @Override
//    public void Mirror(Moveable.Mirror type) {
//        switch(type){
//        case HORIZONTAL:
//            Mirror(new Point(pin.getX()-10,pin.getY()),new Point(pin.getX()+10,pin.getY()));
//            break;
//        case VERTICAL:
//            Mirror(new Point(pin.getX(),pin.getY()-10),new Point(pin.getX(),pin.getY()+10));
//        }
//        
//    }
    public void Translate(AffineTransform translate) {
        pin.Translate(translate);  
        text.Translate(translate);    
        style.calculatePoints();    
    }
    public boolean isClicked(int x, int y) {
      return style.isClicked(x,y);            
    }
    
    @Override
    public long getOrderWeight() {
        return 1;
    }
    @Override
    public String getDisplayName() {
        return "Connector";
    }
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds() ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        
        g2.setColor(isSelected()?Color.GRAY:fillColor); 
        style.Paint(g2,viewportWindow,scale);
    }
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        g2.setColor(Color.BLACK); 
        style.Print(g2,printContext,layermask);  
    }

    @Override
    public Rectangle getPinsRect() {
        return new Rectangle(pin.getX(), pin.getY(), 0,0);
    }
    
    @Override
    public Pin getPin(int x, int y) {
        return null;
    }
    
    @Override
    public Collection<Pin> getPins() {
        return Arrays.asList(pin);
    }
    
    @Override
    public ChipText getChipText() {
        return text;
    }
    public Type getType(){
      return type;  
    }
    
    public void setType(Type type){
      this.type=type;
      style.calculatePoints();
    }
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<connector style=\""+getStyle().ordinal()+"\">\r\n");
        sb.append("<type>"+type.ordinal()+"</type>\r\n");
        sb.append("<name>"+text.get(0).toXML()+"</name>\r\n");
        sb.append(pin.toXML());        
        sb.append("</connector>\r\n");
        return sb.toString();
    }

    public void fromXML(Node node)throws XPathExpressionException,ParserConfigurationException{
        org.w3c.dom.Element  element= ( org.w3c.dom.Element)node;
        //packageName=element.getAttribute("packagename")==null?"":element.getAttribute("packagename");        
        Node n=element.getElementsByTagName("pin").item(0); 
        pin.fromXML(n);
        
        n=element.getElementsByTagName("type").item(0);
        this.type=Type.values()[Integer.parseInt(n.getTextContent())];
                
        String style=element.getAttribute("style");
        setStyle(Style.values()[Integer.parseInt(style)]);
        
        n=element.getElementsByTagName("name").item(0);        
        text.get(0).fromXML(n);
        
        //points are calculated in BaseConnector constructor
        this.style.calculatePoints();
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
        //this.setOwner(getOwningUnit().getSymbol(memento.getParentUUID()));
        //this.setSelected(false);
    }
    //***************************************************************************************************************
    private abstract class BasicStyle{
        protected static final int POINTER_DEPTH=4;  
        
        protected boolean recalculate=true;
        
        protected final int pointsNumber;
        
        protected final Point[] points= {new Point(),new Point(),new Point(),new Point(),new Point()};
        
        private BasicStyle(int pointsNumber){
          this.pointsNumber=pointsNumber;  
          calculatePoints();
        }
        
        protected abstract Style getStyle();
        
        protected abstract void calculatePoints();
        
        protected abstract void Paint(Graphics2D g2,ViewportWindow viewportWindow, AffineTransform scale);
        
        protected abstract void Print(Graphics2D g2,PrintContext printContext,int layermask) ;
        
        private void Move(int xoffset,int yoffset){
            for(int i=0;i<points.length;i++){
                 points[i].setLocation(points[i].x+xoffset,points[i].y+yoffset);
            }  
        }        
        
        public boolean isClicked(int x, int y) {
            if(pin.isClicked(x,y))
              return true;
            else{
                GeneralPath figure = new GeneralPath(GeneralPath.WIND_EVEN_ODD, pointsNumber);
                figure.moveTo((float)points[0].getX(), (float)points[0].getY());
                for (int index = 1; index < pointsNumber; index++) {
                      figure.lineTo((float)points[index].getX(), (float)points[index].getY());
                }
                figure.closePath();  
                
                return figure.contains(x,y);
            }
        }
        
        public Rectangle getBoundingRect() {
            int x1=Integer.MAX_VALUE,y1=Integer.MAX_VALUE,x2=Integer.MIN_VALUE,y2=Integer.MIN_VALUE; 
            
            for(int i=0;i<pointsNumber;i++){
                x1=Math.min(x1,points[i].x);
                y1=Math.min(y1,points[i].y);
                x2=Math.max(x2,points[i].x);
                y2=Math.max(y2,points[i].y);                        
            }                
            return new Rectangle(x1,y1,x2-x1,y2-y1);
        }
    }
    private class BoxStyle extends BasicStyle{
        private BoxStyle(){
          super(5);  
        }
        
        protected  Style getStyle(){
            return Style.BOX;
        }
        
        public void Paint(Graphics2D g2,ViewportWindow viewportWindow, AffineTransform scale) {            
            pin.Paint(g2,viewportWindow,scale,Layer.LAYER_ALL);
            
            FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
            GeneralPath temporal=(GeneralPath)provider.getShape();
            
            temporal.moveTo(points[0].getX(),points[0].getY());
            for(int i=1;i<points.length;i++){            
                  temporal.lineTo(points[i].getX(),points[i].getY());       
            } 
            temporal.closePath();  
            AffineTransform translate= AffineTransform.getTranslateInstance(-viewportWindow.x,-viewportWindow.y);
            
            temporal.transform(scale);
            temporal.transform(translate);
                        
            g2.draw(temporal);              
            text.Paint(g2,viewportWindow,scale,-1);
              //***draw selection
              if(isSelected()){
                  AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);   
                  Composite originalComposite = g2.getComposite();
                  g2.setPaint(Color.gray);                      
                  g2.setComposite(composite );
                  g2.fill(temporal);
                  g2.setComposite(originalComposite);
              }        
           provider.reset();
        }
        
        protected void Print(Graphics2D g2,PrintContext printContext,int layermask) {
            pin.Print(g2, printContext, layermask);
            FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
            GeneralPath temporal=(GeneralPath)provider.getShape();
            
            temporal.moveTo(points[0].getX(),points[0].getY());
            for(int i=1;i<points.length;i++){            
                  temporal.lineTo(points[i].getX(),points[i].getY());       
            } 
            temporal.closePath(); 
            g2.draw(temporal);
            
            text.get(0).Print(g2,printContext,layermask);
            
            provider.reset();
            
        }
        protected void calculatePoints(){
          final int BEGIN_TEXT_OFFSET=4;      
          int pointToPoint = Utilities.POINT_TO_POINT;

          Rectangle2D textRect=null;
            switch(type){
              case INPUT:
                
                   switch(pin.getOrientation()){
                       case NORTH://north
                              text.get(0).setAlignment(Text.Alignment.TOP);
                              textRect=text.getTextureByTag("connector").getBoundingShape().getBounds();
                              points[0].setLocation(pin.getX(),pin.getY());  
                              points[1].setLocation(pin.getX()-pointToPoint/2,pin.getY()+POINTER_DEPTH);
                              points[2].setLocation(pin.getX()-pointToPoint/2,pin.getY()+POINTER_DEPTH+textRect.getWidth()+BEGIN_TEXT_OFFSET);
                              points[3].setLocation(pin.getX()+pointToPoint/2,pin.getY()+POINTER_DEPTH+textRect.getWidth()+BEGIN_TEXT_OFFSET);
                              points[4].setLocation(pin.getX()+pointToPoint/2,pin.getY()+POINTER_DEPTH);                               
                             
                              break;
                       case SOUTH: //south
                              text.get(0).setAlignment(Text.Alignment.BOTTOM); 
                              textRect=text.getTextureByTag("connector").getBoundingShape().getBounds();
                              points[0].setLocation(pin.getX()-pointToPoint/2,pin.getY()-textRect.getWidth()-POINTER_DEPTH-BEGIN_TEXT_OFFSET);
                              points[1].setLocation(pin.getX()-pointToPoint/2,pin.getY()-POINTER_DEPTH);                          
                              points[2].setLocation(pin.getX(),pin.getY());
                              points[3].setLocation(pin.getX()+pointToPoint/2,pin.getY()-POINTER_DEPTH);
                              points[4].setLocation(pin.getX()+pointToPoint/2,pin.getY()-textRect.getWidth()-POINTER_DEPTH-BEGIN_TEXT_OFFSET);                  

                              break;
                       case WEST://west
                              text.get(0).setAlignment(Text.Alignment.LEFT); 
                              textRect=text.getTextureByTag("connector").getBoundingShape().getBounds();
                              points[0].setLocation(pin.getX(),pin.getY());
                              points[1].setLocation(pin.getX()+POINTER_DEPTH,pin.getY()+pointToPoint/2);
                              points[2].setLocation(pin.getX()+textRect.getWidth()+POINTER_DEPTH+BEGIN_TEXT_OFFSET,pin.getY()+pointToPoint/2);
                              points[3].setLocation(pin.getX()+textRect.getWidth()+POINTER_DEPTH+BEGIN_TEXT_OFFSET,pin.getY()-pointToPoint/2); 
                              points[4].setLocation(pin.getX()+POINTER_DEPTH,pin.getY()-pointToPoint/2);                  

                              break;        
                       case EAST: //east
                              text.get(0).setAlignment(Text.Alignment.RIGHT); 
                              textRect=text.getTextureByTag("connector").getBoundingShape().getBounds();
                              points[0].setLocation(pin.getX()-textRect.getWidth()-POINTER_DEPTH-BEGIN_TEXT_OFFSET,pin.getY()-pointToPoint/2);
                              points[1].setLocation(pin.getX()-textRect.getWidth()-POINTER_DEPTH-BEGIN_TEXT_OFFSET,pin.getY()+pointToPoint/2);
                              points[2].setLocation(pin.getX()-POINTER_DEPTH,pin.getY()+pointToPoint/2);
                              points[3].setLocation(pin.getX(),pin.getY());
                              points[4].setLocation(pin.getX()-POINTER_DEPTH,pin.getY()-pointToPoint/2);                                     
                             
                       
                   }            
              break;
            case OUTPUT:
                
                   switch(pin.getOrientation()){
                       case NORTH:  //north
                              text.get(0).setAlignment(Text.Alignment.TOP);
                              textRect=text.getTextureByTag("connector").getBoundingShape().getBounds();
                              points[0].setLocation(pin.getX()-pointToPoint/2,pin.getY());
                              points[1].setLocation(pin.getX()-pointToPoint/2,pin.getY()+textRect.getWidth()+BEGIN_TEXT_OFFSET);
                              points[2].setLocation(pin.getX(),pin.getY()+textRect.getWidth()+POINTER_DEPTH+BEGIN_TEXT_OFFSET);
                              points[3].setLocation(pin.getX()+pointToPoint/2,pin.getY()+textRect.getWidth()+BEGIN_TEXT_OFFSET);
                              points[4].setLocation(pin.getX()+pointToPoint/2,pin.getY());                                                            
                              break;
                       case SOUTH:  //south
                              text.get(0).setAlignment(Text.Alignment.BOTTOM); 
                              textRect=text.getTextureByTag("connector").getBoundingShape().getBounds();
                              points[0].setLocation(pin.getX()-pointToPoint/2,pin.getY());
                              points[1].setLocation(pin.getX()+pointToPoint/2,pin.getY());                          
                              points[2].setLocation(pin.getX()+pointToPoint/2,pin.getY()-textRect.getWidth()-BEGIN_TEXT_OFFSET);
                              points[3].setLocation(pin.getX(),pin.getY()-textRect.getWidth()-POINTER_DEPTH-BEGIN_TEXT_OFFSET);
                              points[4].setLocation(pin.getX()-pointToPoint/2,pin.getY()-textRect.getWidth()-BEGIN_TEXT_OFFSET);                                            
                              break;

                       case WEST:  //west 
                              text.get(0).setAlignment(Text.Alignment.LEFT); 
                              textRect=text.getTextureByTag("connector").getBoundingShape().getBounds();
                              points[0].setLocation(pin.getX(),pin.getY()-pointToPoint/2);
                              points[1].setLocation(pin.getX(),pin.getY()+pointToPoint/2);
                              points[2].setLocation(pin.getX()+textRect.getWidth()+BEGIN_TEXT_OFFSET,pin.getY()+pointToPoint/2);
                              points[3].setLocation(pin.getX()+textRect.getWidth()+POINTER_DEPTH+BEGIN_TEXT_OFFSET,pin.getY()); 
                              points[4].setLocation(pin.getX()+textRect.getWidth()+BEGIN_TEXT_OFFSET,pin.getY()-pointToPoint/2);                                                
                              break;        
                       case EAST: //east
                              text.get(0).setAlignment(Text.Alignment.RIGHT);                        
                              textRect=text.getTextureByTag("connector").getBoundingShape().getBounds();
                              points[0].setLocation(pin.getX()-textRect.getWidth()-BEGIN_TEXT_OFFSET,pin.getY()-pointToPoint/2);
                              points[1].setLocation(pin.getX()-textRect.getWidth()-POINTER_DEPTH-BEGIN_TEXT_OFFSET,pin.getY());
                              points[2].setLocation(pin.getX()-textRect.getWidth()-BEGIN_TEXT_OFFSET,pin.getY()+pointToPoint/2);
                              points[3].setLocation(pin.getX(),pin.getY()+pointToPoint/2);
                              points[4].setLocation(pin.getX(),pin.getY()-pointToPoint/2);                                     
                   }
              
              break;  
            }
           
        }


    }
    private class ArrowStyle extends BasicStyle{
        private ArrowStyle(){
           super(3); 
        }
        protected  Style getStyle(){
            return Style.ARROW;
        }
        public void Paint(Graphics2D g2,ViewportWindow viewportWindow, AffineTransform scale) {
            pin.Paint(g2,viewportWindow, scale,Layer.LAYER_ALL);
            
            FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
            GeneralPath temporal=(GeneralPath)provider.getShape();
            
            temporal.moveTo(points[0].getX(),points[0].getY());
            for(int i=1;i<3;i++){            
                  temporal.lineTo(points[i].getX(),points[i].getY());       
            } 
            temporal.closePath();  
            AffineTransform translate= AffineTransform.getTranslateInstance(-viewportWindow.x,-viewportWindow.y);
            
            temporal.transform(scale);
            temporal.transform(translate);
                        
            g2.draw(temporal);              
            text.Paint(g2,viewportWindow,scale,-1);
              //***draw selection
              if(isSelected()){
                  AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);   
                  Composite originalComposite = g2.getComposite();
                  g2.setPaint(Color.gray);                      
                  g2.setComposite(composite );
                  g2.fill(temporal);
                  g2.setComposite(originalComposite);
              }        
            provider.reset();
              
        }
        
        protected void Print(Graphics2D g2,PrintContext printContext,int layermask) {
            pin.Print(g2, printContext, layermask);
            FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
            GeneralPath temporal=(GeneralPath)provider.getShape();
            
            temporal.moveTo(points[0].getX(),points[0].getY());
            for(int i=1;i<3;i++){            
                  temporal.lineTo(points[i].getX(),points[i].getY());       
            } 
            temporal.closePath(); 
            g2.draw(temporal);
            
            text.get(0).Print(g2,printContext,layermask);
            
            provider.reset();
            
        }
        
        protected void calculatePoints(){      
            int pointToPoint = Utilities.POINT_TO_POINT;            

            switch(type){
              case INPUT:
                
                   switch(pin.getOrientation()){
                       case NORTH://north
                            points[0].setLocation(pin.getX(),pin.getY());
                            points[1].setLocation(pin.getX()-pointToPoint/2,pin.getY()+POINTER_DEPTH);
                            points[2].setLocation(pin.getX()+pointToPoint/2,pin.getY()+POINTER_DEPTH);                              
                              break;
                       case SOUTH: //south
                            points[0].setLocation(pin.getX(),pin.getY());
                            points[1].setLocation(pin.getX()-pointToPoint/2,pin.getY()-POINTER_DEPTH);
                            points[2].setLocation(pin.getX()+pointToPoint/2,pin.getY()-POINTER_DEPTH); 
                              break;
                       case WEST://west
                            points[0].setLocation(pin.getX(),pin.getY());
                            points[1].setLocation(pin.getX()+POINTER_DEPTH,pin.getY()-pointToPoint/2);
                            points[2].setLocation(pin.getX()+POINTER_DEPTH,pin.getY()+pointToPoint/2);                            
                              break;        
                       case EAST: //east
                            points[0].setLocation(pin.getX(),pin.getY());
                            points[1].setLocation(pin.getX()-POINTER_DEPTH,pin.getY()-pointToPoint/2);
                            points[2].setLocation(pin.getX()-POINTER_DEPTH,pin.getY()+pointToPoint/2);                              
                                                                 
                   }            
              break;
            case OUTPUT:
                
                   switch(pin.getOrientation()){
                       case NORTH:  //north
                            points[0].setLocation(pin.getX(),pin.getY()+POINTER_DEPTH);
                            points[1].setLocation(pin.getX()-pointToPoint/2,pin.getY());
                            points[2].setLocation(pin.getX()+pointToPoint/2,pin.getY());                        
                              break;
                       case SOUTH:  //south
                            points[0].setLocation(pin.getX(),pin.getY()-POINTER_DEPTH);
                            points[1].setLocation(pin.getX()-pointToPoint/2,pin.getY());
                            points[2].setLocation(pin.getX()+pointToPoint/2,pin.getY());                            
                              break;
                       case WEST:  //west 
                            points[0].setLocation(pin.getX()+POINTER_DEPTH,pin.getY());
                            points[1].setLocation(pin.getX(),pin.getY()+pointToPoint/2);
                            points[2].setLocation(pin.getX(),pin.getY()-pointToPoint/2);                              
                            break;                       
                       case EAST: //east
                            points[0].setLocation(pin.getX()-POINTER_DEPTH,pin.getY());
                            points[1].setLocation(pin.getX(),pin.getY()+pointToPoint/2);
                            points[2].setLocation(pin.getX(),pin.getY()-pointToPoint/2);   
                        }
              
              break;  
            }
            
        }

    }
    private class CircleStyle extends BasicStyle{

        private CircleStyle(){
          super(4);  
        }
        protected  Style getStyle(){
            return Style.CIRCLE;
        }
        public void Paint(Graphics2D g2,ViewportWindow viewportWindow, AffineTransform scale) {
            pin.Paint(g2,viewportWindow,scale,Layer.LAYER_ALL);
            
            Point2D[] scaledPoints=new Point2D[points.length];
            scale.transform(points,0,scaledPoints,0,points.length);
            
            FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Ellipse2D.class);
            Ellipse2D figure=(Ellipse2D)provider.getShape();
            
            figure.setFrame(scaledPoints[0].getX()-viewportWindow.x,scaledPoints[0].getY()-viewportWindow.y,Math.abs(scaledPoints[0].getX()-scaledPoints[1].getX()),Math.abs(scaledPoints[0].getX()-scaledPoints[1].getX()));                                  
            g2.draw(figure);  
            provider.reset();
            
            provider =ShapeFlyweightFactory.getProvider(Line2D.class);
            Line2D line=(Line2D)provider.getShape();
            
            line.setLine(scaledPoints[0].getX()-viewportWindow.x,scaledPoints[0].getY()-viewportWindow.y, scaledPoints[2].getX()-viewportWindow.x,scaledPoints[2].getY()-viewportWindow.y);
            g2.draw(line);
            
            line.setLine(scaledPoints[1].getX()-viewportWindow.x,scaledPoints[1].getY()-viewportWindow.y, scaledPoints[3].getX()-viewportWindow.x,scaledPoints[3].getY()-viewportWindow.y);
            g2.draw(line);            
            provider.reset();
            
            text.Paint(g2,viewportWindow,scale,Layer.Copper.None.getLayerMaskID());
            
            if(isSelected()){
                AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);   
                Composite originalComposite = g2.getComposite();
                g2.setPaint(Color.gray);                      
                g2.setComposite(composite );
                g2.fill(figure);
                g2.setComposite(originalComposite);
            }               
            
            
        }
        
        protected void Print(Graphics2D g2,PrintContext printContext,int layermask) {
            pin.Print(g2, printContext, layermask);

            
            FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Ellipse2D.class);
            Ellipse2D figure=(Ellipse2D)provider.getShape();
            
            figure.setFrame(points[0].x,points[0].y,Math.abs(points[0].x-points[1].x),Math.abs(points[0].x-points[1].x));                                  
            g2.draw(figure);  
            provider.reset();
            
            provider =ShapeFlyweightFactory.getProvider(Line2D.class);
            Line2D line=(Line2D)provider.getShape();
            
            line.setLine(points[0].x,points[0].y, points[2].x,points[2].y);
            g2.draw(line);
            
            line.setLine(points[1].x,points[1].y, points[3].x,points[3].y);
            g2.draw(line);            
            provider.reset();
            
            text.get(0).Print(g2,printContext,layermask);
            
        }
        
        protected void calculatePoints(){        
            int pointToPoint = Utilities.POINT_TO_POINT;               
                   switch(pin.getOrientation()){
                       case NORTH://north
                            points[0].setLocation(pin.getX()-pointToPoint/2,pin.getY());
                            points[1].setLocation(pin.getX()+pointToPoint/2,pin.getY());
                            points[2].setLocation(pin.getX()+pointToPoint/2,pin.getY()+pointToPoint);                              
                            points[3].setLocation(pin.getX()-pointToPoint/2,pin.getY()+pointToPoint);                             
                              break;
                       case SOUTH: //south
                            points[0].setLocation(pin.getX()-pointToPoint/2,pin.getY()-pointToPoint);
                            points[1].setLocation(pin.getX()+pointToPoint/2,pin.getY()-pointToPoint);
                            points[2].setLocation(pin.getX()+pointToPoint/2,pin.getY());                              
                            points[3].setLocation(pin.getX()-pointToPoint/2,pin.getY()); 
                              break;
                       case WEST://west
                            points[0].setLocation(pin.getX(),pin.getY()-pointToPoint/2);
                            points[1].setLocation(pin.getX()+pointToPoint,pin.getY()-pointToPoint/2);
                            points[2].setLocation(pin.getX()+pointToPoint,pin.getY()+pointToPoint/2);                              
                            points[3].setLocation(pin.getX(),pin.getY()+pointToPoint/2);                    
                              break;        
                       case EAST: //east
                            points[0].setLocation(pin.getX()-pointToPoint,pin.getY()-pointToPoint/2);
                            points[1].setLocation(pin.getX(),pin.getY()-pointToPoint/2);
                            points[2].setLocation(pin.getX(),pin.getY()+pointToPoint/2);                              
                            points[3].setLocation(pin.getX()-pointToPoint,pin.getY()+pointToPoint/2);                                     
                   }             
        }
            
        
    }

    static class Memento extends AbstractMemento<Circuit,SCHConnector>{
        private ChipText.Memento connectorTextMemento;
        
        private AbstractMemento pinMemento;
        
        private Type type;
        
        private Style style;
        
        
        public Memento(MementoType mementoType){
          super(mementoType); 
          connectorTextMemento = new ChipText.Memento();
        }
        
        public void loadStateTo(SCHConnector shape) {
          super.loadStateTo(shape);          
          if(shape.type!=this.type){
             shape.setType(this.type);          
          }
          if(shape.getStyle()!=this.style){
             shape.setStyle(this.style);           
          }
          //symbol.packageName=this.packageName;
          shape.pin.setState(this.pinMemento);
          connectorTextMemento.loadStateTo(shape.getChipText());
          shape.style.calculatePoints();
        }
        
        public void saveStateFrom(SCHConnector shape) {
            super.saveStateFrom(shape);
            this.style=shape.getStyle();
            this.type=shape.type;
            connectorTextMemento.saveStateFrom(shape.getChipText());
            this.pinMemento=shape.pin.getState(mementoType);        
        }

        public void Clear() {
            super.Clear();
            pinMemento.Clear();
            connectorTextMemento.Clear();
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

            return(this.getUUID().equals(other.getUUID()) &&
                   getMementoType().equals(other.getMementoType())&&
                   type==other.type&&
                   style==other.style&&
                   connectorTextMemento.equals(other.connectorTextMemento)&&
                   pinMemento.equals(other.pinMemento)
                );            
          
        }
        
        @Override
        public int hashCode(){
          int hash=getUUID().hashCode();
          hash+=getMementoType().hashCode();
          hash+=type.hashCode();
          hash+=style.hashCode();
          hash+=connectorTextMemento.hashCode();
          hash+=pinMemento.hashCode();
          return hash;
        }
        
        public boolean isSameState(Circuit unit) {
            SCHConnector connector=(SCHConnector)unit.getShape(getUUID());
            return (connector.getState(getMementoType()).equals(this));               
        }
        
    }
}
