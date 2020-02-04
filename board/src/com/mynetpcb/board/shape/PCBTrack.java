package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.TrackShape;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.d2.shapes.Rectangle;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PCBTrack extends TrackShape implements PCBShape{
    
    private int clearance;
    private String net;
    
    public PCBTrack(int thickness,int layermaskId){
        super(thickness,layermaskId);
        this.displayName="Track"; 
    }
    
    public PCBTrack clone()throws CloneNotSupportedException{
            PCBTrack copy = (PCBTrack) super.clone();
            copy.floatingStartPoint = new Point();
            copy.floatingMidPoint = new Point();
            copy.floatingEndPoint = new Point();
            copy.resizingPoint=null;
            copy.polyline=this.polyline.clone();
            return copy;        
    }

    @Override
    public Collection<Shape> getNetShapes(Collection<UUID> selected) {
        Collection<Shape> net=new ArrayList<>(); 
        //1. via
        Collection<PCBVia> vias=getOwningUnit().getShapes(PCBVia.class);         
        for(PCBVia via:vias ){
            if(selected.contains(via.getUUID())){
                continue;
            }
            if(this.polyline.intersect(via.getOuter())){
               net.add(via); 
            }
        }
        //2.track on same layer
        Collection<PCBTrack> tracks=getOwningUnit().getShapes(PCBTrack.class,this.copper.getLayerMaskID());         
        Circle circle=new Circle(new Point(),0);
        for(PCBTrack track:tracks ){
            if(track==this){
                continue;
            }
            if(selected.contains(track.getUUID())){
                continue;
            }
            //my points on another
            for(Point pt:this.polyline.points){
                circle.pc=pt;
                circle.r=this.getThickness()/2;
                if(track.polyline.intersect(circle)){
                   net.add(track);
                   break;
                }   
            }
            //another points on me
            for(Point pt:track.polyline.points){
                circle.pc=pt;
                circle.r=track.getThickness()/2;
                if(this.polyline.intersect(circle)){
                   net.add(track);
                   break;
                }   
            }            
            
        } 
        //3.Footprint pads on me
        Collection<PCBFootprint> footprints=getOwningUnit().getShapes(PCBFootprint.class);         
        //the other side
        tracks=getOwningUnit().getShapes(PCBTrack.class,Layer.Side.change(this.copper.getLayerMaskID()).getLayerMaskID());
        
        for(PCBFootprint footprint:footprints){
            Collection<PadShape> pads=footprint.getPads();
            for(PadShape pad:pads){
                if(pad.getType()!=PadShape.Type.THROUGH_HOLE){
                    continue;
                }                
                for(Point pt:this.polyline.points){
                    double distance=pt.distanceTo(pad.getCenter());
                    if(!Utils.GT(distance,this.getThickness())){  //this pad is a via 
                        //find tracks on pad
                        for(PCBTrack track:tracks ){  //each track on opposite layer
                         if(selected.contains(track.getUUID())){
                            continue;
                         }
                         //another points on me
                          for(Point p:track.polyline.points){
                             distance=p.distanceTo(pad.getCenter());
                             if(!Utils.GT(distance,this.getThickness())){  //this pad is a via 
                               net.add(track);
                               break;
                             }
                          }   
                        }                                                                       
                    }                                       
                }
            }
        }
        return net;
    }
    @Override
    public <T extends ClearanceSource> void drawClearance(Graphics2D g2, ViewportWindow viewportWindow,
                                                          AffineTransform scale, T source) {
        if(isSameNet(source)){
            return;
        }       
        
        Shape shape=(Shape)source;
        //no need to draw clearance if not on active side
        //if(((CompositeLayerable)getOwningUnit()).getActiveSide()!=Layer.Side.resolve(this.copper.getLayerMaskID())){
        //   return;
        //}        
        if((shape.getCopper().getLayerMaskID()&this.copper.getLayerMaskID())==0){        
             return;  //not on the same layer
        }
        if(!shape.getBoundingShape().intersects(this.getBoundingShape())){
           return; 
        }
                
        double lineThickness=(thickness+2*(this.clearance!=0?this.getClearance():source.getClearance())) *scale.getScaleX();            
        
        Polyline polyline=this.polyline.clone();   
        
        
        polyline.scale(scale.getScaleX());
        polyline.move(-viewportWindow.getX(),- viewportWindow.getY());

        g2.setStroke(new BasicStroke((float) lineThickness, 1, 1));

        g2.setColor(Color.BLACK);        
         
        g2.setClip(source.getClippingRegion());
        polyline.paint(g2, false);
        g2.setClip(null);


    }

    @Override
    public <T extends ClearanceSource> void printClearance(Graphics2D g2, PrintContext printContext,
                                                           T source) {
        if(isSameNet(source)){
            return;
        } 
        Shape shape=(Shape)source;
        if((shape.getCopper().getLayerMaskID()&this.copper.getLayerMaskID())==0){        
             return;  //not on the same layer
        } 
        if(!shape.getBoundingShape().intersects(this.getBoundingShape())){
           return; 
        }
        
        double lineThickness=(thickness+2*(this.clearance!=0?this.getClearance():source.getClearance()));                    
        g2.setStroke(new BasicStroke((float) lineThickness, 1, 1));
        g2.setColor(printContext.getBackgroundColor());       
         
        polyline.paint(g2, false);
        
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        
        Box rect = this.polyline.box();
        rect.scale(scale.getScaleX());           
        if (!this.isFloating()&& (!rect.intersects(viewportWindow))) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        
        Polyline r=this.polyline.clone();   
        
        // draw floating point
        if (this.isFloating()) {
            Point p = this.floatingMidPoint.clone();                              
            r.add(p); 
            
            p = this.floatingEndPoint.clone();
            r.add(p);                                     
        }
        
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        double wireWidth = thickness * scale.getScaleX();
        g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));

        //transparent rect
        r.paint(g2, false);
        
    }
    
    @Override
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        if (this.isSelected()&&isControlPointVisible) {
            Point pt=null;
            if(resizingPoint!=null){
                pt=resizingPoint.clone();
                pt.scale(scale.getScaleX());
                pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }
            Polyline r=this.polyline.clone();                                       
            r.scale(scale.getScaleX());
            r.move(-viewportWindow.getX(),- viewportWindow.getY());
            
            for(Object p:r.points){
              Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),(Point)p); 
            }
        }        
    }
    
    @Override
    public int getDrawingLayerPriority() {
          int order;  

         if(((CompositeLayerable)getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())){
           order= 4;
         }else{
           order= 3; 
         }  
        return order;     
    }
    @Override
    public void setClearance(int clearance) {
        this.clearance=clearance;
    }

    @Override
    public int getClearance() {    
        return clearance;
    }
    public String getNetName(){
        return net;
    }
    
    public void setNetName(String net){
        this.net=net;
    }
    @Override
    public boolean isSublineSelected() {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean isSublineInRect(Rectangle rectangle) {
        // TODO Implement this method
        return false;
    }

    @Override
    public void setSublineSelected(Rectangle rectangle, boolean b) {
        // TODO Implement this method

    }

    @Override
    public Set<LinePoint> getSublinePoints() {
        return Collections.emptySet();
    }

    @Override
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<track layer=\""+this.copper.getName()+"\" thickness=\""+this.getThickness()+"\" clearance=\""+clearance+"\" net=\""+(this.net==null?"":this.net)+"\" >");
        for(Point point:this.polyline.points){
            sb.append(Utilities.roundDouble(point.x) + "," + Utilities.roundDouble(point.y) + ",");
        }        
        sb.append("</track>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element  element= (Element)node;
        
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
        this.copper=Layer.Copper.valueOf(element.getAttribute("layer"));
        this.clearance=element.getAttribute("clearance").equals("")?0:Integer.parseInt(element.getAttribute("clearance"));
        this.net=element.getAttribute("net").isEmpty()?null:element.getAttribute("net");   
        StringTokenizer st = new StringTokenizer(element.getTextContent(), ",");
        while(st.hasMoreTokens()){
          this.add(new Point(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken())));  
        }   

    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    static class Memento extends AbstractMemento<Board, PCBTrack> {

        private double Ax[];

        private double Ay[];

        private int clearance;
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }
        
        @Override
        public void loadStateTo(PCBTrack shape) {
            super.loadStateTo(shape);
            shape.polyline.points.clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.add(new Point(Ax[i], Ay[i]));
            }
            //***reset floating start point
            if (shape.polyline.points.size() > 0) {
                shape.floatingStartPoint.set(shape.polyline.points.get(shape.polyline.points.size() - 1));
                shape.reset();
            }
            shape.clearance=clearance;
        }
        
        @Override
        public void saveStateFrom(PCBTrack shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.polyline.points.size()];
            Ay = new double[shape.polyline.points.size()];
            for (int i = 0; i < shape.polyline.points.size(); i++) {
                Ax[i] = (shape.polyline.points.get(i)).x;
                Ay[i] = (shape.polyline.points.get(i)).y;
            }
            this.clearance=shape.clearance;
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
            Memento other = (Memento)obj;
            
            return (super.equals(obj)&&this.clearance==other.clearance&&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode()+this.clearance;
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            PCBTrack line = (PCBTrack) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }    
}
