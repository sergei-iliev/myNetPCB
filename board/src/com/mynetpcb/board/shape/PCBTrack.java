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
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.d2.shapes.Rectangle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

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
    public <T extends ClearanceSource> void drawClearance(Graphics2D g2, ViewportWindow viewportWindow,
                                                          AffineTransform scale, T source) {
//        if(Utilities.isSameNet(source, this)){
//            return;
//        }       
        
        Shape shape=(Shape)source;
        //no need to draw clearance if not on active side
        if(((CompositeLayerable)getOwningUnit()).getActiveSide()!=Layer.Side.resolve(this.copper.getLayerMaskID())){
           return;
        }        
        if((shape.getCopper().getLayerMaskID()&this.copper.getLayerMaskID())==0){        
             return;  //not on the same layer
        }
        if(!shape.getBoundingShape().intersects(this.getBoundingShape())){
           return; 
        }
                
        double lineThickness=(thickness+2*(this.clearance!=0?this.getClearance():source.getClearance())) *scale.getScaleX();            
        
        Polyline r=this.polyline.clone();   
        
        
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());

        g2.setStroke(new BasicStroke((float) lineThickness, 1, 1));

        g2.setColor(Color.BLACK);        
         
        g2.setClip(source.getClippingRegion());
        r.paint(g2, false);
        g2.setClip(null);


    }

    @Override
    public <T extends ClearanceSource> void printClearance(Graphics2D graphics2D, PrintContext printContext,
                                                           T clearanceSource) {
       

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
            Point p = this.floatingEndPoint.clone();                              
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
        //this.net=element.getAttribute("net").isEmpty()?null:element.getAttribute("net");   
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
