package com.mynetpcb.circuit.shape;


import com.mynetpcb.circuit.popup.CircuitPopupMenu;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Moveable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.ChipText;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import java.lang.reflect.Method;

import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SCHBusPin extends SCHWire implements Textable{
    
    private ChipText text;
    
    public SCHBusPin() {
        super(1);
        fillColor=Color.BLACK;        
        getLinePoints().add(new LinePoint(0,0));
        getLinePoints().add(new LinePoint(-8,-8));
        text=new ChipText();
        text.Add(new FontTexture("name","???",0,0,Text.Alignment.LEFT,Utilities.POINT_TO_POINT));
        text.setFillColor(Color.BLACK);
    }
    public SCHBusPin clone() throws CloneNotSupportedException{
        SCHBusPin copy=(SCHBusPin)super.clone();
        copy.text=this.text.clone();
        return copy;
    }
    @Override
    public Method showContextPopup()throws NoSuchMethodException, SecurityException{
        return CircuitPopupMenu.class.getDeclaredMethod("registerTextureMethod",new Class[] {MouseScaledEvent.class,Shape.class});        
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
       Point point=getLinePoints().get(1); 
       Point p=getOwningUnit().getGrid().positionOnGrid(point.x,point.y);        
       text.Move(p.x-point.x,p.y-point.y);  
       super.alignToGrid(isRequired);
       return null;
       //return new Point2D.Double(point.getX()-floatingPoint.getX(),point.getY()-floatingPoint.getY());
    }
    @Override
    public int getDrawingOrder() {        
        return 101;
    }
    public void Clear() {
      super.Clear();
      text.clear();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        this.text.setSelected(selected);
    }
    
    @Override
    public ChipText getChipText() {
        return text;
    }
    @Override
    public void Move(int xoffset, int yoffset) {
        super.Move(xoffset,yoffset);
        text.Move(xoffset,yoffset);
    }
    @Override
    public void Translate(AffineTransform translate) {         
        super.Translate(translate);
        text.Translate(translate);
    }
    
    @Override
    public void Rotate(AffineTransform rotation) {        
        super.Rotate(rotation);
        text.Rotate(rotation);
    }
    @Override
    public Point getCenter() {
        
        return new Point(getLinePoints().get(0).x,getLinePoints().get(0).y);
    }
    
//    @Override
//    public void Rotate(Moveable.Rotate type) {
//        switch(type){
//        case LEFT:
//            Rotate(AffineTransform.getRotateInstance(Math.PI/2,getLinePoints().get(0).x,getLinePoints().get(0).y)); 
//            break;
//        case RIGHT:
//            Rotate(AffineTransform.getRotateInstance(-Math.PI/2,getLinePoints().get(0).x,getLinePoints().get(0).y));
//        }
//    }
    @Override
    public void Mirror(Point A,Point B){
       super.Mirror(A,B);
       text.Mirror(A,B);
    }
//    @Override
//    public void Mirror(Moveable.Mirror type) {
//        switch(type){
//        case HORIZONTAL:
//            Mirror(new Point(getLinePoints().get(0).x-10,getLinePoints().get(0).y),new Point(getLinePoints().get(0).x+10,getLinePoints().get(0).y)); 
//            break;
//        case VERTICAL:
//            Mirror(new Point(getLinePoints().get(0).x,getLinePoints().get(0).y-10),new Point(getLinePoints().get(0).x,getLinePoints().get(0).y+10));             
//        }
//    }
    @Override
    public Point isControlRectClicked(int x, int y) {
        Point point=super.isControlRectClicked(x, y);
        if(getLinePoints().get(0).equals(point)){
           return null; 
        }else{
           return point;
        }
    }
//    @Override
//    public void drawControlShape(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale){ 
//        Utilities.drawCrosshair(g2, viewportWindow, scale, getResizingPoint(), selectionRectWidth,getLinePoints().get(1));
//    }
    
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask){
      super.Print(g2,printContext,layermask);
      text.Paint(g2, new ViewportWindow(0, 0, 0, 0), AffineTransform.getScaleInstance(1, 1),layermask);
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledBoundingRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);         
        
        if(!scaledBoundingRect.intersects(viewportWindow)){
          return;   
        }
        

        double lineThickness=thickness*scale.getScaleX();
        
        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
        GeneralPath temporal=(GeneralPath)provider.getShape(); 
        temporal.moveTo(points.get(0).getX(),points.get(0).getY());
        for(int i=1;i<points.size();i++){            
              temporal.lineTo(points.get(i).getX(),points.get(i).getY());       
        } 
        
        AffineTransform translate= AffineTransform.getTranslateInstance(-viewportWindow.x,-viewportWindow.y);
        
        temporal.transform(scale);
        temporal.transform(translate);

        g2.setStroke(new BasicStroke((float)lineThickness));    
        g2.setColor(isSelected()?Color.GRAY:fillColor);
        
        g2.draw(temporal); 

        if(this.isFloating()) {
            temporal.reset();
            temporal.moveTo(floatingStartPoint.getX(), floatingStartPoint.getY());
            temporal.lineTo(floatingMidPoint.getX(),floatingMidPoint.getY());
            temporal.lineTo(floatingEndPoint.getX(),floatingEndPoint.getY());
                        
            temporal.transform(scale);
            temporal.transform(translate);
            g2.draw(temporal);
        }
        
        provider.reset();
        
        //***draw buspin text
        this.text.Paint(g2,viewportWindow,scale,layermask); 
        
        if(this.isSelected()){
              this.drawControlShape(g2,viewportWindow,scale);
        } 
    }
    @Override
    public String getDisplayName(){
        return "BusPin";
    }
    public void fromXML(Node node) {
           Element element=(Element)node;
           Node n=element.getElementsByTagName("name").item(0);
           this.text.getTextureByTag("name").fromXML(n); 

           NodeList nodelist = element.getElementsByTagName("wirepoints");
           n = nodelist.item(0);
           StringTokenizer st=new StringTokenizer(Utilities.trimCRLF(n.getTextContent()),"|");         
           Point point = new Point();
           StringTokenizer stock=new StringTokenizer(st.nextToken(),",");
           point.setLocation(Integer.parseInt(stock.nextToken()),Integer.parseInt(stock.nextToken()));  

           getLinePoints().get(0).setLocation(point);
           stock=new StringTokenizer(st.nextToken(),",");
           point.setLocation(Integer.parseInt(stock.nextToken()),Integer.parseInt(stock.nextToken())); 
           getLinePoints().get(1).setLocation(point);  
    }
    
    @Override
    public String toXML() {
        StringBuffer xml=new StringBuffer();
        xml.append("<buspin>\r\n");
        xml.append("<name>"+text.getTextureByTag("name").toXML()+"</name>\r\n");
        xml.append("<wirepoints>");
        xml.append(getLinePoints().get(0).x+","+getLinePoints().get(0).y+"|");
        xml.append(getLinePoints().get(1).x+","+getLinePoints().get(1).y+"|");
        xml.append("</wirepoints>\r\n");       
        xml.append("</buspin>\r\n");
        return xml.toString();
    }
    
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        ((Memento)memento).loadStateTo(this);
    }    
    
    static class Memento extends SCHWire.Memento{
        
        private ChipText.Memento buspinTextMemento;
        
        public Memento(MementoType mementoType){
          super(mementoType);  
          buspinTextMemento = new ChipText.Memento();
        }
    
        public void saveStateFrom(SCHBusPin shape) {
            super.saveStateFrom(shape);
            buspinTextMemento.saveStateFrom(shape.getChipText());     
        }
        
        public void loadStateTo(SCHBusPin shape) {
            super.loadStateTo(shape);   
            buspinTextMemento.loadStateTo(shape.getChipText());
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

            return (super.equals(obj)&&other.buspinTextMemento.equals(this.buspinTextMemento));             
          
        }
        
        @Override
        public int hashCode(){
            return super.hashCode()+this.buspinTextMemento.hashCode();
        }
        
        @Override
        public void Clear() {
            super.Clear();
            buspinTextMemento.Clear();
        }
        
        
        public boolean isSameState(Circuit unit) {
            SCHBusPin busPin=(SCHBusPin)unit.getShape(getUUID());
            return (busPin.getState(getMementoType()).equals(this));  
        }        
    }    
}
