package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.gerber.Fillable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polygon;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.d2.shapes.RoundRectangle;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SolidRegion extends Shape implements Resizeable,Fillable, Trackable<Point>,Externalizable{
    private Polygon polygon;
    private Point floatingStartPoint; //***mid 90 degree forming
    private Point floatingEndPoint;
    private Point resizingPoint;    
    
    public SolidRegion(int layermaskId) {        
        super(0,layermaskId);
        this.bendingPointDistance=Utilities.DISTANCE;
        this.displayName = "Solid Region";
        this.floatingStartPoint=new Point();
        this.floatingEndPoint=new Point();                 
        this.polygon=new Polygon();

    }
    @Override    
    public SolidRegion clone()throws CloneNotSupportedException{
          SolidRegion copy=(SolidRegion)super.clone();
          copy.polygon=this.polygon.clone();
          copy.floatingStartPoint=new Point();
          copy.floatingEndPoint=new Point();
          return copy;
    }
    @Override
    public void clear() {        
        polygon.points.clear();
    }
    public long getOrderWeight(){
       return (long)this.polygon.area(); 
    }
    public Box getBoundingShape() {
        return this.polygon.box();        
    }
    
    public void add(Point point) {
        this.polygon.points.add(point);
    }
    @Override
    public void add(double x, double y) {
        this.add(new Point(x,y));
    }
    
    @Override
    public boolean isFloating() {
       return (!this.floatingStartPoint.equals(this.floatingEndPoint));                
    }
    @Override
    public boolean isClicked(double x,double y){
       return this.polygon.contains(x,y);
    }
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
    public Point getResizingPoint() {    
        return resizingPoint;
    }

    @Override
    public void setResizingPoint(Point point) {
        this.resizingPoint=point;
    }

    @Override
    public void resize(double xoffset, double yoffset, Point clickedPoint) {
        clickedPoint.set(clickedPoint.x + xoffset,
                                                                clickedPoint.y + yoffset);
    }

    @Override
    public void alignResizingPointToGrid(Point point) {
        getOwningUnit().getGrid().snapToGrid(point); 
    }
    @Override
    public void move(double xoffset, double yoffset) {
        this.polygon.move(xoffset,yoffset);        
    }
    @Override
    public void setSide(Layer.Side side, com.mynetpcb.d2.shapes.Line line,double angle) {
        this.setCopper(Layer.Side.change(this.getCopper().getLayerMaskID()));
        this.mirror(line);
        this.rotate=angle;
    }    
    @Override
    public void mirror(Line line) {
            this.polygon.mirror(line);
    }
    @Override
    public void rotate(double angle, Point origin) {
                //fix angle
                double alpha=this.rotate+angle;
                if(alpha>=360){
                   alpha-=360;
                }
                if(alpha<0){
                   alpha+=360; 
                }       
                this.rotate=alpha;
                this.polygon.rotate(angle,origin);
    }
    @Override
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {                               
            Point pt=null;
            if(resizingPoint!=null){
                pt=resizingPoint.clone();
                pt.scale(scale.getScaleX());
                pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }
           
            Polygon r=this.polygon.clone();   
            r.scale(scale.getScaleX());
            r.move(-viewportWindow.getX(),- viewportWindow.getY());
            for(var p:r.points){
              Utilities.drawCircle(g2,  pt,(Point)p); 
            }                                               
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
        if((this.copper.getLayerMaskID()&layersmask)==0){
            return;
        }
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
    
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
        Composite originalComposite = g2.getComposite();                     
        g2.setComposite(composite ); 
        
        g2.setStroke(new BasicStroke());

        //transparent rect
        if (this.isFloating()) {
          r.paint(g2, false);
        }else{
          r.paint(g2, true);  
        }
        
        g2.setComposite(originalComposite);
                
    }
    @Override
    public void print(Graphics2D g2,PrintContext printContext,int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }

        g2.setStroke(new BasicStroke(thickness,1,1));    
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());        
        
        this.polygon.paint(g2, true);      

    }
    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<solidregion copper=\"" + getCopper().getName() + "\">");
        for (Point point : this.polygon.points) {
            sb.append(Utilities.roundDouble(point.x) + "," + Utilities.roundDouble(point.y) + ",");
        }
        sb.append("</solidregion>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element = (Element) node;
        this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));
        StringTokenizer st = new StringTokenizer(element.getTextContent(), ",");

        while (st.hasMoreTokens()) {
           this.add(new Point(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())));
        }

    }


    @Override
    public List<Point> getLinePoints() {
        return polygon.points;
    }

    @Override
    public void insertPoint(double x, double y) {
        // TODO Implement this method

    }


//    protected Point getBendingPointClicked(double x,double y){
//        Box rect = Box.fromRect(x
//                        - Utilities.DISTANCE / 2, y - Utilities.DISTANCE
//                        / 2, Utilities.DISTANCE, Utilities.DISTANCE);
//
//        
//        Optional<Point> opt= this.polygon.points.stream().filter(( wirePoint)->rect.contains(wirePoint)).findFirst();                  
//                  
//        
//        return opt.orElse(null);
//    }

    @Override
    public void reset(Point p) {
        this.floatingStartPoint.set(p.x,p.y);
        this.floatingEndPoint.set(p.x,p.y); 
    }

    @Override
    public void reset(double x, double y) {
        this.floatingStartPoint.set(x,y);
        this.floatingEndPoint.set(x,y); 
    }

    @Override
    public void reset() {
        this.reset(this.floatingStartPoint);     
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
        // TODO Implement this method
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
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    public static class Memento extends AbstractMemento<Footprint, SolidRegion> {
        private double Ax[];
        private double Ay[];
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(SolidRegion shape) {
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
        }

        @Override
        public void saveStateFrom(SolidRegion shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.polygon.points.size()];
            Ay = new double[shape.polygon.points.size()];
            for (int i = 0; i < shape.polygon.points.size(); i++) {
                Ax[i] = (shape.polygon.points.get(i)).x;
                Ay[i] = (shape.polygon.points.get(i)).y;
            }
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
            return (super.equals(obj)&&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode();
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            SolidRegion polygon = (SolidRegion) unit.getShape(getUUID());
            return (polygon.getState(getMementoType()).equals(this));
        }
    }     
}
