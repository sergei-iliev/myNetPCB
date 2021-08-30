package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Typeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.pin.CompositePinable;
import com.mynetpcb.core.capi.pin.Pinable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.CompositeTextable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.FontText;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.shape.FontLabel;
import com.mynetpcb.symbol.shape.SymbolShapeFactory;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SCHSymbol extends Shape implements CompositeTextable,Typeable,CompositePinable,Externalizable{
    private List<Shape> shapes;
    private Typeable.Type type;
    private SymbolFontTexture reference,unit;     
    
    public SCHSymbol() {
        super(1,Layer.LAYER_ALL);
        this.shapes=new ArrayList<Shape>();              
        this.reference=new SymbolFontTexture("","reference",0,0,Texture.Alignment.LEFT.ordinal(),8,Font.PLAIN);
        this.unit=new SymbolFontTexture("","unit",0,0,Texture.Alignment.LEFT.ordinal(),8,Font.PLAIN);
        
        this.reference.setFillColor(Color.BLACK);
        this.unit.setFillColor(Color.BLACK);
        
        this.type=Typeable.Type.SYMBOL;
    }
    
    @Override
    public SCHSymbol clone() throws CloneNotSupportedException {
        SCHSymbol copy=(SCHSymbol)super.clone();
        copy.reference =reference.clone();
        copy.unit=unit.clone();
        copy.shapes=new ArrayList<Shape>();        
        for(Shape shape:this.shapes){ 
          copy.shapes.add(shape.clone());  
        }
        return copy;    
    }
    @Override
    public void clear() {    
          this.shapes.forEach(shape->{
                      shape.setOwningUnit(null);
                      shape.clear();
                      shape=null;
         });
         this.shapes.clear();    
         this.unit.clear();
         this.reference.clear();         
    }    
    @Override
    public long getClickableOrder(){
        Box box = this.getBoundingShape();      
        return (long)(box.getWidth()*box.getHeight());
        
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        Box r=getPinsRect();
        //may not have pins
        if(r==null)
           return null;
        Point point=this.getOwningUnit().getGrid().positionOnGrid(r.min.x,r.min.y); 
        this.move(point.x-r.min.x,point.y-r.min.y);
        return null;
    }
    public void add(Shape shape){
      if (shape == null)
            return;   
      shapes.add(shape);  
    }
    public Collection<? extends Shape> getShapes() {
        return this.shapes;
    } 
    @Override
    public Box getBoundingShape(){
        Box r = new Box();
        double x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE;

        //***empty schematic,element,package
        if (shapes.size() == 0) {
            return r;
        }

        for (Shape shape : shapes) {
            Box tmp = shape.getBoundingShape();
            if (tmp != null) {
                x1 = Math.min(x1, tmp.min.x);
                y1 = Math.min(y1, tmp.min.y);
                x2 = Math.max(x2, tmp.max.x);
                y2 = Math.max(y2, tmp.max.y);
            }
        }
        r.setRect(x1, y1, x2 - x1, y2 - y1);
        return r; 
    }    
    @Override
    public void move(double xoffset, double yoffset) {
        for(Shape shape:shapes){
            shape.move(xoffset,yoffset);
        }        
        //***move module text
         unit.move(xoffset,yoffset);
         reference.move(xoffset,yoffset);
    }
    @Override
    public void mirror(Line line){
        for(Shape shape:shapes){
            shape.mirror(line);       
        }

        this.mirrorText(line,this.unit);
        this.mirrorText(line,this.reference);           
    }
    private void mirrorText(Line line,SymbolFontTexture texture){
        int oldalignment = texture.shape.alignment;
        texture.mirror(line);   
        if (line.isVertical()) { //right-left mirroring
            if (texture.shape.alignment == oldalignment) {
                texture.shape.anchorPoint.set(texture.shape.anchorPoint.x +
                                        (texture.shape.metrics.ascent - texture.shape.metrics.descent),texture.shape.anchorPoint.y);
            }
        } else { //***top-botom mirroring          
            if (texture.shape.alignment == oldalignment) {
                    texture.shape.anchorPoint.set(texture.shape.anchorPoint.x,texture.shape.anchorPoint.y +(texture.shape.metrics.ascent - texture.shape.metrics.descent));
            }
        }   
    }    
    @Override
    public void rotate(double angle, Point center){
        for(Shape shape:this.shapes){                    
           shape.rotate(angle,center);  
        }
        this.unit.setRotation(angle,center);
        this.reference.setRotation(angle,center);
    }  
    @Override
    public void setSelected(boolean selected) {        
        super.setSelected(selected);
        for(Shape shape:this.shapes){   
          shape.setFillColor(selected?Color.BLUE:Color.BLACK);
        }
        unit.setSelected(selected);
        reference.setSelected(selected);
    }
    @Override
    public void setType(Typeable.Type type) {
        this.type = type;
    }
    @Override
    public Typeable.Type getType() {
        return type;
    }
    @Override
    public String toXML() {
        StringBuffer xml=new StringBuffer();
        String type="type=\""+(this.getType()==Symbol.Type.SYMBOL?Typeable.Type.SYMBOL.toString():this.getType())+"\"";
               xml.append("<module "+type+" >\r\n");
               //xml.append("<footprint library=\""+ (packaging.getFootprintLibrary()==null?"":packaging.getFootprintLibrary())+"\" category=\""+(packaging.getFootprintCategory()==null?"":packaging.getFootprintCategory())+"\"  filename=\""+(packaging.getFootprintFileName()==null?"":packaging.getFootprintFileName())+"\" name=\""+(packaging.getFootprintName()==null?"":packaging.getFootprintName())+"\"/>\r\n");
               xml.append("<name>"+displayName+"</name>\r\n");

               xml.append("<reference>"+(this.getTextureByTag("reference")==null?"":FontLabel.toXML(this.getTextureByTag("reference")))+"</reference>\r\n");                           
               xml.append("<unit>"+(this.getTextureByTag("unit")==null?"":FontLabel.toXML(this.getTextureByTag("unit")))+"</unit>\r\n");
               
//               //***labels and connectors
//               CircuitMgr circuitMgr = CircuitMgr.getInstance();
//               if(circuitMgr.getChildrenByParent(getOwningUnit().getShapes(),this).size()>0){
//                  xml.append("<children>\r\n");
//                     Collection<Shape> children=circuitMgr.getChildrenByParent(getOwningUnit().getShapes(),this);
//                     for(Shape child:children){
//                       xml.append(((Externalizable)child).toXML());  
//                     }
//                  xml.append("</children>\r\n");
//               }
                  
             xml.append("<elements>\r\n");
             for(Shape e:shapes){
                 xml.append(((Externalizable)e).toXML());
             }
             xml.append("</elements>\r\n");
             xml.append("</module>\r\n");                 
        return xml.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element  element= (Element)node;
        
        setType(element.getAttribute("type").equals("")?Symbol.Type.SYMBOL:Symbol.Type.valueOf(element.getAttribute("type")));
        
//        //packaging
//        NodeList nlist=((Element)node).getElementsByTagName("footprint");
//        if(nlist.item(0)!=null){
//            Element e=(Element)nlist.item(0);
//            packaging.setFootprintLibrary(e.getAttribute("library"));
//            packaging.setFootprintCategory(e.getAttribute("category"));
//            packaging.setFootprintFileName(e.getAttribute("filename"));
//            packaging.setFootprintName(e.getAttribute("name"));
//        }
        
        Texture reference= this.getTextureByTag("reference");        
        Texture unit= this.getTextureByTag("unit");

        Node n=element.getElementsByTagName("name").item(0);
        if(n!=null){
         this.displayName=n.getTextContent();  
        }       
        n=element.getElementsByTagName("reference").item(0);
        if(n!=null){
            Element ref=(Element)n;              
            NodeList refList=ref.getElementsByTagName("label");            
            if(refList.getLength()==0){
                reference.fromXML(n);              //old schema 
            }else{
                //reference.fromXML(refList.item(0));    //new schema
                FontLabel.fromXML(reference,refList.item(0));
            }
        }

        n=element.getElementsByTagName("unit").item(0);
        if(n!=null){
            Element unt=(Element)n;  
            NodeList unitList=unt.getElementsByTagName("label");
            
            if(unitList.getLength()==0){
               unit.fromXML(n);                //old schema
            }else{
               //unit.fromXML(unitList.item(0));    //new schema
            	FontLabel.fromXML(unit,unitList.item(0));
            }                       
        }
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try{
        NodeList nodelist = (NodeList) xpath.evaluate("./elements/*", node, XPathConstants.NODESET);
        AbstractShapeFactory shapeFactory=new SymbolShapeFactory();
        for(int i=0;i<nodelist.getLength();i++){
              n=nodelist.item(i);
              Shape shape = shapeFactory.createShape(n);
              this.add(shape);
        }       
        }catch(XPathExpressionException e){
            e.printStackTrace(System.out);
        }  

    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
        Box rect = this.getBoundingShape();             
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
         return;
        }
                
        for(Shape shape:this.shapes){   
          shape.paint(g2,viewportWindow,scale,layersmask);  
        }   
        unit.paint(g2, viewportWindow, scale, layersmask);
        reference.paint(g2, viewportWindow, scale, layersmask);

    }
    
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {                        
        for(Shape shape:this.shapes){   
          shape.print(g2,printContext,layermask);  
        }   
        unit.print(g2, printContext, layermask);
        reference.print(g2, printContext, layermask);                
    }
    @Override
    public Texture getTextureByTag(String  tag) {
        if(tag.equals(this.reference.getTag()))
            return this.reference;
        else if(tag.equals(this.unit.getTag()))
            return this.unit;
        else
        return null;        
    }
    
    @Override
    public Texture getClickedTexture(double x, double y) {
        if(this.reference.isClicked(x, y))
            return this.reference;
        else if(this.unit.isClicked(x, y))
            return this.unit;
        else
        return null;
    }

    @Override
    public boolean isClickedTexture(double x, double y) {
        return this.getClickedTexture(x, y)!=null;
    }
    @Override
    public  Box getPinsRect(){   
        Box r = new Box();
        double x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE;
        boolean  isPinnable=false;
        //***empty schematic,element,package
        if (shapes.size() == 0) {
            return null;
        }

        for (Shape shape : shapes) {
            if(shape instanceof Pinable){
              Point p=((Pinable)shape).getPinPoint();
              x1=Math.min(x1,p.x );
              y1=Math.min(y1,p.y);
              x2=Math.max(x2,p.x+0);
              y2=Math.max(y2,p.y +0);             
              isPinnable=true;
            }
        }
        r.setRect(x1, y1, x2 - x1, y2 - y1);
        return r;         
        
    }
    @Override
    public Collection<Point> getPinPoints() {
        // TODO Implement this method
        return Collections.emptySet();
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    static class Memento extends AbstractMemento<Circuit,SCHSymbol>{
        
        private final List<AbstractMemento> mementoList;
        
        private SymbolFontTexture.Memento unit,reference;

        private String displayName;
        
        public Memento(MementoType operationType){
           super(operationType);
           mementoList=new LinkedList<AbstractMemento>();
           unit=new SymbolFontTexture.Memento();
           reference=new SymbolFontTexture.Memento();                      
        }
        
        public void loadStateTo(SCHSymbol symbol) {
          super.loadStateTo(symbol);
          unit.loadStateTo(symbol.unit); 
          reference.loadStateTo(symbol.reference);                           
          symbol.setDisplayName(displayName);
          /*
           * Symbol is recreated with empty shapes in it
           */
          if(symbol.shapes.size()==0){
            //***fill elements
            AbstractShapeFactory shapeFactory=new SymbolShapeFactory();
            for(AbstractMemento elementMemento:mementoList){
               Shape shape=shapeFactory.createShape(elementMemento); 
               symbol.add(shape);               
            } 
          }
          for(int i=0;i<mementoList.size();i++){
              AbstractMemento memento=mementoList.get(i);
              symbol.shapes.get(i).setState(memento);
          }
        }
        
        public void saveStateFrom(SCHSymbol symbol) {
            super.saveStateFrom(symbol);
            this.unit.saveStateFrom(symbol.unit);
            this.reference.saveStateFrom(symbol.reference);
            displayName=symbol.getDisplayName();
            for(Shape ashape:symbol.shapes){
                mementoList.add(ashape.getState(mementoType));     
            }            
                        
        }
        @Override
        public void clear() {
            super.clear();
            for(AbstractMemento memento:mementoList){
              memento.clear();  
            }
            displayName=null;
            mementoList.clear();
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
            
    
            return  super.equals(obj)&&Objects.equals(this.displayName, other.displayName)&&                
                   mementoList.equals(other.mementoList)&&
                   reference.equals(other.reference)&&
                   unit.equals(other.unit);         
        }

        
        @Override
        public int hashCode(){
            int hash=super.hashCode(); 
            hash+=Objects.hashCode(displayName);
            hash+=this.mementoList.hashCode();
            hash+=this.unit.hashCode()+this.reference.hashCode();
            return hash;  
        }        
        
        @Override
        public boolean isSameState(Unit unit) {
            SCHSymbol shape=(SCHSymbol)unit.getShape(getUUID());            
            return (shape.getState(getMementoType()).equals(this));  
        }
    }    
}
