package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.CopperAreaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceTarget;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polygon;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PCBCopperArea extends CopperAreaShape implements PCBShape{
    
    private Polygon polygon;
    private Point floatingStartPoint; //***mid 90 degree forming
    private Point floatingEndPoint;
    private Point resizingPoint;   
    private int clearance;
    private String net;
    private PadShape.PadConnection padConnection;
    
    public PCBCopperArea(int layermaskId) {
        super(layermaskId);
        this.bendingPointDistance=Utilities.DISTANCE;
        this.displayName = "Copper Area";
        this.clearance=(int)Grid.MM_TO_COORD(0.2); 
        floatingStartPoint=new Point();
        floatingEndPoint=new Point();
        this.polygon = new Polygon(); 
        this.padConnection=PadShape.PadConnection.DIRECT;
        this.fill=Fill.FILLED;
    }
    public PCBCopperArea clone() throws CloneNotSupportedException{
        PCBCopperArea copy=(PCBCopperArea)super.clone();
        copy.floatingStartPoint = new Point();
        copy.floatingEndPoint = new Point();
        copy.resizingPoint=null;
        copy.polygon=this.polygon.clone();  
        return copy;        
    }
    @Override
    public int getDrawingLayerPriority() {
        if(getOwningUnit()==null){            
            return super.getDrawingLayerPriority();
        }
        
        if(((CompositeLayerable)getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())){
           return 2;
        }else{
           return 1; 
        }
    }
    @Override
    public Box getBoundingShape() {        
        return this.polygon.box();
    }
    @Override
    public List<Point> getLinePoints() {
        return this.polygon.points;
    }

    @Override
    public void add(Point point) {
        this.polygon.points.add(point);
    }

    @Override
    public void add(double x, double y) {
        this.polygon.points.add(new Point(x,y));
    }
    
    @Override
    public void rotate(double angle, Point point) {        
        this.polygon.rotate(angle,point);
    }
    
    @Override
    public void insertPoint(double x, double y) {        

    }
    @Override
    public boolean isClicked(double x,double y) {               
                Point pt=new Point(x,y);

                Point prevPoint = this.polygon.points.get(this.polygon.points.size()-1);
                Line line=new Line(new Point(), new Point());
                for(Point point:this.polygon.points){
                    if(prevPoint.equals(point)){
                        prevPoint = point;
                        continue;
                    }

                    line.setLine(prevPoint, point);
                    Point projectionPoint = line.projectionPoint(pt);

                    if(projectionPoint.distanceTo(pt)>Utilities.DISTANCE){
                        prevPoint = point;
                        continue;
                    }
                    
                    double a = (projectionPoint.x - prevPoint.x) / ((point.x - prevPoint.x) == 0 ? 1 : point.x - prevPoint.x);
                    double b = (projectionPoint.y - prevPoint.y) / ((point.y - prevPoint.y) == 0 ? 1 : point.y - prevPoint.y);

                    if (0 <= a && a <= 1 && 0 <= b && b <= 1) { //is projection between start and end point                                                    
                            return true;
                    }
                    prevPoint = point;
                }
                
            return false;
    } 
    
//    private Point getBendingPointClicked(double x,double y){
//        Box rect = Box.fromRect(x
//                        - this.selectionRectWidth / 2, y - this.selectionRectWidth
//                        / 2, this.selectionRectWidth, this.selectionRectWidth);
//
//        
//        Optional<Point> opt= this.polygon.points.stream().filter(( wirePoint)->rect.contains(wirePoint)).findFirst();                                    
//        
//        return opt.orElse(null);
//    }
    @Override
    public  boolean isShapeDeletable() {
   	 return getLinePoints().size()==3; 
    }
    @Override
    public Point isControlRectClicked(double x, double y,ViewportWindow viewportWindow) {
        Point pt=new Point(x,y);
		pt.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		pt.move(-viewportWindow.getX(),- viewportWindow.getY());                    
                  
        for(Point p:this.polygon.points){
  		  var tmp=p.clone();
  		  tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
  		  tmp.move(-viewportWindow.getX(),- viewportWindow.getY()); 
          
  		  if(Utils.LE(pt.distanceTo(tmp),this.selectionRectWidth/2)){                                  
             return p;
          }
        }
        return null;         
    }

    @Override
    public void reset(Point point) {
        this.reset(point.x,point.y);  
    }

    @Override
    public void reset(double x, double y) {
        Point p=getBendingPointClicked(x, y,Utilities.DISTANCE);
        floatingStartPoint.set(p==null?x:p.x,p==null?y:p.y);
        floatingEndPoint.set(p==null?x:p.x,p==null?y:p.y);         
    }

    @Override
    public void reset() {
        this.reset(floatingStartPoint);
    }

    @Override
    public Point getFloatingStartPoint() {
        return floatingStartPoint;
    }

    @Override
    public Point getFloatingMidPoint() {    
        return null;
    }

    @Override
    public Point getFloatingEndPoint() {        
        return floatingEndPoint;
    }

    @Override
    public void shiftFloatingPoints() {
        floatingStartPoint.set(polygon.points.get(polygon.points.size()-1).x, polygon.points.get(polygon.points.size()-1).y);
    }

    @Override
    public void deleteLastPoint() {
        // TODO Implement this method
    }

//    @Override
//    public void removePoint(double x, double y,int distance) {
//        Point point=getBendingPointClicked(x, y,bendingPointDistance);
//        if(point!=null){
//          this.polygon.points.remove(point);
//          point = null;
//        } 
//
//    }

    @Override
    public boolean isEndPoint(double x, double y) {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean isFloating() {
        return (!this.floatingStartPoint.equals(this.floatingEndPoint));
    }
    @Override
    public boolean isInRect(Box r) {
        for(Point wirePoint:polygon.points){
            if (!r.contains(wirePoint))
                return false;            
        }
        return true;
        
    }
    @Override
    public void mirror(Line line) {
        this.polygon.mirror(line);
    }  
    @Override
    public void move(double xoffset, double yoffset) {
        this.polygon.move(xoffset,yoffset);        
    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layersmask) {
        if((layersmask&this.copper.getLayerMaskID())==0){        
             return;
        }

        g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:this.copper.getColor());   
        polygon.paint(g2, true);  
           
        //print clearence background
        Collection<ClearanceTarget> targets=getOwningUnit().getShapes(ClearanceTarget.class);
        for(ClearanceTarget target:targets){
            target.printClearance(g2,printContext, this);
        }             
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {        
        Box rect = this.polygon.box();
        rect.scale(scale.getScaleX());           
        if (!this.isFloating()&& (!rect.intersects(viewportWindow))) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());

        Polygon r=this.polygon.clone();   
        
        // draw floating point
        if (this.isFloating()) {
            Point p = this.floatingEndPoint.clone();                              
            r.points.add(p); 
        }
        
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        
        g2.setStroke(new BasicStroke());

        Composite originalComposite = g2.getComposite();
        AlphaComposite composite;
        if(((CompositeLayerable)this.getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())) {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);                                                        	  
        }else {
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);                                                        	           
        }
        g2.setComposite(composite );
        
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        
        if (this.isFloating()) {
          r.paint(g2, false);
        }else{
          r.paint(g2, true);  
        }
        g2.setComposite(originalComposite);
        if(this.fill==Fill.FILLED){
        //draw clearence background
         Collection<ClearanceTarget> targets=getOwningUnit().getShapes(ClearanceTarget.class);
         this.prepareClippingRegion(viewportWindow, scale);
         for(ClearanceTarget target:targets){              
              target.drawClearance(g2, viewportWindow, scale, this);
         }
        }
        
                
    }
    @Override
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        
        if (this.isSelected()) {
            Point pt=null;
            if(resizingPoint!=null){
                pt=resizingPoint.clone();
                pt.scale(scale.getScaleX());
                pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }
            Polygon r=this.polygon.clone();   
            r.scale(scale.getScaleX());
            r.move(-viewportWindow.getX(),- viewportWindow.getY());
            
            for(Object p:r.points){
              Utilities.drawCircle(g2,  pt,(Point)p); 
            }
        } 
    }
    /*
     * Local cache
     */
    private java.awt.Polygon clip=new java.awt.Polygon();
    
    @Override
    public java.awt.Polygon getClippingRegion() {        
        return clip;
    }

    @Override
    public void prepareClippingRegion(ViewportWindow viewportWindow, AffineTransform scale) {
        clip.reset();
        for(Point point:polygon.points){
            java.awt.Point position=new java.awt.Point();
            position.setLocation(point.x*scale.getScaleX(),point.y*scale.getScaleX());            
            clip.addPoint(position.x-(int)viewportWindow.getX(),position.y-(int)viewportWindow.getY());
        }
    }

    @Override
    public PadShape.PadConnection getPadConnection() {        
        return padConnection;
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
    public String getNetName(){
        return net;
    }
    @Override
    public void setNetName(String net){
        this.net=net;
    }
    public void setPadConnection(PadShape.PadConnection padConnection) {
        this.padConnection = padConnection;
    }

    @Override
    public Point getResizingPoint(){
        return resizingPoint;
    }
    
    @Override
    public void setResizingPoint(Point point) {
        this.resizingPoint=point;
    }

    @Override
    public void resize(double xoffset, double yoffset, Point point) {
        point.set(point.x + xoffset,point.y + yoffset);        
    }

    @Override
    public void alignResizingPointToGrid(Point pt) {
        getOwningUnit().getGrid().snapToGrid(pt);
    }

    @Override
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<copperarea layer=\""+this.copper.getName()+"\" clearance=\""+this.clearance+"\" net=\""+(this.net==null?"":this.net) +"\" padconnect=\""+this.padConnection+"\" >");
        for(Point point:polygon.points){
            sb.append(Utilities.roundDouble(point.x) + "," + Utilities.roundDouble(point.y) + ",");
        }        
        sb.append("</copperarea>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element  element= (Element)node;
        
        this.copper=Layer.Copper.valueOf(element.getAttribute("layer"));
        this.clearance=Integer.parseInt(element.getAttribute("clearance"));
        this.net=element.getAttribute("net").isEmpty()?null:element.getAttribute("net");         
        this.padConnection=element.getAttribute("padconnect").isEmpty()?PadShape.PadConnection.DIRECT:PadShape.PadConnection.valueOf(element.getAttribute("padconnect"));
        
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




    static class Memento extends AbstractMemento<Board,PCBCopperArea>{
        private double Ax[];
        private double Ay[];
        private int clearance;
        private String net;
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(PCBCopperArea shape) {
            super.loadStateTo(shape);
            shape.polygon.points.clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.add(new Point(Ax[i], Ay[i]));
            }
            //***reset floating start point
            if (shape.polygon.points.size() > 0) {
                shape.floatingStartPoint.set(shape.polygon.points.get(shape.polygon.points.size() - 1));
                shape.reset();
            }
            shape.net=net;
            shape.clearance=clearance;
        }

        @Override
        public void saveStateFrom(PCBCopperArea shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.polygon.points.size()];
            Ay = new double[shape.polygon.points.size()];
            for (int i = 0; i < shape.polygon.points.size(); i++) {
                Ax[i] = (shape.polygon.points.get(i)).x;
                Ay[i] = (shape.polygon.points.get(i)).y;
            }
            this.net=shape.net;
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
            Memento other = (Memento) obj;
            return (super.equals(obj)&&this.clearance==other.clearance&&Objects.equals(this.net, other.net)&&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode()+this.clearance+Objects.hashCode(this.net);
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }
        
        @Override
        public boolean isSameState(Unit unit) {
            PCBCopperArea polygon = (PCBCopperArea) unit.getShape(getUUID());
            return (polygon.getState(getMementoType()).equals(this));
        }
        
    }        
}
