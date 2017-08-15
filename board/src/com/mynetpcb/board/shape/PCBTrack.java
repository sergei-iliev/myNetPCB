package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.ClearanceSource;
import com.mynetpcb.core.board.CompositeLayerable;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.TrackShape;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PCBTrack extends TrackShape implements PCBShape{
    
    public  Point floatingStartPoint; //***the last wire point

    public  Point floatingMidPoint; //***mid 90 degree forming

    public  Point floatingEndPoint;
    
    private  List<LinePoint> points;

    private Point resizingPoint;
    
    private int clearance;
    
    private String net;
    
    public PCBTrack(int thickness,int layermaskId){
        super(thickness,layermaskId);
        this.points=new LinkedList<LinePoint>();    
        this.fillColor=Color.BLUE;
        this.floatingStartPoint = new Point();
        this.floatingMidPoint = new Point();
        this.floatingEndPoint = new Point();
        this.selectionRectWidth=3000;
        this.clearance=0;
    }
    
    public PCBTrack clone()throws CloneNotSupportedException{
        PCBTrack copy=(PCBTrack)super.clone();
        copy.floatingStartPoint = new Point();
        copy.floatingMidPoint = new Point();
        copy.floatingEndPoint = new Point();
        copy.points=new LinkedList<LinePoint>(); 
        for(Point point:points){
            copy.points.add(new LinePoint(point.x,point.y));
        }
        return copy;
    }
    
    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        getOwningUnit().getGrid().snapToGrid(targetPoint); 
    }

    @Override
    public int getDrawingOrder() {
        int order=super.getDrawingOrder();
        if(getOwningUnit()==null){            
            return order;
        }
        
        
         if(((CompositeLayerable)getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())){
           order= 4;
         }else{
           order= 3; 
         }  
        return order;     
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {

        if((layersmask&this.copper.getLayerMaskID())==0){
             return;
        }     
        
        //FIX caching for the lines isCasheEnabled=false;
        Rectangle2D scaledBoundingRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);         
        if(!this.isFloating()&&!scaledBoundingRect.intersects(viewportWindow)){
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
  
        g2.setStroke(new BasicStroke((float)lineThickness,JoinType.JOIN_ROUND.ordinal(),EndType.CAP_ROUND.ordinal())); 
        
        g2.setColor(isSelected()?Color.GRAY:(this.copper.getColor()));
        
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
        Composite originalComposite = g2.getComposite();                     
        g2.setComposite(composite );
        g2.draw(temporal);                 
        g2.setComposite(originalComposite);
           
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
    }
    @Override
    public void drawControlShape(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale){   
        if((!this.isSelected())&&(!this.isSublineSelected())){
          return;
        }
        Utilities.drawCrosshair(g2, viewportWindow, scale, points, resizingPoint, selectionRectWidth);
    }
    @Override
    public <T extends PCBShape & ClearanceSource> void drawClearence(Graphics2D g2,
                                                                     ViewportWindow viewportWindow,
                                                                     AffineTransform scale, T source) {      
        if(Objects.equals(source.getNetName(), this.net)&&(!("".equals(source.getNetName())))&&(!(null==this.net))){
            return;
        }
        Shape shape=(Shape)source;
        if((shape.getCopper().getLayerMaskID()&this.copper.getLayerMaskID())==0){        
             return;  //not on the same layer
        }
        if(!shape.getBoundingShape().intersects(this.getBoundingShape().getBounds())){
           return; 
        }
        
        double lineThickness;
        if(this.clearance!=0){
          lineThickness=(thickness+2*this.getClearance()) *scale.getScaleX();            
        }else{
          lineThickness=(thickness+2*source.getClearance()) *scale.getScaleX();              
        }
        
        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
        GeneralPath temporal=(GeneralPath)provider.getShape();            
        
        temporal.moveTo(points.get(0).getX(),points.get(0).getY());
        for(int i=1;i<points.size();i++){            
              temporal.lineTo(points.get(i).getX(),points.get(i).getY());       
        } 
        
        AffineTransform translate= AffineTransform.getTranslateInstance(-viewportWindow.x,-viewportWindow.y);
        
        temporal.transform(scale);
        temporal.transform(translate); 

        g2.setStroke(new BasicStroke((float)lineThickness,JoinType.JOIN_ROUND.ordinal(),EndType.CAP_ROUND.ordinal())); 
        g2.setColor(Color.BLACK);        
         
        g2.setClip(source.getClippingRegion(viewportWindow,scale));
        g2.draw(temporal);         
        g2.setClip(null);
        
        provider.reset();
    }
    @Override
    public String getDisplayName() {
        return "Track";
    }

    @Override
    public List<LinePoint> getLinePoints() {
        return points;
    }

    @Override
    public void addPoint(Point point) {
      points.add(new LinePoint(point));
    }
    
    @Override
    public void add(int x, int y) {
        points.add(new LinePoint(x,y));
    }
    
    @Override
    public void insertPoint(int x, int y) {
        int count=-1,index=-1;
        //build testing rect
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(selectionRectWidth/2), y-(selectionRectWidth/2),selectionRectWidth, selectionRectWidth);
        //inspect line by line
        FlyweightProvider lineProvider=ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line=(Line2D)lineProvider.getShape();
        
        //***make lines and iterate one by one
        Point prevPoint = this.points.get(0);
        Iterator<LinePoint> i = points.iterator();
        while (i.hasNext()) {
            count++;
            Point nextPoint = i.next();
            line.setLine(prevPoint, nextPoint);
            if (line.intersects(rect)){
                index=count;
                break;
            }    
            prevPoint = nextPoint;
        }
        
        lineProvider.reset();
        rectProvider.reset();
        if(count!=-1){
           points.add(index, new LinePoint(x,y)); 
        }
         
    }
    
    @Override
    public Point isBendingPointClicked(int x,int y){
        return isControlRectClicked(x, y);
    }

    public Point isControlRectClicked(int x, int y) {                
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(thickness/2), y-(thickness/2),thickness, thickness);
        
        Point point=null;
        Point click=new Point(x,y);
        int distance=Integer.MAX_VALUE;
        
        for (Point wirePoint : points) {
            if(rect.contains(wirePoint)){ 
                int min=(int)click.distance(wirePoint);
                if(distance>min){
                    distance=min;  
                    point= wirePoint;                
                }
            }
        }
        
        rectProvider.reset();
        return point;
    }    
    @Override
    public void Clear() {
      points.clear();
    }
    
    @Override
    public void Move(int xoffset, int yoffset) {
        this.clearCache();
        for(Point wirePoint:points){
            wirePoint.setLocation(wirePoint.x + xoffset,
                                  wirePoint.y + yoffset);            
        } 
    }

    @Override
    public void Mirror(Point A,Point B) {
        this.clearCache();
        for (Point wirePoint : points) {
            wirePoint.setLocation(Utilities.mirrorPoint(A,B, wirePoint));
        }
    }
    @Override
    public void Rotate(AffineTransform rotation) {
        this.clearCache();
        for(Point wirePoint:points){
            rotation.transform(wirePoint, wirePoint);
        }
    }
    @Override
    public long getOrderWeight() {
        return 4;
    }
    
    @Override
    public boolean isInRect(Rectangle r) {
        for(Point wirePoint:points){
            if (!r.contains(wirePoint))
                return false;            
        }
        return true;
    }
    
    @Override
    public boolean isClicked(int x, int y) {
        boolean result=false;
        //build testing rect
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(thickness/2), y-(thickness/2),thickness, thickness);
        //inspect line by line
        FlyweightProvider lineProvider=ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line=(Line2D)lineProvider.getShape();
        
        //***make lines and iterate one by one
        Point prevPoint = points.iterator().next();
        Iterator<LinePoint> i = points.iterator();
        while (i.hasNext()) {
            Point nextPoint = i.next();
            line.setLine(prevPoint, nextPoint);
            if (line.intersects(rect)){
                result= true;
                break;
            }    
            prevPoint = nextPoint;
        }
        
        lineProvider.reset();
        rectProvider.reset();        
        return result;
    }

    @Override
    public Rectangle calculateShape(){
        int x1=Integer.MAX_VALUE,y1=Integer.MAX_VALUE,x2=Integer.MIN_VALUE,y2=Integer.MIN_VALUE;        
        
        for (Point point : points) {
            x1 = Math.min(x1, point.x);
            y1 = Math.min(y1, point.y);
            x2 = Math.max(x2, point.x);
            y2 = Math.max(y2, point.y);
        } 
        //add bending points
        return new Rectangle(x1, y1, (x2 - x1)==0?1:x2 - x1, y2 - y1==0?1:y2 - y1); 
    }
    
    @Override
    public Point getResizingPoint(){
        return resizingPoint;
    }
    
    @Override
    public void Reset(Point point) {
        this.Reset(point.x,point.y);  
    }

    @Override
    public void Reset(int x,int y) {
        Point p=isBendingPointClicked(x, y);
        floatingStartPoint.setLocation(p==null?x:p.x,p==null?y:p.y);
        floatingMidPoint.setLocation(p==null?x:p.x,p==null?y:p.y);
        floatingEndPoint.setLocation(p==null?x:p.x,p==null?y:p.y);  
    }

    @Override
    public void Reset() {
        this.Reset(floatingStartPoint);
    }

    @Override
    public void Resize(int xoffset, int yoffset, Point clickedPoint) {
        clickedPoint.setLocation(clickedPoint.x+xoffset, clickedPoint.y+yoffset);
    }
    
    @Override
    public Point getFloatingStartPoint() {
        return floatingStartPoint;
    }

    @Override
    public Point getFloatingMidPoint() {
        return floatingMidPoint;
    }

    @Override
    public Point getFloatingEndPoint() {
        return floatingEndPoint;
    }

    @Override
    public void shiftFloatingPoints() {
        floatingStartPoint.setLocation(points.get(points.size()-1).x, points.get(points.size()-1).y);
        floatingMidPoint.setLocation(floatingEndPoint.x, floatingEndPoint.y);       
    }
    
    @Override
    public void deleteLastPoint(){
        if (points.size() == 0)
            return;

        points.remove(points.get(points.size() - 1));

        //***reset floating start point
        if (points.size() > 0)
            floatingStartPoint.setLocation(points.get(points.size() - 1));        
    }

    @Override
    public void Reverse(int x,int y) {
        Point p=isBendingPointClicked(x, y);
        if (points.get(0).x == p.x &&
            points.get(0).y == p.y) {
            Collections.reverse(points);
        }       
    }

    @Override
    public void removePoint(int x, int y) {
        Point point=isBendingPointClicked(x, y);
        if(point!=null){
          points.remove(point);
          point = null;
        }    
    }

    @Override
    public boolean isEndPoint(int x, int y) {
        if (points.size() < 2) {
            return false;
        }
        
        Point point=isBendingPointClicked(x, y);
        if(point==null){
            return false;
        }
        //***head point
        if (points.get(0).x==point.x&&points.get(0).y==point.y) {
            return true;
        }
        //***tail point
        if ((points.get(points.size() - 1)).x==point.x&& (points.get(points.size() - 1)).y==point.y) {
            return true;
        }
        return false;
    }  


    @Override
    public boolean isFloating(){
      return (!(floatingStartPoint.equals(floatingEndPoint) &&
              floatingStartPoint.equals(floatingMidPoint)));  
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        if((this.copper.getLayerMaskID()&layermask)==0){
            return;
        }
        GeneralPath line=null;
        line = new GeneralPath(GeneralPath.WIND_EVEN_ODD,points.size());      
        line.moveTo((float)points.get(0).getX(),(float)points.get(0).getY());
         for(int i=1;i<points.size();i++){            
             line.lineTo((float)points.get(i).getX(),(float)points.get(i).getY());       
         } 
        g2.setStroke(new BasicStroke(thickness,JoinType.JOIN_ROUND.ordinal(),EndType.CAP_ROUND.ordinal()));
        g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());
        
        g2.draw(line);
    }

    @Override
    public void setResizingPoint(Point point) {
      this.resizingPoint=point;
    }
    
    @Override
    public void setSelected(boolean selection) {
        super.setSelected(selection);
        if(!selection){
            resizingPoint=null;
            for (LinePoint point : points) {
                point.setSelected(selection);
            }
        }
    }
    
    @Override
    public boolean isSublineInRect(Rectangle r){
        for (LinePoint point : points) {
            if (r.contains(point)) {
                return true;
            }
        }
        return false;        
    }
    
    @Override
    public boolean isSublineSelected() {
        if (points.size() == 0) {
            return false; //wire is being constructed
        }
        boolean p = points.get(0).isSelected();
        for (LinePoint point : points) {
            if (p != point.isSelected()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setSublineSelected(Rectangle r, boolean selected) {
        for (LinePoint point : points) {
            if (r.contains(point)) {
                point.setSelected(selected);
            }
        }
    }

    @Override
    public Set<LinePoint> getSublinePoints() {
        Set<LinePoint> subWirePoints = new HashSet<LinePoint>();
        if (this.isSelected()) {
            return null;
        }
        for (LinePoint point : points) {
            if (point.isSelected()) {
                subWirePoints.add(point);
            }
        }
        return subWirePoints;
    }
    


    @Override
    public void setClearance(int clearance) {
       this.clearance=clearance;
    }

    @Override
    public int getClearance() {
        return this.clearance;
    }    
    
    @Override
    public <T extends PCBShape & ClearanceSource> void printClearence(Graphics2D g2, T source) {
        Shape shape=(Shape)source;
        if((shape.getCopper().getLayerMaskID()&this.copper.getLayerMaskID())==0){        
             return;  //not on the same layer
        } 
        if(Objects.equals(source.getNetName(), this.net)&&(!("".equals(source.getNetName())))&&(!(null==this.net))){
            return;
        }
        GeneralPath line=null;
        int lineThickness;
        
        if(this.clearance!=0){
          lineThickness=(thickness+2*this.getClearance()) ;            
        }else{
          lineThickness=(thickness+2*source.getClearance());              
        }
        
        
        line = new GeneralPath(GeneralPath.WIND_EVEN_ODD,points.size());      
        line.moveTo((float)points.get(0).getX(),(float)points.get(0).getY());
         for(int i=1;i<points.size();i++){            
             line.lineTo((float)points.get(i).getX(),(float)points.get(i).getY());       
         } 

        g2.setStroke(new BasicStroke(lineThickness,JoinType.JOIN_ROUND.ordinal(),EndType.CAP_ROUND.ordinal()));
        g2.setColor(Color.WHITE);
        
        g2.draw(line);

    }

    @Override
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<track layer=\""+this.copper.getName()+"\" thickness=\""+this.getThickness()+"\" clearance=\""+clearance+"\" net=\""+this.net+"\" >");
        for(Point point:points){
            sb.append(point.x+","+point.y+","); 
        }        
        sb.append("</track>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element  element= (Element)node;
        
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
        this.copper=Layer.Copper.valueOf(element.getAttribute("layer"));
        this.clearance=element.getAttribute("clearance").equals("")?0:Integer.parseInt(element.getAttribute("clearance"));
        this.net=element.getAttribute("net");
        StringTokenizer st = new StringTokenizer(element.getTextContent(), ",");
        while(st.hasMoreTokens()){
          this.addPoint(new Point(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())));  
        }    
    }

    @Override
    public String getNetName() {
        
        return this.net;
    }

    @Override
    public void setNetName(String net) {
       this.net=net;
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
    static class Memento extends AbstractMemento<Board, PCBTrack> {

        private int Ax[];

        private int Ay[];

        private String net;
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }
        
        @Override
        public void loadStateTo(PCBTrack shape) {
            super.loadStateTo(shape);
            shape.points.clear();
            shape.net=net;
            for (int i = 0; i < Ax.length; i++) {
                shape.addPoint(new Point(Ax[i], Ay[i])); 
            }
            //***reset floating start point
            if (shape.points.size() > 0) {
                shape.floatingStartPoint.setLocation(shape.points.get(shape.points.size() -
                                                                 1));
                shape.Reset();
            }
        }
        
        @Override
        public void saveStateFrom(PCBTrack shape) {
            super.saveStateFrom(shape);
            net=shape.net;
            Ax = new int[shape.points.size()];
            Ay = new int[shape.points.size()];
            for (int i = 0; i < shape.points.size(); i++) {
                Ax[i] = shape.points.get(i).x;
                Ay[i] = shape.points.get(i).y;
            }
        }

        @Override
        public void Clear() {
            super.Clear();
            Ax = null;
            Ay = null;
            net=null;
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
            
            return (getUUID().equals(other.getUUID()) &&layerindex==other.layerindex&&
                    getMementoType().equals(other.getMementoType()) &&
                    thickness==other.thickness&&Objects.equals(net, other.net)&&
                    Arrays.equals(Ax, other.Ax) &&
                    Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash += this.getMementoType().hashCode();
            hash += thickness+layerindex;
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            hash += Objects.hashCode(net);
            return hash;
        }

        public boolean isSameState(Board unit) {
            PCBTrack wire = (PCBTrack)unit.getShape(getUUID());
            return (wire.getState(getMementoType()).equals(this));
        }
    }
}
