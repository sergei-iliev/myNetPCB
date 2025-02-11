package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.gerber.Fillable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.shape.Shape.Fill;
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
    
    protected com.mynetpcb.d2.shapes.Arc arc;
    protected Point resizingPoint;
    private ArcType arcType;
    
    public Point A,B,M;   //points to track arc vertices during mid point resize - it turns out arc.start,arc.end,arc.middle are different due to double math
    
    public Arc(double x,double y,double r,double startAngle,double endAngle,int thickness,int layermaskid)   {
        super(thickness,layermaskid);
        this.displayName="Arc";
        this.arc=new com.mynetpcb.d2.shapes.Arc(new Point(x,y),r,startAngle,endAngle); 
        this.arcType=ArcType.CENTER_POINT_ARC;
    }
    @Override
    public Arc clone() throws CloneNotSupportedException {        
        Arc copy= (Arc)super.clone();
        copy.arc=this.arc.clone();
        
        return copy;
    }
    @Override
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {        
        Point pt=null;
        if(resizingPoint!=null){
        	pt=resizingPoint.clone();
            pt.scale(scale.getScaleX());
            pt.move(-viewportWindow.getX(),- viewportWindow.getY());
        }        
        com.mynetpcb.d2.shapes.Arc a=this.arc.clone();
        a.scale(scale.getScaleX());
        a.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        Utilities.drawCircle(g2,  pt,a.getStart(),a.getEnd(),a.getMiddle(),a.getCenter());
                            
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

    public Point isControlRectClicked(double x,double y,ViewportWindow viewportWindow) {
          
          Point pt=new Point(x,y);
  		  pt.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
  		  pt.move(-viewportWindow.getX(),- viewportWindow.getY());
  		  
  		  Point p=this.arc.getStart();
  		  var tmp=p.clone();
  		  tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
  		  tmp.move(-viewportWindow.getX(),- viewportWindow.getY());  		  
          if(Utils.LT(pt.distanceTo(tmp),selectionRectWidth/2)){
              return p;
          }                     

          p=this.arc.getEnd();
          tmp=p.clone();
  		  tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
  		  tmp.move(-viewportWindow.getX(),- viewportWindow.getY());          
          if(Utils.LT(pt.distanceTo(tmp),selectionRectWidth/2)){
              return p;
          }          
          p=this.arc.getMiddle();
          tmp=p.clone();
  		  tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
  		  tmp.move(-viewportWindow.getX(),- viewportWindow.getY());                    
          if(Utils.LT(pt.distanceTo(tmp),selectionRectWidth/2)){
              return p;      
          }
          return null;
    }
    
    public boolean isStartAnglePointClicked(double x,double y,ViewportWindow viewportWindow){  
          Point pt=new Point(x,y);
		  pt.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		  pt.move(-viewportWindow.getX(),- viewportWindow.getY());
		  
		  var tmp=this.arc.getStart().clone();
		  tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		  tmp.move(-viewportWindow.getX(),- viewportWindow.getY()); 
          if(Utils.LT(pt.distanceTo(tmp),selectionRectWidth/2)){
            return true;
          }else{
            return false;
          }
    }
    public boolean isMidPointClicked(double x,double y,ViewportWindow viewportWindow){
          Point pt=new Point(x,y);
		  pt.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		  pt.move(-viewportWindow.getX(),- viewportWindow.getY());
		  
		  var tmp=this.arc.getMiddle().clone();
		  tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		  tmp.move(-viewportWindow.getX(),- viewportWindow.getY()); 
          if(Utils.LT(pt.distanceTo(tmp),selectionRectWidth/2)){
            return true;
          }else{
            return false;
        }
    }
    public boolean isExtendAnglePointClicked(double x,double y,ViewportWindow viewportWindow){
          Point pt=new Point(x,y);
		  pt.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		  pt.move(-viewportWindow.getX(),- viewportWindow.getY());
		  
		  var tmp=this.arc.getEnd().clone();
		  tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		  tmp.move(-viewportWindow.getX(),- viewportWindow.getY());         
          if(Utils.LT(pt.distanceTo(tmp),selectionRectWidth/2)){
            return true;
          }else{
            return false;
         }
    }        
    @Override
    public boolean isClicked(double x,double y) {
    	if(this.fill==Fill.EMPTY) {
      	  return (this.arc.isPointOn(new Point(x,y),this.thickness/2));
      	}else {    		
      	  return this.arc.contains(x, y);	
      	}
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
    public void resizeStartEndPoint(double xoffset, double yoffset, boolean isStartPoint) {
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
    	
    	
    	this.resizingPoint=isStartPoint?this.arc.getStart():this.arc.getEnd();
    	
    	
    }
    @Override
    public void resize(double xoffset, double yoffset, Point point) {        

    	//previous mid pont
    	var oldM=this.M.clone();		
        this.M=this.calculateResizingMidPoint(xoffset,yoffset);
        
         
    	//mid point on line
    	var m=new Point((this.A.x+this.B.x)/2,(this.A.y+this.B.y)/2);    		    	
    	var C=this.M;  //projection
    	var C1=m;
        
    	var y=C1.distanceTo(C);
    	var x=C1.distanceTo(this.A);
        
    	var l=(x*x)/y;
    	var lambda=(l-y)/2;

    	var v=new Vector(C,C1);
    	var norm=v.normalize();			  
    	
    	var a=C1.x +lambda*norm.x;
    	var b=C1.y + lambda*norm.y;
    	var center=new Point(a,b);
        var r = center.distanceTo(this.A);
    			        
            
        var startAngle =new Vector(center,this.A).slope();
        var endAngle = new Vector(center, this.B).slope();
    	
    	var start = 360 - startAngle;		
    	var end= (360-endAngle)-start;		
    		
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
    		
        //check if M and oldM on the same plane	    
    	if(Utilities.isLeftPlane(this.A,this.B,this.M)!=Utilities.isLeftPlane(this.A,this.B,oldM)){		     					
    			if(this.arc.endAngle<0){  //negative extend
    			 this.arc.endAngle=(360-end);
    			}else{
    			 this.arc.endAngle=-1*(360-end);	
    			}						     		
    	    }else{							
    	    	this.arc.endAngle=end;			
    	    }			   
    	
        this.resizingPoint=this.arc.getMiddle();
    }

    private Point calculateResizingMidPoint(double x, double y){
            //Line line=new Line(this.arc.getCenter(),this.arc.getMiddle());
            //return line.projectionPoint(new Point(x,y));
    	    var middle=new Point((this.A.x+this.B.x)/2,(this.A.y+this.B.y)/2);
    	    var line=new Line(middle,this.M);
    	    return line.projectionPoint(new Point(x,y));
    }
    @Override
    public void alignResizingPointToGrid(Point point) {
        // TODO Implement this method
    }
    @Override
    public void alignStartEndPointToGrid(boolean isStartPoint) {
    	var A=this.arc.getStart().clone();
    	var B=this.arc.getEnd().clone();
    	
    	if(isStartPoint) {
    		var targetPoint=this.getOwningUnit().getGrid().positionOnGrid(A.x,A.y);
    		this.resizeStartEndPoint(targetPoint.x-A.x,targetPoint.y-A.y, isStartPoint);
    	}else {
    		var targetPoint=this.getOwningUnit().getGrid().positionOnGrid(B.x,B.y);
    		this.resizeStartEndPoint(targetPoint.x-B.x,targetPoint.y-B.y, isStartPoint);    		
    	}
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
