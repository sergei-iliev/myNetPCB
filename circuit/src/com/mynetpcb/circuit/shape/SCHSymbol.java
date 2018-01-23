package com.mynetpcb.circuit.shape;


import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Packageable;
import com.mynetpcb.core.capi.PinLineable;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.Typeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Container;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.ChipText;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Packaging;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.shape.FontLabel;
import com.mynetpcb.symbol.shape.Pin;
import com.mynetpcb.symbol.shape.SymbolShapeFactory;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SCHSymbol extends Shape implements Container,Textable,Pinaware<Pin>,Packageable,Externalizable{
    private Typeable.Type type;
    
    private List<Shape> shapes;
    
    private ChipText text;
    
    private String symbolName;
    
    private Packaging packaging;
    
    public SCHSymbol() {
        super(0,0,0,0,0,0);
        this.shapes=new ArrayList<Shape>();
        text = new ChipText();
        text.Add(new FontTexture("reference", "", 0, 0, Text.Alignment.LEFT, 8));
        text.Add(new FontTexture("unit", "", 8, 8, Text.Alignment.LEFT, 8));
        text.setFillColor(Color.BLACK);
        symbolName="";
        packaging=new Packaging();
    }
    
    public SCHSymbol clone() throws CloneNotSupportedException {
        SCHSymbol copy=(SCHSymbol)super.clone();
        copy.text =text.clone();
        copy.shapes=new ArrayList<Shape>();
        for(Shape shape:this.shapes){ 
          copy.Add(shape.clone());  
        }
        copy.packaging.copy(this.packaging);
        return copy;        
    }
    public void Move(int xoffset, int yoffset) {
        for(Shape shape:shapes){
            shape.Move(xoffset,yoffset);
        }        
        //***move module text
         text.Move(xoffset,yoffset);            
    }
    public void Mirror(Point A,Point B) {
        for(Shape shape:shapes){
            shape.Mirror(A,B);       
        }
        text.Mirror(A,B);
    }


    public void Translate(AffineTransform translate) {
        for(Shape shape:shapes)
            shape.Translate(translate);  
        
        //***scale module text
        text.Translate(translate);             
    }

    public void Rotate(AffineTransform rotation) {
        for(Shape shape:shapes)
           shape.Rotate(rotation);        
        //***Rotate text
        text.Rotate(rotation);            
    }

    public void setDisplayName(String symbolName){
        this.symbolName=symbolName; 
    }
    @Override
    public String getDisplayName() {
        return symbolName;
    }
    
    public void setType(Typeable.Type type) {
        this.type = type;
    }

    public Typeable.Type getType() {
        return type;
    }
    @Override
    public List<Shape> getShapes(){
        return shapes;
    }
    public void Add(Shape shape){
      if (shape == null)
            return;   
      shapes.add(shape);  
    }
    
    @Override
    public void Clear() {    
       shapes.clear();
       text.clear();
    }
    
    public void setSelected(boolean isSelected) {
        super.setSelected(isSelected);
    
        for(Shape shape: shapes){
            if(shape instanceof Pin){
              shape.setSelected(isSelected);
            }
        }
        text.setSelected(isSelected);
    } 
    
    @Override
    public ChipText getChipText() {
        return text;
    }
    
    @Override
    public Rectangle calculateShape() {
        Rectangle r = new Rectangle();
        int x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE;

        //***empty schematic,element,package
        if (shapes.size() == 0) {
            return r;
        }

        for (Shape shape : shapes) {
            Rectangle tmp = shape.getBoundingShape().getBounds();
            if (tmp != null) {
                x1 = Math.min(x1, tmp.x);
                y1 = Math.min(y1, tmp.y);
                x2 = Math.max(x2, tmp.x + tmp.width);
                y2 = Math.max(y2, tmp.y + tmp.height);
            }
        }
        r.setRect(x1, y1, x2 - x1, y2 - y1);
        return r; 
    }
    
    @Override
    public long getOrderWeight() {
        java.awt.Shape r=getBoundingShape();
        return (r.getBounds().width * r.getBounds().height);
    }
    
    @Override
    public int getDrawingOrder() {        
        return 99;
    }
    
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        g2.setColor(Color.BLACK); 
        for(Shape shape:shapes){
            shape.Print(g2,printContext,layermask);   
        }
        this.text.Paint(g2, new ViewportWindow(0, 0, 0, 0), AffineTransform.getScaleInstance(1, 1),-1);    
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds() ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        
        g2.setColor(isSelected()?Color.GRAY:fillColor); 
        for(Shape shape:shapes){
            shape.Paint(g2,viewportWindow, scale,layermask);   
        }

        this.text.Paint(g2,viewportWindow,scale,layermask);
        
        if(this.isSelected()){
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);   
            Composite originalComposite = g2.getComposite();
            g2.setPaint(Color.gray);                      
            g2.setComposite(composite );
            Rectangle r=this.getBoundingShape().getBounds();
            Utilities.setScaleRect(r.x, r.y, r.width, r.height, r, scale);
            RoundRectangle2D shape=new RoundRectangle2D.Double(r.x-viewportWindow.x,r.y-viewportWindow.y,r.width,r.height, 10, 10);
            g2.fill(shape);
            g2.setComposite(originalComposite);
        }
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        Rectangle r=getPinsRect();
        //may not have pins
        if(r==null)
           return null;
        Point point=getOwningUnit().getGrid().positionOnGrid(r.x,r.y); 
        Move(point.x-r.x,point.y-r.y);
        return null;
    }
    
    
    @Override
    public Collection<Pin> getPins() {
        List<Pin> pins=new LinkedList<Pin>();
        for(Shape shape:shapes)
            if(shape instanceof Pin)
              pins.add((Pin)shape);  
        return pins; 
    }

    @Override
    public Rectangle getPinsRect(){
        int x1=Integer.MAX_VALUE,y1=Integer.MAX_VALUE,x2=Integer.MIN_VALUE,y2=Integer.MIN_VALUE;
        boolean isPinnable=false;        
        
        for(Shape shape:shapes) 
          if(shape instanceof PinLineable){
              PinLineable element=(PinLineable)shape;
              Point p=element.getPinPoint();
              x1=Math.min(x1,p.x );
              y1=Math.min(y1,p.y);
              x2=Math.max(x2,p.x+0);
              y2=Math.max(y2,p.y +0);             
              isPinnable=true;
          }
        if(isPinnable)
            return new Rectangle(x1,y1,x2-x1,y2-y1);            
        else
            return null;        
    }

    public void fromXML(Node node)throws XPathExpressionException,ParserConfigurationException{
        Element  element= (Element)node;
        
        setType(element.getAttribute("type").equals("")?Symbol.Type.SYMBOL:Symbol.Type.valueOf(element.getAttribute("type")));
        
        //packaging
        NodeList nlist=((Element)node).getElementsByTagName("footprint");
        if(nlist.item(0)!=null){
            Element e=(Element)nlist.item(0);
            packaging.setFootprintLibrary(e.getAttribute("library"));
            packaging.setFootprintCategory(e.getAttribute("category"));
            packaging.setFootprintFileName(e.getAttribute("filename"));
            packaging.setFootprintName(e.getAttribute("name"));
        }
        
        Texture reference= text.getTextureByTag("reference");
     
        Texture unit= text.getTextureByTag("unit");

        Node n=element.getElementsByTagName("name").item(0);
        if(n!=null){
         this.symbolName=n.getTextContent();  
        }       
        n=element.getElementsByTagName("reference").item(0);
        if(n!=null){
            Element ref=(Element)n;  
            NodeList refList=ref.getElementsByTagName("label");            
            if(refList.getLength()==0){
                reference.fromXML(n);              //old schema 
            }else{
                FontLabel.fromXML(refList.item(0),reference);    //new schema 
            }
        }else{
           reference.Move(this.getBoundingShape().getBounds().x,this.getBoundingShape().getBounds().y);
        }    

        n=element.getElementsByTagName("unit").item(0);
        if(n!=null){
            Element unt=(Element)n;  
            NodeList unitList=unt.getElementsByTagName("label");
            
            if(unitList.getLength()==0){
               unit.fromXML(n);                //old schema
            }else{
               FontLabel.fromXML(unitList.item(0),unit);    //new schema 
            }                       
        }else{
           unit.Move(this.getBoundingShape().getBounds().x,this.getBoundingShape().getBounds().y);       
        }    
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try{
        NodeList nodelist = (NodeList) xpath.evaluate("./elements/*", node, XPathConstants.NODESET);
        AbstractShapeFactory shapeFactory=new SymbolShapeFactory();
        for(int i=0;i<nodelist.getLength();i++){
              n=nodelist.item(i);
              Shape shape = shapeFactory.createShape(n);
              this.Add(shape);
        }       
        }catch(XPathExpressionException e){
            e.printStackTrace(System.out);
        }             
    }    
    //***Very similar to MODULE.Save()
    public String toXML() {
        StringBuffer xml=new StringBuffer();
        String type="type=\""+(this.getType()==Symbol.Type.SYMBOL?Typeable.Type.SYMBOL.toString():this.getType())+"\"";
               xml.append("<module "+type+" >\r\n");
               xml.append("<footprint library=\""+ (packaging.getFootprintLibrary()==null?"":packaging.getFootprintLibrary())+"\" category=\""+(packaging.getFootprintCategory()==null?"":packaging.getFootprintCategory())+"\"  filename=\""+(packaging.getFootprintFileName()==null?"":packaging.getFootprintFileName())+"\" name=\""+(packaging.getFootprintName()==null?"":packaging.getFootprintName())+"\"/>\r\n");
               xml.append("<name>"+symbolName+"</name>\r\n");

               xml.append("<reference>"+(text.getTextureByTag("reference")==null?"":FontLabel.toXML(text.getTextureByTag("reference")))+"</reference>\r\n");                           
               xml.append("<unit>"+(text.getTextureByTag("unit")==null?"":FontLabel.toXML(text.getTextureByTag("unit")))+"</unit>\r\n");
               
               //***labels and connectors
               CircuitMgr circuitMgr = CircuitMgr.getInstance();
               if(circuitMgr.getChildrenByParent(getOwningUnit().getShapes(),this).size()>0){
                  xml.append("<children>\r\n");
                     Collection<Shape> children=circuitMgr.getChildrenByParent(getOwningUnit().getShapes(),this);
                     for(Shape child:children){
                       xml.append(((Externalizable)child).toXML());  
                     }
                  xml.append("</children>\r\n");
               }
                  
             xml.append("<elements>\r\n");
             for(Shape e:shapes){
                 xml.append(((Externalizable)e).toXML());
             }
             xml.append("</elements>\r\n");
             xml.append("</module>\r\n");                 
        return xml.toString();  
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
    }

    @Override
    public Pin getPin(int x, int y) {      
        for(Pin pin:getPins()){              
            PinLineable.Pair points  = pin.getPinPoints();
            if(points.getA().x <= x && x <= points.getB().x && points.getA().y <= y && y <= points.getB().y){
              return pin;  
            }
        }             
        return null;  
    }

    @Override
    public Packaging getPackaging() {
       return packaging;
    }

    static class Memento extends AbstractMemento<Circuit,SCHSymbol>{
        private final List<AbstractMemento> mementoList;
        
        private ChipText.Memento text;
        
        private String symbolName;
        
        //private String packageName;
        
        public Memento(MementoType operationType){
           super(operationType);
           mementoList=new LinkedList<AbstractMemento>();
           text=new ChipText.Memento();
        }
        
        public void loadStateTo(SCHSymbol symbol) {
          super.loadStateTo(symbol);
          text.loadStateTo(symbol.text); 
          symbol.symbolName=this.symbolName;
          //shape.packageName=this.packageName;
          /*
           * Symbol is recreated with empty shapes in it
           */
          if(symbol.shapes.size()==0){
            //***fill elements
            AbstractShapeFactory shapeFactory=new SymbolShapeFactory();
            for(AbstractMemento elementMemento:mementoList){
               Shape shape=shapeFactory.createShape(null,elementMemento); 
               symbol.Add(shape);               
            } 
          }
          for(int i=0;i<mementoList.size();i++){
              AbstractMemento memento=mementoList.get(i);
              symbol.shapes.get(i).setState(memento);
          }
        }
        
        public void saveStateFrom(SCHSymbol symbol) {
            super.saveStateFrom(symbol);
            this.text.saveStateFrom(symbol.getChipText());
            for(Shape ashape:symbol.shapes){
                mementoList.add(ashape.getState(mementoType));     
            }            
            this.symbolName=symbol.symbolName;
            //this.packageName=symbol.packageName;
        }
        
        public void Clear() {
            super.Clear();
            for(AbstractMemento memento:mementoList){
              memento.Clear();  
            }
            mementoList.clear();
            text.Clear();
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
            
    
            return(getUUID().equals(other.getUUID())&&
                   getMementoType().equals(other.getMementoType())&&
                   mementoList.equals(other.mementoList)&&
                   symbolName.equals(other.symbolName)&&
                   //packageName.equals(other.packageName)&&
                   text.equals(other.text)
                );
                      
        }

        
        @Override
        public int hashCode(){
            int hash=getUUID().hashCode(); 
            hash+=this.getMementoType().hashCode();
            hash+=this.mementoList.hashCode();
            hash+=this.symbolName.hashCode();
            //hash+=this.packageName.hashCode();
            hash+=this.text.hashCode();
            return hash;  
        }              
        
        public boolean isSameState(Circuit unit) {
            SCHSymbol shape=(SCHSymbol)unit.getShape(getUUID());            
            if(shape==null){
                throw new IllegalStateException("shape uuid="+getUUID()+" does not exist");
            }
            return (shape.getState(getMementoType()).equals(this));  
        }

    }

}
