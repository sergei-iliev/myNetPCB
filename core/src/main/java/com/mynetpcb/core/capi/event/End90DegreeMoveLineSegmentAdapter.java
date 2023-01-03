package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.line.Segmentable;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Segment;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.d2.shapes.Vector;

public class End90DegreeMoveLineSegmentAdapter extends MoveLineSegmentAdapter{
	public End90DegreeMoveLineSegmentAdapter(Segmentable track,Segment segment) {		
		  super(track,segment);
	}
	
	@Override
	public void moveEndSegment(Point p){
		if(!(this.segment.isVertical()||this.segment.isHorizontal())){		
			super.moveEndSegment(p);
			return;
		}
		
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
	    double xx,yy;   
	    if(this.segment.isHorizontal()){     
	     xx=endpoint2.x;
		 yy=y;
	    }else{
		 xx=x;
		 yy=endpoint2.y;
		}	
	    endpoint2.set(xx,yy);
	    commonpoint.set(x,y);

	}	
	
}
