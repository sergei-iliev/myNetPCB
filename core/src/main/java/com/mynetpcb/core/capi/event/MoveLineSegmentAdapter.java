package com.mynetpcb.core.capi.event;

import java.util.List;

import com.mynetpcb.core.capi.line.Segmentable;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Segment;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.d2.shapes.Vector;
/*
 * Used is 90 degree end segment movement instead
 */
@Deprecated
public class MoveLineSegmentAdapter {
	private final List<Segment> segments;
	public final Segment segment,copy;
	
	public MoveLineSegmentAdapter(Segmentable track,Segment segment) {		
		  this.segments=track.getSegments();
	      this.segment=segment;
	      this.copy=segment.clone();
	}
	
	public void moveSegment(Point p){	
		 if(this.segment==null){
		   return;	
		 }	 
	     if(this.isSingleSegment()){
	    	 new IllegalStateException("Segments size must be bigger then 1");
		 }else if(this.isEndSegment()){
		   this.moveEndSegment(p);	
		 }else{
			this.moveMidSegment(p);
		 }
	}
	public void moveEndSegment(Point p){
	     //find neigbor segment
		 var segm=this.findPrev();     
		 if(segm==null){
		   	segm=this.findNext();
		 }
		 	
		 
		 //find common point and end point on same segm
	     var commonpoint=segm.pe; //common point between target segment and segm
	     var endpoint1=segm.ps;   //distant point from common one		
		 if(segm.ps==this.segment.ps||segm.ps==this.segment.pe){	  
		   commonpoint=segm.ps;
	       endpoint1=segm.pe;	   
	     }
		 //find free end point on this.segment
		 var endpoint2=this.segment.ps;
	     if(commonpoint==this.segment.ps){
		   endpoint2=this.segment.pe;	
		 }
	//find the direction of movement in regard to mouse point and segm end point
	     var invertDirection=true;
	     if(Utilities.isLeftPlane(this.segment.ps,this.segment.pe,p)==Utilities.isLeftPlane(this.segment.ps,this.segment.pe,endpoint1)){		
		 	invertDirection=false;	    
	     }
	//1. move common point
	     var projPoint=this.segment.projectionPoint(p);	 
	     var distance=projPoint.distanceTo(p);		
	    
	     var vsegment=new Vector(commonpoint,endpoint2);
	     var vsegm=new Vector(commonpoint,endpoint1);

		 
	     var angle=vsegment.angleTo(vsegm);
	     if(angle>180){
	        angle=360-angle;    
		 }
		//find units to move along segm
		var sina=Math.sin(Utils.radians(angle));
	    var delta=distance/sina;

	    var inverted=vsegm.clone();
	    if(invertDirection){
	      inverted.invert();
		}
	    var norm=inverted.normalize();
		  
	      
	    var x=commonpoint.x +delta*norm.x;
		var y=commonpoint.y +delta*norm.y;
		
	//2. move free end point of this.segment
	    var xx=endpoint2.x +delta*norm.x;
		var yy=endpoint2.y +delta*norm.y;
	    	
	    endpoint2.set(xx,yy);
	    commonpoint.set(x,y);
		
	}
	public void moveMidSegment(Point p){
		 //find neigbor segment
		 var prevsegm=this.findPrev();     	      
	     var nextsegm=this.findNext();     

	//1. prev segment movement
		 //find common point and end point of  prev segment
	     var prevpoint=prevsegm.pe;	//common point between target segment and prev
	     var endpoint1=prevsegm.ps;  //distance point
		 if(prevsegm.ps==this.segment.ps||prevsegm.ps==this.segment.pe){	  
		   prevpoint=prevsegm.ps;
	       endpoint1=prevsegm.pe;	   
	     }

		 //find end point on this.segment
		 var endpoint2=this.segment.ps;
	     if(prevpoint==this.segment.ps){
		   endpoint2=this.segment.pe;	
		 }

	     //find the direction of movement in regard to mouse point and prevsegm end point
	     var invertDirection=true;
	     if(Utilities.isLeftPlane(this.segment.ps,this.segment.pe,p)==Utilities.isLeftPlane(this.segment.ps,this.segment.pe,endpoint1)){		
		 	invertDirection=false;	    
	     }	 

	     var projPoint=this.segment.projectionPoint(p);	 
	     var distance=projPoint.distanceTo(p);		
	    
	     var vsegment=new Vector(prevpoint,endpoint2);
	     var vsegm=new Vector(prevpoint,endpoint1);
		 
	     var angle=vsegment.angleTo(vsegm);
	     if(angle>180){
	        angle=360-angle;    
		 }
		//find units to move along segm
		var sina=Math.sin(Utils.radians(angle));
	    var delta=distance/sina;

	    var inverted=vsegm.clone();
	    if(invertDirection){
	    	inverted.invert();
		}

	    var norm=inverted.normalize();
		  
	      
	    var x=prevpoint.x +delta*norm.x;
		var y=prevpoint.y +delta*norm.y;
	    


	//2. next segment movement
		 //find common point and end point on same segm - prev
	     var nextpoint=nextsegm.pe; //common point between target segment and next segment
	     endpoint1=nextsegm.ps;
		 if(nextsegm.ps==this.segment.ps||nextsegm.ps==this.segment.pe){	  
		   nextpoint=nextsegm.ps;
	       endpoint1=nextsegm.pe;	   
	     }

		 //find end point on this.segment
		 endpoint2=this.segment.ps;
	     if(nextpoint==this.segment.ps){
		   endpoint2=this.segment.pe;	
		 }
	     //find the direction of movement in regard to mouse point and nextsegm end point
	     invertDirection=true;
	     if(Utilities.isLeftPlane(this.segment.ps,this.segment.pe,p)==Utilities.isLeftPlane(this.segment.ps,this.segment.pe,endpoint1)){		
		 	invertDirection=false;	    
	     }	 

	      projPoint=this.segment.projectionPoint(p);	 
	      distance=projPoint.distanceTo(p);		
	    
	      vsegment=new Vector(nextpoint,endpoint2);
	      vsegm=new Vector(nextpoint,endpoint1);
		 
	     angle=vsegment.angleTo(vsegm);
	     if(angle>180){
	        angle=360-angle;    
		 }
		//find units to move along segm
		 sina=Math.sin(Utils.radians(angle));
	     delta=distance/sina;

	     inverted=vsegm.clone();
	     if(invertDirection){
	    	inverted.invert();
		 }

	     norm=inverted.normalize();

	    var xx=nextpoint.x +delta*norm.x;
		var yy=nextpoint.y +delta*norm.y;
	    nextpoint.set(xx,yy);
	    
	    prevpoint.set(x,y);	
	}
	/*
	Avoid loosing direction vectors by moving point to overlapping position
	*/
	public void validateNonZeroVector(){		
		  for(var s:this.segments){				
			  if((Double.isNaN(s.length()))||Utils.EQ(s.length(),0)){
				this.segment.set(this.copy.ps.x,this.copy.ps.y,this.copy.pe.x,this.copy.pe.y);
				break;
			  }
		  }		
	}	
	public boolean isSingleSegment(){
		return this.segments.size()<2;
	}	
	public boolean isEndSegment(){
		 //find neigbor segment
		 var segm=this.findPrev();     	 
	     if(segm==null){
		     return true;
		 }

	     segm=this.findNext();     
	     return segm==null;		
	}
	
	public Segment findPrev(){
		Segment prev=null;
		for(var s :this.segments){				
			if(s.same(this.segment)){
				return prev;
			}
			prev=s;
		}
		return null;
	}
	public Segment findNext(){		
		Segment next=null;
		for (var i = this.segments.size() - 1; i >= 0; i--) {
			if(this.segments.get(i).same(this.segment)){
				return next;
			}
			next=this.segments.get(i);
	    	
		}
		return null;		
	}


}
