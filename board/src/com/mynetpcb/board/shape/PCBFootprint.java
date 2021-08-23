package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.pin.Pinable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractLine;
import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polygon;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.pad.shape.FootprintShapeFactory;
import com.mynetpcb.pad.shape.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PCBFootprint extends FootprintShape implements PCBShape{
    private List<Shape> shapes;
    
    private GlyphTexture reference,value; 
    
    //private String footprintName;    
    
    private Grid.Units units;
    
    //mm/inch value
    private double val;
    
    private int clearance;
    
    public PCBFootprint(int layermask) {
        super(layermask);
        this.shapes=new ArrayList<Shape>();
        reference=new GlyphTexture("", "reference", 0, 0, (int)Grid.MM_TO_COORD(1.2));
        value=new GlyphTexture("", "value", 8, 8, (int)Grid.MM_TO_COORD(1.2));
        
        units=Grid.Units.MM;
        val=2.54;
    }
    @Override
    public PCBFootprint clone() throws CloneNotSupportedException {
        PCBFootprint copy=(PCBFootprint)super.clone();
        copy.reference =reference.clone();
        copy.value=value.clone();
        copy.shapes=new ArrayList<Shape>();
        copy.units=this.units;
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
         this.value.clear();
         this.reference.clear();
         this.rotate=0;
    }
    public void add(Shape shape){
      if (shape == null)
            return;   
      shape.setControlPointVisibility(false);
      shapes.add(shape);  
    }
    
    @Override
    public Collection<PadShape> getPads(){
       return shapes.stream().filter(s->s instanceof PadShape).map(s->(Pad)s).collect(Collectors.toList());        
    }
    
    @Override
    public Collection<? extends Shape> getShapes() {
        return this.shapes;
    }    
    public Grid.Units getGridUnits(){
      return units;
    }
    public void setGridUnits(Grid.Units units){
      this.units=units;
    }     

    public void setSide(Layer.Side side){
        //mirror footprint
        Box r=getBoundingShape();
        Point p=r.getCenter();
        Line line= new Line(p.x,p.y-10,p.x,p.y+10);
        
        for(Shape shape:shapes){
            shape.setSide(side,line,(360-this.rotate));
        }  
        this.reference.setSide(side,line,(360-this.rotate));       
        this.value.setSide(side,line,(360-this.rotate));       
        
        this.setCopper(Layer.Side.change(this.getCopper().getLayerMaskID()));
        this.rotate=360-this.rotate;
    }
    
    public Layer.Side getSide(){
        return Layer.Side.resolve(getCopper().getLayerMaskID());       
    }
    @Override
    public void setSelected (boolean selection) {
            super.setSelected(selection);
            this.shapes.forEach(shape->{   
                      shape.setSelected(selection);                             
            });  
            this.value.setSelected(selection);
            this.reference.setSelected(selection);
    }
    @Override
    public Box getBoundingShape() {

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
    public boolean isVisibleOnLayers(int layermasks){
        for(Shape shape:this.shapes){
           if(shape.isVisibleOnLayers(layermasks))
             return true;
        }
        return false;
    }  
    @Override
    public boolean isClicked(double x, double y, int layermasks) {
        for(Shape shape:this.shapes){
            if(shape.isVisibleOnLayers(layermasks)){
                if(shape.isClicked(x, y))
                  return true;
            }             
        }
        return false;   
    }
    @Override
    public boolean isClicked(double x, double y) {
        Box r=this.getBoundingShape();
        if(!r.contains(x,y)){
             return false;
        }
        Polygon ps=new Polygon();
        boolean result=false;

        for(Shape shape:this.shapes){
           if(!(shape instanceof AbstractLine)){ 
               if(shape.isClicked(x,y)){
                  result=true; 
                  break;
               }
           }else{
                ps.points.addAll(((AbstractLine)shape).getLinePoints());  //line vertices                   
           }
        };             
        if(result){
            return true;//click on a anything but a Line
        }
        
        this.sortPolygon(ps.points);  //line only
        return ps.contains(x,y);
    }
    
    private Point getPolygonCentroid(Collection<Point> points){
        double x=0,y=0;
        for(Point p:points){
                x+=p.x;
                y+=p.y;
        };
        return new Point(x/points.size(),y/points.size());
    }
    
    private void  sortPolygon(List<Point> points){
        Point center=this.getPolygonCentroid(points);
        
    
        points.sort((a,b)->{
         double a1=(Utils.degrees(Math.atan2(a.x-center.x,a.y-center.y))+360)%360;
         double a2=(Utils.degrees(Math.atan2(b.x-center.x,b.y-center.y))+360)%360;
         return ((int)a1-(int)a2);
        });
    }
    @Override
    public long getClickableOrder(){
        return (long)getBoundingShape().area();
    }
    @Override
    public int getDrawingLayerPriority() {        
        if(((CompositeLayerable)getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID()))
           return 100;
        else
           return 99;
    }
    @Override
    public Point getCenter() {        
        return this.getBoundingShape().getCenter();
    }
    @Override
    public void mirror(Line line) {
        for(Shape shape:shapes){
            shape.mirror(line);       
        }
        value.mirror(line);
        reference.mirror(line);  
    }
    
    @Override
    public void move(double xoffset, double yoffset) {
        for(Shape shape:shapes){
            shape.move(xoffset,yoffset);
        }        
        //***move module text
         value.move(xoffset,yoffset);
         reference.move(xoffset,yoffset);
    }
    
    @Override
    public void setRotation(double angle,Point center){ 
            angle=Math.abs(angle);
            double alpha=angle-this.rotate;
            for(Shape shape:this.shapes){                    
              shape.rotate(alpha,center);  
            }       
            this.value.rotate(alpha,center);
            this.reference.rotate(alpha,center);
        
            this.rotate=angle;
    }
    
    @Override
    public void rotate(double angle, Point center) {            
    //fix angle
       double alpha=this.rotate+angle;
       if(alpha>=360){
             alpha-=360;
       }
       if(alpha<0){
             alpha+=360; 
       }

        for(Shape shape:this.shapes){                    
           shape.rotate(angle,center);  
        }
        this.value.rotate(angle,center);
        this.reference.rotate(angle,center);
        
        this.rotate=alpha;
    }

    public double getGridValue(){
       return val; 
    }    
    public void setGridValue(double val){
       this.val=val; 
    }    
    @Override
    public <T extends ClearanceSource> void drawClearance(Graphics2D g2, ViewportWindow viewportWindow,
                                                          AffineTransform scale, T source) {
        Shape shape=(Shape)source;
        Box rect=shape.getBoundingShape();
        if(!rect.intersects(this.getBoundingShape())){
           return; 
        }
        
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
         return;
        }
     
        for(Shape pad:shapes){
            if(pad instanceof Pad){                                
                ((Pad)pad).drawClearance(g2,viewportWindow,scale,source);
            }
        }        

    }

    @Override
    public <T extends ClearanceSource> void printClearance(Graphics2D g2, PrintContext printContext,
                                                           T source) {
        Shape shape=(Shape)source;
        Box rect=getBoundingShape();
        
        if(!shape.getBoundingShape().intersects(rect)){
           return; 
        }
        
        
        for(Shape s:shapes){
            if(s instanceof ClearanceTarget){
                ((ClearanceTarget)s).printClearance(g2,printContext,source);
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

    @Override
    public Collection<Point> getPinPoints() {
        return Collections.emptySet();
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
              x2=Math.max(x2,p.x);
              y2=Math.max(y2,p.y);             
              isPinnable=true;
            }
        }
        r.setRect(x1, y1, x2 - x1, y2 - y1);
        return r;        
    }
    @Override
    public String toXML() {
        StringBuffer xml=new StringBuffer();
               xml.append("<footprint layer=\""+this.copper.getName()+"\" rt=\"" + this.rotate + "\">\r\n");
               xml.append("<name>"+displayName+"</name>\r\n");
               xml.append("<units raster=\""+this.getGridValue()+"\">"+this.getGridUnits()+"</units>\r\n"); 
               xml.append("<reference layer=\""+Layer.Copper.resolve(reference.getLayermaskId()).getName()+"\"  >"+(reference.toXML())+"</reference>\r\n");                           
               xml.append("<value layer=\""+Layer.Copper.resolve(value.getLayermaskId()).getName()+"\">"+(value.toXML())+"</value>\r\n");              
                  
               xml.append("<shapes>\r\n");
               for(Shape e:shapes){
                 xml.append(((Externalizable)e).toXML());
               }
               xml.append("</shapes>\r\n");
               xml.append("</footprint>\r\n");                 
        return xml.toString(); 
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element  element= (Element)node;
        this.copper=Layer.Copper.valueOf(element.getAttribute("layer"));
        
        if(element.getAttribute("rt").length()>0){
          this.rotate=Double.parseDouble(element.getAttribute("rt"));
        }
        
        Node n=element.getElementsByTagName("units").item(0);
        this.setGridUnits(Grid.Units.MM);
        if(n!=null){
           this.val=Double.parseDouble(((Element)n).getAttribute("raster"));
        }else{
           this.val=0.8; 
        }
        n=element.getElementsByTagName("name").item(0);
        
        if(n!=null){
          this.displayName=n.getTextContent();  
        }       
        
        n=element.getElementsByTagName("reference").item(0);           
        reference.fromXML(n);
       

        n=element.getElementsByTagName("value").item(0);
        value.fromXML(n);  
  
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try{
        NodeList nodelist = (NodeList) xpath.evaluate("./shapes/*", node, XPathConstants.NODESET);
        AbstractShapeFactory shapeFactory=new FootprintShapeFactory();
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
        
        if((value.getLayermaskId()&layersmask)!=0) {
        	value.setFillColor(Layer.Copper.resolve(value.getLayermaskId()).getColor());
        	value.paint(g2, viewportWindow, scale, layersmask);
        }
        if((reference.getLayermaskId()&layersmask)!=0) {
        	reference.setFillColor(Layer.Copper.resolve(reference.getLayermaskId()).getColor());
        	reference.paint(g2, viewportWindow, scale, layersmask);
        }
    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        for(Shape shape:shapes){
          if((shape.getCopper().getLayerMaskID()&layermask)!=0)
            shape.print(g2,printContext,layermask);
        }

         if((value.getLayermaskId()&layermask)!=0){            
          value.setFillColor((printContext.isBlackAndWhite()?Color.BLACK:Layer.Copper.resolve(value.getLayermaskId()).getColor()));
          value.print(g2,printContext,layermask);
         }
         if((reference.getLayermaskId()&layermask)!=0){            
             reference.setFillColor((printContext.isBlackAndWhite()?Color.BLACK:Layer.Copper.resolve(value.getLayermaskId()).getColor()));
             reference.print(g2,printContext,layermask);
         }         
         
             
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    @Override
    public Texture getTextureByTag(String tag) {
        if(tag.equals("value")){
          return value;  
        }else if(tag.equals("reference")){
          return reference;
        }else
          return null;
    }
    @Override
    public Texture getClickedTexture(double x, double y) {
        if(this.reference.isClicked(x, y))
            return this.reference;
        else if(this.value.isClicked(x, y))
            return this.value;
        else
        return null;
    }

    @Override
    public boolean isClickedTexture(double x, double y) {
        return this.getClickedTexture(x, y)!=null;
    }


    static class Memento extends AbstractMemento<Board,PCBFootprint>{
        
        private final List<AbstractMemento> mementoList;
        
        private GlyphTexture.Memento value,reference;
        
        private double val;
        
        
        
        public Memento(MementoType operationType){
           super(operationType);
           mementoList=new LinkedList<AbstractMemento>();
           value=new GlyphTexture.Memento();
           reference=new GlyphTexture.Memento();              
        }
        
        public void loadStateTo(PCBFootprint symbol) {
          super.loadStateTo(symbol);
          value.loadStateTo(symbol.value); 
          reference.loadStateTo(symbol.reference);        
          symbol.val=this.val;
          /*
           * Symbol is recreated with empty shapes in it
           */
          if(symbol.shapes.size()==0){
            //***fill elements
            AbstractShapeFactory shapeFactory=new FootprintShapeFactory();
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
        
        public void saveStateFrom(PCBFootprint symbol) {
            super.saveStateFrom(symbol);
            this.value.saveStateFrom(symbol.value);
            this.reference.saveStateFrom(symbol.reference);
            
            for(Shape ashape:symbol.shapes){
                mementoList.add(ashape.getState(mementoType));     
            }            
                        
            this.val=symbol.val;
        }
        @Override
        public void clear() {
            super.clear();
            for(AbstractMemento memento:mementoList){
              memento.clear();  
            }
            mementoList.clear();
            value.clear();
            reference.clear();
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
            
    
            return  super.equals(obj)&&Double.compare(val, other.val)==0&&                
                   mementoList.equals(other.mementoList)&&
                   reference.equals(other.reference)&&
                   value.equals(other.value);         
        }

        
        @Override
        public int hashCode(){
            int hash=super.hashCode(); 
            hash+=this.mementoList.hashCode();
            hash+=Double.hashCode(this.val);
            hash+=this.value.hashCode()+this.reference.hashCode();
            return hash;  
        }        
        
        @Override
        public boolean isSameState(Unit unit) {
            PCBFootprint shape=(PCBFootprint)unit.getShape(getUUID());            
            if(shape==null){
                throw new IllegalStateException("shape uuid="+getUUID()+" does not exist");
            }
            return (shape.getState(getMementoType()).equals(this));  
        }
    }
}
