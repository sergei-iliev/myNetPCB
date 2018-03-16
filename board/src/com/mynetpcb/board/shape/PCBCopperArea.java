package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.board.CompositeLayerable;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.CopperAreaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.core.utils.Utilities;

import com.mynetpcb.pad.shape.Pad;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PCBCopperArea extends CopperAreaShape implements PCBShape{

    public  Point floatingStartPoint;
    
    public  Point floatingEndPoint;
    
    private Point resizingPoint;
    
    private int clearance;
    
    private Polygonal polygon;
    
    private String net;
    
    private PadShape.PadConnection padConnection;
    
    public PCBCopperArea(int layermaskId) {
        super(layermaskId);
        this.clearance=Grid.MM_TO_COORD(0.2); 
        floatingStartPoint=new Point();
        floatingEndPoint=new Point();
        this.polygon = new Polygonal(); 
        this.selectionRectWidth=3000;
        this.padConnection=PadShape.PadConnection.DIRECT;
        this.fill=Fill.FILLED;
    }

    public PCBCopperArea clone() throws CloneNotSupportedException {
        PCBCopperArea copy=(PCBCopperArea)super.clone();
        copy.floatingStartPoint = new Point();
        copy.floatingEndPoint = new Point();
        copy.polygon=this.polygon.clone();  
        return copy;
    }
    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        getOwningUnit().getGrid().snapToGrid(targetPoint);   
    }
    @Override
    public List<LinePoint> getLinePoints() {
       return this.polygon.getLinePoints();
    }

    @Override
    public void addPoint(Point point) {
        polygon.addPoint(point);
    }
    
    @Override
    public void add(int x, int y) {
        polygon.addPoint(new Point(x,y));
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
        Point prevPoint = polygon.getLinePoints().get(polygon.getLinePoints().size()-1);
        Iterator<LinePoint> i = polygon.getLinePoints().iterator();
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
           polygon.addPoint(index, new Point(x,y)); 
        }
    }

    @Override
    public Point isBendingPointClicked(int x, int y) {
       return isControlRectClicked(x,y);
    }

    @Override
    public boolean isInRect(Rectangle r) {

        for(Point wirePoint:polygon.getLinePoints()){
            if (!r.contains(wirePoint))
                return false;            
        }
        return true;
    }
    
    @Override
    public void Reset(Point point) {
        this.Reset(point.x,point.y);  
    }

    @Override
    public void Reset(int x,int y) {
        Point p=isBendingPointClicked(x, y);
        floatingStartPoint.setLocation(p==null?x:p.x,p==null?y:p.y);
        floatingEndPoint.setLocation(p==null?x:p.x,p==null?y:p.y);  
    }

    @Override
    public void Reset() {
        this.Reset(floatingStartPoint);
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
        floatingStartPoint.setLocation(polygon.getLinePoints().get(polygon.getLinePoints().size()-1).x, polygon.getLinePoints().get(polygon.getLinePoints().size()-1).y);
    }

    @Override
    public void deleteLastPoint() {
        // TODO Implement this method
    }

    @Override
    public void Reverse(int x, int y) {
        // TODO Implement this method

    }

    @Override
    public void removePoint(int x, int y) {
        // TODO Implement this method

    }

    @Override
    public boolean isEndPoint(int x, int y) {
        return false;
    }

    @Override
    public boolean isFloating() {
        return (!floatingStartPoint.equals(floatingEndPoint));                
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
        
        if((layersmask&this.copper.getLayerMaskID())==0){        
             return;
        }
        
        Rectangle2D scaledBoundingRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);         
        if(!this.isFloating()&&!scaledBoundingRect.intersects(viewportWindow)){
          return;   
        }

        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
        GeneralPath temporal=(GeneralPath)provider.getShape();
        
        List<LinePoint>points=polygon.getLinePoints();
        temporal.moveTo(points.get(0).getX(),points.get(0).getY());
        for(int i=1;i<points.size();i++){            
              temporal.lineTo(points.get(i).getX(),points.get(i).getY());       
        } 
        if(isFloating()){            
            temporal.lineTo(floatingEndPoint.getX(),floatingEndPoint.getY());  
        }
        temporal.closePath();
        
        AffineTransform translate = AffineTransform.getTranslateInstance(-viewportWindow.x, -viewportWindow.y);
        temporal.transform(scale);
        temporal.transform(translate);
        
        
        g2.setStroke(new BasicStroke()); 
        g2.setColor(isSelected()?Color.GRAY:(this.copper.getColor()));
        
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
        Composite originalComposite = g2.getComposite();                     
        g2.setComposite(composite );        
        if(this.fill==Fill.FILLED){
            g2.fill(temporal);        
        }else{
            g2.draw(temporal);
        }
        
        if(isFloating()){
            g2.draw(temporal);
        }
        //draw bounding 
        if(this.isSelected()){           
           g2.setColor(Color.BLUE); 
           g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));            
           g2.draw(temporal);
        }
        
        
        if(this.fill==Fill.FILLED){
        //draw clearence background
         Collection<ClearanceTarget> targets=getOwningUnit().getShapes(ClearanceTarget.class);
         for(ClearanceTarget target:targets){
              target.drawClearence(g2, viewportWindow, scale, this);
         }
        }
        
        g2.setComposite(originalComposite);
        provider.reset();
    }
    @Override
    public void Print(Graphics2D g2,PrintContext printContext, int layersmask) {
        if((layersmask&this.copper.getLayerMaskID())==0){        
             return;
        }
        
        GeneralPath polyline = 
            new GeneralPath(GeneralPath.WIND_EVEN_ODD, polygon.npoints);


        polyline.moveTo(polygon.getLinePoints().get(0).getX(),polygon.getLinePoints().get(0).getY());
        for(int i=1;i<polygon.getLinePoints().size();i++){            
             polyline.lineTo(polygon.getLinePoints().get(i).getX(),polygon.getLinePoints().get(i).getY());       
        }
        
        polyline.closePath();
              
        g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:this.copper.getColor());        
        g2.fill(polyline);        

        //print clearence background
         Collection<ClearanceTarget> targets=getOwningUnit().getShapes(ClearanceTarget.class);
         for(ClearanceTarget target:targets){
              target.printClearence(g2,printContext, this);
         }        
    }
    @Override
    public void drawControlShape(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale){   
        if((!this.isSelected())){
          return;
        }        
        Utilities.drawCrosshair(g2, viewportWindow, scale, polygon.getLinePoints(), resizingPoint, selectionRectWidth);
    }
    
    @Override
    public Point isControlRectClicked(int x, int y) {
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(selectionRectWidth/2), y-(selectionRectWidth/2),selectionRectWidth, selectionRectWidth);
        
        Point point=null;
        Point click=new Point(x,y);
        int distance=Integer.MAX_VALUE;
        
        for (Point wirePoint : polygon.getLinePoints()) {
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
    public java.awt.Shape calculateShape(){       
       return polygon;
    }
    
    @Override
    public boolean isClicked(int x, int y) {
        boolean result=false;
        //build testing rect
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(selectionRectWidth/2), y-(selectionRectWidth/2),selectionRectWidth, selectionRectWidth);
        //inspect line by line
        FlyweightProvider lineProvider=ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line=(Line2D)lineProvider.getShape();
        
        //***make lines and iterate one by one
        Point prevPoint = polygon.getLinePoints().get(polygon.getLinePoints().size()-1);
        Iterator<LinePoint> i = polygon.getLinePoints().iterator();
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
    public Point getResizingPoint() {
        return resizingPoint;
    }

    @Override
    public void setResizingPoint(Point point) {
        this.resizingPoint=point;
    }

    @Override
    public void Resize(int xoffset, int yoffset, Point clickedPoint) {
        polygon.Resize(xoffset, yoffset, clickedPoint);
    }
    
    @Override
    public void Rotate(AffineTransform rotation) {
       polygon.Rotate(rotation);
    }
    
    @Override
    public void setSelected(boolean selection) {
        super.setSelected(selection);
        if(!selection){
            resizingPoint=null;
        }
    }
    
    @Override
    public void Move(int xoffset, int yoffset) {
        polygon.Move(xoffset, yoffset);
    }
    
    @Override
    public void Mirror(Point A,Point B) {
        polygon.Mirror(A,B);
    }
    
    @Override
    public String toXML() {
        StringBuffer sb=new StringBuffer();
        sb.append("<copperarea layer=\""+this.copper.getName()+"\" clearance=\""+this.clearance+"\" net=\""+(this.net==null?"":this.net) +"\" padconnect=\""+this.padConnection+"\" >");
        for(Point point:polygon.getLinePoints()){
            sb.append(point.x+","+point.y+","); 
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
          this.addPoint(new Point(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken())));  
        }  

    }

    
    @Override
    public String getDisplayName() {
        return "Copper Area";
    }
    
    @Override
    public int getDrawingOrder() {
        if(getOwningUnit()==null){            
            return super.getDrawingOrder();
        }
        
        if(((CompositeLayerable)getOwningUnit()).getActiveSide()==Layer.Side.resolve(this.copper.getLayerMaskID())){
           return 2;
        }else{
           return 1; 
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
    public Polygon getClippingRegion(ViewportWindow viewportWindow,AffineTransform scale) {
        Polygon clip=new Polygon();
        for(Point point:polygon.getLinePoints()){
            Point position=new Point();            
            scale.transform(point,position);            
            clip.addPoint(position.x-viewportWindow.x,position.y-viewportWindow.y);
        }
        return clip;
    }
    
    @Override
    public long getOrderWeight() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public String getNetName() {        
        return this.net;
    }

    @Override
    public void setNetName(String net) {
       if((net!=null)&&(!net.trim().isEmpty())){
         this.net=net;
       }else{
         this.net=null;  
       }
    }

    public void setPadConnection(PadShape.PadConnection padConnection) {
        this.padConnection = padConnection;
    }

    public PadShape.PadConnection getPadConnection() {
        return padConnection;
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

    static class Memento extends AbstractMemento<Board, PCBCopperArea> {

        private int Ax[];

        private int Ay[];
        
        private String net;
        
        private PadShape.PadConnection padConnection;
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }
        
        @Override
        public void loadStateTo(PCBCopperArea shape) {
            super.loadStateTo(shape);
            shape.net=net;
            shape.padConnection=padConnection;
            shape.polygon.reset();
            for (int i = 0; i < Ax.length; i++) {
                shape.addPoint(new Point(Ax[i], Ay[i])); 
            }
            //***reset floating start point
            if (shape.polygon.getLinePoints().size() > 0) {
                shape.floatingStartPoint.setLocation(shape.polygon.getLinePoints().get(shape.polygon.getLinePoints().size() -
                                                                 1));
                shape.Reset();
            }
        }
        
        @Override
        public void saveStateFrom(PCBCopperArea shape) {
            super.saveStateFrom(shape);
            net=shape.net;
            padConnection=shape.padConnection;            
            Ax = new int[shape.polygon.getLinePoints().size()];
            Ay = new int[shape.polygon.getLinePoints().size()];
            for (int i = 0; i < shape.polygon.getLinePoints().size(); i++) {
                Ax[i] = shape.polygon.getLinePoints().get(i).x;
                Ay[i] = shape.polygon.getLinePoints().get(i).y;
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
            
            return (getUUID().equals(other.getUUID())&&fill==other.fill&&layerindex==other.layerindex&&
                    getMementoType().equals(other.getMementoType()) &&
                    Arrays.equals(Ax, other.Ax) &&Objects.equals(net, other.net)&&padConnection.equals(other.padConnection)&&
                    Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash += this.getMementoType().hashCode();
            hash +=layerindex;
            hash+=fill;
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            hash+=Objects.hashCode(net);
            hash+=Objects.hashCode(padConnection);
            return hash;
        }

        public boolean isSameState(Board unit) {
            PCBCopperArea wire = (PCBCopperArea)unit.getShape(getUUID());
            return (wire.getState(getMementoType()).equals(this));
        }
    }
    
}
