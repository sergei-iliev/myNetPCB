package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.gerber.Fillable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Segment;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.d2.shapes.Vector;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Arc  extends Shape implements ArcGerberable,Fillable,Resizeable,Externalizable {
    
    private com.mynetpcb.d2.shapes.Arc arc;
    private Point resizingPoint;
    private ArcType arcType;
    
    public Arc(double x,double y,double r,double startAngle,double endAngle,int thickness,int layermaskid)   {
        super(thickness,layermaskid);
        this.displayName="Arc";
        this.arc=new com.mynetpcb.d2.shapes.Arc(new Point(x,y),r,startAngle,endAngle); 
        this.selectionRectWidth=3000;
        this.arcType=ArcType.CENTER_POINT_ARC;
    }
    @Override
    public Arc clone() throws CloneNotSupportedException {        
        Arc copy= (Arc)super.clone();
        copy.arc=this.arc.clone();
        
        return copy;
    }
    
    @Override
    public void setSide(Layer.Side side, Line line,double angle) {
        this.setCopper(Layer.Side.change(this.getCopper().getLayerMaskID()));
        this.mirror(line);
        this.rotate=angle;
    }
    
    @Override
    public void mirror(Line line) {
      this.arc.mirror(line);
    }
    public void setRadius(double r){
            this.arc.r=r;   
    }
    public void setExtendAngle(double extendAngle){
        this.arc.endAngle=Utilities.roundDouble(extendAngle);
    }
    public void setStartAngle(double startAngle){        
        this.arc.startAngle=Utilities.roundDouble(startAngle);
    }
    @Override
    public long getClickableOrder() {
        
        return (long)arc.area();
    }
    public double getRadius(){
       return arc.r;
    }
    public double getStartAngle(){
        return arc.startAngle;
    }
    public Point getCenter(){
        return arc.pc;
    }
    public double getExtendAngle(){
        return arc.endAngle;
    }
    public ArcType getArcType() {
		return arcType;
	}
    public void setArcType(ArcType arcType) {
    	this.arcType = arcType;
    }
    @Override
    public Box getBoundingShape() {
        
        return this.arc.box();
    }
    @Override
    public void move(double xoffset,double yoffset) {
        this.arc.move(xoffset,yoffset);
        
    } 
    @Override
    public Point getStartPoint() {        
        return this.arc.getStart();
    }
    
    public Point getMiddlePoint() {
        return this.arc.getMiddle();        
    }
    
    @Override
    public Point getEndPoint() {
        return this.arc.getEnd();        
    }

    @Override
    public double getI() {
        double i=0;        
        //Utilities.QUADRANT quadrant= Utilities.getQuadrantLocation(arc.pc,getStartPoint());
       
        //    switch(quadrant){
        //     case SECOND:case THIRD:
                i=arc.pc.x-getStartPoint().x;
        //        break;
        //     case FIRST:case FORTH:
                //convert to -
       //         i=arc.pc.x-getStartPoint().x;
       //      break;
       //     }        
        return i;
    }

    @Override
    public double getJ() {
        double j=0;
        //Utilities.QUADRANT quadrant= Utilities.getQuadrantLocation(arc.pc,getStartPoint());        
        //    switch(quadrant){
        //     case FIRST:case SECOND:
                j=arc.pc.y-getStartPoint().y;
        //        break;
        //     case THIRD:case FORTH:
                //convert to -
         //       j=arc.pc.y-getStartPoint().y;
        //     break;
        //    }        
        return j;
    }

    @Override
    public boolean isSingleQuadrant() {
        return Math.abs(arc.endAngle)<=90;
    }

    @Override
    public boolean isClockwise() {
        return arc.endAngle <0;
    }

    public Point isControlRectClicked(int x,int y) {
          Point a=new Point(x,y);
          if(Utils.LT( a.distanceTo(this.arc.getStart()),selectionRectWidth/2)){
              return this.arc.getStart();
          }                     
          if(Utils.LT(a.distanceTo(this.arc.getEnd()),selectionRectWidth/2)){
              return this.arc.getEnd();
          }
          if(Utils.LT(a.distanceTo(this.arc.getMiddle()),selectionRectWidth/2)){
              return this.arc.getMiddle();      
          }
          return null;
    }
    public boolean isStartAnglePointClicked(int x,int y){  
        Point a=new Point(x,y);
        if(Utils.LT(a.distanceTo(this.arc.getStart()),selectionRectWidth/2)){
            return true;
        }else{
            return false;
        }
    }
    public boolean isMidPointClicked(int x,int y){
        Point a=new Point(x,y);
        if(Utils.LT(a.distanceTo(this.arc.getMiddle()),selectionRectWidth/2)){
            return true;
        }else{
            return false;
        }
    }
    public boolean isExtendAnglePointClicked(int x,int y){
        Point a=new Point(x,y);
        if(Utils.LT(a.distanceTo(this.arc.getEnd()),selectionRectWidth/2)){
            return true;
        }else{
            return false;
        }
    }    
    
    @Override
    public boolean isClicked(int x,int y) {
        if(this.arc.isPointOn(new Point(x, y),this.thickness))
        	return true;
        
    	if (this.arc.contains(new Point(x, y)))
                    return true;
            else
                    return false;
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
    public void setRotation(double rotate, Point center) {
        double alpha=rotate-this.rotate;
        this.arc.rotate(alpha,center);          
        this.rotate=rotate;        
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
      this.arc.rotate(angle,origin); 
    }
    @Override
    public void resizeStartEndPoint(int xoffset, int yoffset, boolean isStartPoint) {
   	    Point A=this.arc.getStart().clone();
   	    Point B=this.arc.getEnd().clone();
   	    Point M=this.arc.getMiddle().clone();
   	    Point O=new Point();
   	    
    	Segment middleSegment=new Segment(A,B);
    	Point middlePoint=middleSegment.middle();
    	
    	double delta=M.distanceTo(middlePoint);
    	if(isStartPoint) {
    		A.move(xoffset, yoffset);
    	}else {
    		B.move(xoffset, yoffset);
    	}
    	middleSegment.set(A, B);
    	middlePoint=middleSegment.middle();
    	
    	O.set(middlePoint);
    	M.set(middlePoint);
    	
    	Vector v=new Vector(middlePoint,A);
    	if(this.arc.endAngle>0) {
    	  v.rotate90CW();	
    	}else {
    	  v.rotate90CCW();	
    	}
    	
    	Vector norm=v.normalize();
    	double x=M.x+delta*norm.x;
    	double y=M.y+delta*norm.y;
    	M.set(x, y);  //set new position of mid point
    	
    	//same calculation as arc on 3 points
        Point C=M;  //projection
        Point C1=O;
        
        x=C1.distanceTo(A);
        y=C1.distanceTo(C);
        
        double l=(x*x)/y;
        double lambda=(l-y)/2;
        
        v=new Vector(C,C1);
        norm=v.normalize();			  
      	
        double a=C1.x +lambda*norm.x;
        double b=C1.y + lambda*norm.y;
        Point center=new Point(a,b);
        double r = center.distanceTo(A);  
          
        double startAngle =new Vector(center,A).slope();
        double endAngle = new Vector(center, B).slope();
          
        

        double start = 360 - startAngle;		
        double end= (360-endAngle)-start;		
      		
        if(this.arc.endAngle<0){  //negative extend
      	if(end>0){			  
      	  end=end-360;
      	}
        }else{		//positive extend			
      	if(end<0){ 					   
      	  end=360-Math.abs(end);
      	}			
     	  }

      	
        this.arc.getCenter().set(center.x,center.y);
        this.arc.r=r;
        this.arc.startAngle=start;
        this.arc.endAngle=end;
    	
    	
    	
    }
    
    @Override
    public void resize(int xoffset, int yoffset, Point point) {        
         
          this.resizingPoint=this.calculateResizingMidPoint(xoffset,yoffset);
            
          //old middle point on arc
          Point a1=this.arc.getMiddle();  
          //mid point on line
          Point m=new Point((this.arc.getStart().x+this.arc.getEnd().x)/2,(this.arc.getStart().y+this.arc.getEnd().y)/2);
          //new middle point on arc
          Point a2=this.resizingPoint;  //new middle
        	
        	//do they belong to the same plane in regard to m 
          Vector vec = new Vector(m, a2);
          Vector linevec=new Vector(m,a1);
          boolean samePlane = Utils.GT(vec.dot(linevec.normalize()), 0);           
            
          //which plane            
          if(!samePlane){
              return;
          }

          Point C=this.resizingPoint;  //projection
          Point C1=m;
          
          double x=C1.distanceTo(this.arc.getStart());
          double y=C1.distanceTo(C);

            
          double l=(x*x)/y;
          double lambda=(l-y)/2;

          Vector v=new Vector(C,C1);
          Vector norm=v.normalize();			  
        	
          double a=C1.x +lambda*norm.x;
          double b=C1.y + lambda*norm.y;
          Point center=new Point(a,b);
            
            
          double startAngle =new Vector(center,this.arc.getStart()).slope();
          double endAngle = new Vector(center, this.arc.getEnd()).slope();
            
          double r = center.distanceTo(this.arc.getStart());

          double start = 360 - startAngle;		
          double end= (360-endAngle)-start;		
        		
          if(this.arc.endAngle<0){  //negative extend
        	if(end>0){			  
        	  end=end-360;
        	}
          }else{		//positive extend			
        	if(end<0){ 					   
        	  end=360-Math.abs(end);
        	}			
       	  }

        	
          this.arc.getCenter().set(center.x,center.y);
          this.arc.r=r;
          this.arc.startAngle=start;
          this.arc.endAngle=end;
   
    }
    private Point calculateResizingMidPoint(int x, int y){
            Line line=new Line(this.arc.getCenter(),this.arc.getMiddle());
            return line.projectionPoint(new Point(x,y)); 
    }
    @Override
    public void alignResizingPointToGrid(Point point) {
        // TODO Implement this method
    }

    @Override
    public String toXML() {
        return "<arc copper=\""+getCopper().getName()+"\"  x=\""+Utilities.roundDouble(this.arc.pc.x)+"\" y=\""+Utilities.roundDouble(this.arc.pc.y)+"\" radius=\""+Utilities.roundDouble(this.arc.r)+"\"  thickness=\""+this.getThickness()+"\" start=\""+Utilities.roundDouble(this.arc.startAngle)+"\" extend=\""+Utilities.roundDouble(this.arc.endAngle)+"\" fill=\""+this.getFill().index+"\" />\r\n";
    }

    @Override
    public void fromXML(Node node)  {
        Element  element= (Element)node;        
        
        if(element.hasAttribute("copper")){
          this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));    
        } 
        double xx=(Double.parseDouble(element.getAttribute("x")));
        double yy=(Double.parseDouble(element.getAttribute("y")));  
        
        if(element.getAttribute("width").length()>0){      
            int diameter=(Integer.parseInt(element.getAttribute("width")));           
            this.arc.pc.set(xx+((diameter/2)),yy+((diameter/2)));
            this.arc.r=diameter/2;                            
        }else{
            double radius=(Double.parseDouble(element.getAttribute("radius"))); 
            this.arc.pc.set(xx,yy);
            this.arc.r=radius;                                      
        } 
        
        this.setStartAngle(Double.parseDouble(element.getAttribute("start")));
        this.setExtendAngle(Double.parseDouble(element.getAttribute("extend")));
System.out.println(element.getAttribute("fill"));
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
        this.setFill(Fill.byIndex(Integer.parseInt(element.getAttribute("fill"))==0?1:Integer.parseInt(element.getAttribute("fill")))); 
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        Box rect = this.arc.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        com.mynetpcb.d2.shapes.Arc  a=this.arc.clone();
        a.scale(scale.getScaleX());
        a.move(-viewportWindow.getX(),- viewportWindow.getY());
        if (fill == Fill.EMPTY) { //framed
            double wireWidth = thickness * scale.getScaleX();
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
            //transparent rect
            a.paint(g2, false);
        } else { //filled
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
            Composite originalComposite = g2.getComposite();                     
            g2.setComposite(composite ); 
            a.paint(g2,true);
            g2.setComposite(originalComposite); 
        }
        
        if(this.isSelected()&&isControlPointVisible){            
            Utilities.drawCrosshair(g2,  resizingPoint,(int)(selectionRectWidth*scale.getScaleX()),a.getStart(),a.getEnd(),a.getMiddle());
        } 
    	
    }
    @Override
    public void print(Graphics2D g2,PrintContext printContext,int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }

        g2.setStroke(new BasicStroke(thickness,1,1));    
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());        
        if(this.fill==Fill.EMPTY){
          this.arc.paint(g2, false);      
        }else{
          this.arc.paint(g2, true);      
        }
 
    }
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Footprint,Arc>{
        private double startAngle;        
        private double endAngle;
        private double x;
        private double r;
        private double y;
        
        
        public Memento(MementoType mementoType) {
           super(mementoType);            
        }
        @Override
        public void saveStateFrom(Arc shape) {
            super.saveStateFrom(shape);         
            this.startAngle=(shape).arc.startAngle;
            this.endAngle=(shape).arc.endAngle;

            this.x=shape.arc.pc.x;
            this.y=shape.arc.pc.y;
            this.r=shape.arc.r;
            
        }
        @Override
        public void loadStateTo(Arc shape) {
           super.loadStateTo(shape);
           shape.arc.r=r;
           shape.arc.pc.set(x, y);
           shape.arc.startAngle=startAngle;
           shape.arc.endAngle=endAngle;
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
            
            return super.equals(obj) && Utils.EQ(this.x, other.x)&&
            Utils.EQ(this.y, other.y)&&Utils.EQ(this.r,other.r)&&Utils.EQ(this.startAngle, other.startAngle)&&Utils.EQ(this.endAngle,other.endAngle);  
        }
        
        @Override
        public int hashCode(){            
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.x)+
                    Double.hashCode(this.y)+Double.hashCode(this.r)+Double.hashCode(this.startAngle)+Double.hashCode(this.endAngle);
            return hash;
        }
        
        @Override
        public boolean isSameState(Unit unit) {
            Arc arc = (Arc) unit.getShape(getUUID());
            return (arc.getState(getMementoType()).equals(this));
        }
    }    
}
