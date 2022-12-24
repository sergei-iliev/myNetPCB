package com.mynetpcb.core.capi.line;

import java.util.List;

import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Segment;

public interface Segmentable {

	public boolean isSegmentClicked(Point pt);
	
	public Segment getSegmentClicked(Point pt);
	
	public List<Segment> getSegments();
	
	public boolean isSingleSegment();
}
