package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.board.unit.BoardMgr;
import com.mynetpcb.core.board.ClearanceSource;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Grid.Units;
import com.mynetpcb.core.capi.Pinable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.ChipText;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.shape.FootprintShapeFactory;
import com.mynetpcb.pad.shape.Pad;

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
import java.util.Objects;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PCBFootprint extends FootprintShape<Pad> implements PCBShape{
    
    private List<Shape> shapes;
    
    private ChipText text;
    
    private String footprintName;    
    
    private Grid.Units units;
    
    //mm/inch value
    private double value;
    
    private int clearance;
    
    public PCBFootprint(int layermask) {
        super(layermask);
        this.shapes=new ArrayList<Shape>();
        text = new ChipText();
        text.Add(new GlyphTexture("", "reference", 0, 0, Grid.MM_TO_COORD(1.2)));
        text.Add(new GlyphTexture("", "value", 8, 8, Grid.MM_TO_COORD(1.2)));      
        units=Grid.Units.MM;
        value=2.54;
    }
    
    public PCBFootprint clone() throws CloneNotSupportedException {
        PCBFootprint copy=(PCBFootprint)super.clone();
        copy.text =text.clone();
        copy.shapes=new ArrayList<Shape>();
        copy.units=this.units;
        copy.value=this.value;
        for(Shape shape:this.shapes){ 
          copy.Add(shape.clone());  
        }
        return copy;        
    }
    public void setSide(Layer.Side side){
        //mirror footprint
        Rectangle r=getBoundingShape().getBounds();
        Point p=new Point((int)r.getCenterX(),(int)r.getCenterY()); 
        Mirror(new Point(p.x,p.y-10),new Point(p.x,p.y+10));
        
        for(Shape shape:shapes){
            shape.setCopper(Layer.Side.change(shape.getCopper()));
        }
        Layer.Copper copper=Layer.Side.change(this.getCopper());
        //convert text layer
        for(Texture texture:text.getChildren()){          
          texture.setLayermaskId(Layer.Side.change(Layer.Copper.resolve(texture.getLayermaskId())).getLayerMaskID());                   
        }        
        this.setCopper(copper);
    }
    
    public Layer.Side getSide(){
        return Layer.Side.resolve(getCopper().getLayerMaskID());       
    }
    //footprint has a complex layering
    @Override
    public boolean isVisibleOnLayers(int layermasks){
        for(Shape shape:shapes){
            if(shape.isVisibleOnLayers(layermasks)){
              return true;
            }
        }               
        return false;
    }
    
    public Units getGridUnits(){
      return units;
    }
    public void setGridUnits(Units units){
      this.units=units;
    }    
    public double getGridValue(){
       return value; 
    }    
    public void setGridValue(double value){
       this.value=value; 
    }
    public void Add(Shape shape){
      if (shape == null)
            return;   
      shapes.add(shape);  
    }
    
    public List<Shape> getShapes(){
        return shapes;
    }
    
    @Override
    public ChipText getChipText() {
        return text;
    }
    
    @Override
    public void Clear() {    
       shapes.clear();
       text.clear();
    }
    
    public void setDisplayName(String footprintName){
        this.footprintName=footprintName; 
    }
    
    @Override
    public String getDisplayName() {
        return footprintName;
    }
    @Override
    public long getOrderWeight() {
        java.awt.Shape r=getBoundingShape();
        return (r.getBounds().width);
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
    @Override
    public void Rotate(AffineTransform rotation) {
        for(Shape shape:shapes)
           shape.Rotate(rotation);        
        //***Rotate text
        text.Rotate(rotation);            
    }
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {        
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds() ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        
        g2.setColor(isSelected()?Color.GRAY:fillColor); 
        for(Shape shape:shapes){
            shape.Paint(g2,viewportWindow, scale,layermask);   
        }
        //HACK!!!could not figure out better approach
        for(Texture texture:text.getChildren()){
         if((texture.getLayermaskId()&layermask)!=0){            
          texture.setFillColor((isSelected()?Color.GRAY:Layer.Copper.resolve(texture.getLayermaskId()).getColor()));
          texture.Paint(g2,viewportWindow,scale,texture.getLayermaskId());
         }
        }
        if(this.isSelected()){                
            AlphaComposite composite =
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            Composite originalComposite = g2.getComposite();
            g2.setPaint(Color.GRAY);
            g2.setComposite(composite);
            RoundRectangle2D selectionRect=new RoundRectangle2D.Double(scaledRect.getX()-viewportWindow.x,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getHeight(), 10, 10);
            g2.fill(selectionRect);
            g2.setComposite(originalComposite); 
        }
    }
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermaskId) {
        for(Shape shape:shapes){
          if((shape.getCopper().getLayerMaskID()&layermaskId)!=0)
            shape.Print(g2,printContext,layermaskId);
        }
        //HACK!!!could not figure out better approach
        for(Texture texture:text.getChildren()){
         if((texture.getLayermaskId()&layermaskId)!=0){            
          texture.setFillColor((printContext.isBlackAndWhite()?Color.BLACK:Layer.Copper.resolve(texture.getLayermaskId()).getColor()));
          texture.Print(g2,printContext,layermaskId);
         }
        }
    }

    @Override
    public <T extends PCBShape & ClearanceSource> void printClearence(Graphics2D g2,PrintContext printContext, T source) {
        Shape shape=(Shape)source;
        Rectangle2D targetRect=getBoundingShape().getBounds();
        
        if(!shape.getBoundingShape().intersects(targetRect)){
           return; 
        }
        
        
        for(Shape pad:shapes){
            if(pad instanceof Pad){
                //if(Objects.equals(source.getNetName(), ((Pad)pad).getNetName())&&(!("".equals(source.getNetName())))&&(!(null==source.getNetName()))){
                //    continue;
                //}
                ((Pad)pad).printClearance(g2,printContext,source);
            }
        }               
    }
    
    @Override
    public Collection<Pad> getPins() {
        List<Pad> pins=new LinkedList<Pad>();
        for(Shape shape:shapes)
            if(shape instanceof Pad)
              pins.add((Pad)shape);  
        return pins;
    }

    @Override
    public Pad getPin(int x, int y) {
        for(Pad pin:getPins()){            
            if(pin.isClicked(x, y)){
              return pin;  
            }
        }             
        return null; 
    }

    @Override
    public Rectangle getPinsRect() {
        int x1=Integer.MAX_VALUE,y1=Integer.MAX_VALUE,x2=Integer.MIN_VALUE,y2=Integer.MIN_VALUE;
        boolean isPinnable=false;        
        
        for(Shape shape:shapes) 
          if(shape instanceof Pinable){
              Pinable element=(Pinable)shape;
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
    public String toXML() {
        StringBuffer xml=new StringBuffer();
               xml.append("<footprint layer=\""+this.copper.getName()+"\">\r\n");
               xml.append("<name>"+footprintName+"</name>\r\n");
               xml.append("<units raster=\""+this.getGridValue()+"\">"+this.getGridUnits()+"</units>\r\n"); 
               xml.append("<reference layer=\""+Layer.Copper.resolve(text.getTextureByTag("reference").getLayermaskId()).getName()+"\"  >"+(text.getTextureByTag("reference")==null?"":text.getTextureByTag("reference").toXML())+"</reference>\r\n");                           
               xml.append("<value layer=\""+Layer.Copper.resolve(text.getTextureByTag("value").getLayermaskId()).getName()+"\">"+(text.getTextureByTag("value")==null?"":text.getTextureByTag("value").toXML())+"</value>\r\n");
        //***labels and connectors
               BoardMgr boardMgr = BoardMgr.getInstance();
               if(boardMgr.getChildrenByParent(getOwningUnit().getShapes(),this).size()>0){
                 xml.append("<children>\r\n");
                   Collection<Shape> children=boardMgr.getChildrenByParent(getOwningUnit().getShapes(),this);
                    for(Shape child:children){
                      xml.append(((Externalizable)child).toXML());  
                    }
                    xml.append("</children>\r\n");
        }               
                  
               xml.append("<shapes>\r\n");
               for(Shape e:shapes){
                 xml.append(((Externalizable)e).toXML());
               }
               xml.append("</shapes>\r\n");
               xml.append("</footprint>\r\n");                 
        return xml.toString();  
    }

    @Override
    public void fromXML(Node node) {
        Element  element= (Element)node;
        this.copper=Layer.Copper.valueOf(element.getAttribute("layer"));
        
        Texture reference= text.getTextureByTag("reference");
        Texture unit= text.getTextureByTag("value");
        
        Node n=element.getElementsByTagName("units").item(0);
        this.setGridUnits(Grid.Units.MM);
        if(n!=null){
           this.value=Double.parseDouble(((Element)n).getAttribute("raster"));
        }else{
           this.value=0.8; 
        }
        n=element.getElementsByTagName("name").item(0);
        
        if(n!=null){
         this.footprintName=n.getTextContent();  
        }       
        
        n=element.getElementsByTagName("reference").item(0);
        if(n!=null){            
           reference.fromXML(n);
        }else{
           reference.Move(this.getBoundingShape().getBounds().x,this.getBoundingShape().getBounds().y);
        }    

        n=element.getElementsByTagName("value").item(0);
        if(n!=null){
           unit.fromXML(n);  
        }else{
           unit.Move(this.getBoundingShape().getBounds().x,this.getBoundingShape().getBounds().y);       
        }    
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try{
        NodeList nodelist = (NodeList) xpath.evaluate("./shapes/*", node, XPathConstants.NODESET);
        AbstractShapeFactory shapeFactory=new FootprintShapeFactory();
        for(int i=0;i<nodelist.getLength();i++){
              n=nodelist.item(i);
              Shape shape = shapeFactory.createShape(n);
              this.Add(shape);
        }       
        }catch(XPathExpressionException e){
            e.printStackTrace(System.out);
        }   
    }

    /*
     * Investigate if footprint intersects copper layer
     */
    @Override
    public <T extends PCBShape & ClearanceSource> void drawClearence(Graphics2D g2,
                                                                     ViewportWindow viewportWindow,
                                                                     AffineTransform scale, T source) {
        
        Shape shape=(Shape)source;
        Rectangle2D targetRect=getBoundingShape().getBounds();
 
        if(!shape.getBoundingShape().intersects(targetRect)){
           return; 
        }
        
        Rectangle2D scaledRect = Utilities.getScaleRect(targetRect ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        
        for(Shape pad:shapes){
            if(pad instanceof Pad){                                
                ((Pad)pad).drawClearance(g2,viewportWindow,scale,source);
            }
        }
        
    }
    
    @Override
    public void setClearance(int clearance) {
        this.clearance=clearance;
    }

    @Override
    public int getClearance() {
 
        return clearance;
    }

    static class Memento extends AbstractMemento<Board,PCBFootprint>{
        private final List<AbstractMemento> mementoList;
        
        private ChipText.Memento text;
        
        private String footprintName;
        
        private double value;
        
        private Units units;
        
        public Memento(MementoType operationType){
           super(operationType);
           mementoList=new LinkedList<AbstractMemento>();
           text=new ChipText.Memento();
        }
        
        public void loadStateTo(PCBFootprint symbol) {
          super.loadStateTo(symbol);
          text.loadStateTo(symbol.text); 
          symbol.footprintName=this.footprintName;
          symbol.units=this.units;
          symbol.value=this.value;
          /*
           * Symbol is recreated with empty shapes in it
           */
          if(symbol.shapes.size()==0){
            //***fill elements
            AbstractShapeFactory shapeFactory=new FootprintShapeFactory();
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
        
        public void saveStateFrom(PCBFootprint symbol) {
            super.saveStateFrom(symbol);
            this.text.saveStateFrom(symbol.getChipText());
            for(Shape ashape:symbol.shapes){
                mementoList.add(ashape.getState(mementoType));     
            }            
            this.footprintName=symbol.footprintName;
            this.units=symbol.units;
            this.value=symbol.value;
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
                   footprintName.equals(other.footprintName)&&
                   layerindex==other.layerindex&&
                   units==other.units&&
                   Double.compare(value, other.value)==0&&
                   text.equals(other.text)
                );
                      
        }

        
        @Override
        public int hashCode(){
            int hash=getUUID().hashCode(); 
            hash+=this.getMementoType().hashCode();
            hash+=this.mementoList.hashCode();
            hash+=this.footprintName.hashCode();
            hash+=this.layerindex;
            hash+=this.units.hashCode();
            hash+=new Double(this.value).hashCode();
            hash+=this.text.hashCode();
            return hash;  
        }              
        
        public boolean isSameState(Board unit) {
            PCBFootprint shape=(PCBFootprint)unit.getShape(getUUID());            
            if(shape==null){
                throw new IllegalStateException("shape uuid="+getUUID()+" does not exist");
            }
            return (shape.getState(getMementoType()).equals(this));  
        }

    }
}
