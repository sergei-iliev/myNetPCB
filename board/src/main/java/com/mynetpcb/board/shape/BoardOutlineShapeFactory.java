package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.pad.shape.RoundRect;

public final class BoardOutlineShapeFactory {

	public static void createRect(Board board) {
		var line=new PCBLine((int)Grid.MM_TO_COORD(0.5),Layer.BOARD_OUTLINE_LAYER);
		line.add(0, 0);
		line.add(board.getWidth(),0);
		board.add(line);
		
		line=new PCBLine((int)Grid.MM_TO_COORD(0.5),Layer.BOARD_OUTLINE_LAYER);
		line.add(board.getWidth(), 0);
		line.add(board.getWidth(),board.getHeight());
		board.add(line);

		line=new PCBLine((int)Grid.MM_TO_COORD(0.5),Layer.BOARD_OUTLINE_LAYER);
		line.add(board.getWidth(), board.getHeight());
		line.add(0,board.getHeight());
		board.add(line);
		
		line=new PCBLine((int)Grid.MM_TO_COORD(0.5),Layer.BOARD_OUTLINE_LAYER);
		line.add(0, board.getHeight());
		line.add(0,0);
		board.add(line);

	}
	
	public static void createRoundRect(Board board) {
		var rect=new RoundRect(0, 0, board.getWidth(),board.getHeight(), (int)Grid.MM_TO_COORD(5), (int)Grid.MM_TO_COORD(0.5),Layer.BOARD_OUTLINE_LAYER);
	    for(var a:rect.getShape().arcs) {
	    	var arc=new PCBArc(a.pc.x,a.pc.y, a.r, a.startAngle, a.endAngle, (int)Grid.MM_TO_COORD(0.5),Layer.BOARD_OUTLINE_LAYER);
	        board.add(arc);
	    }
	    for(var s:rect.getShape().segments) {
	    	var line=new PCBLine((int)Grid.MM_TO_COORD(0.5),Layer.BOARD_OUTLINE_LAYER);
	    	line.add(s.ps.x,s.ps.y);
	    	line.add(s.pe.x,s.pe.y);
	        board.add(line);
	    }
	    
	}

	public static void createCircle(Board board) {
		double d=Math.min(board.getWidth(),board.getHeight());
		double x=board.getWidth()/2;
		double y=board.getHeight()/2;
		
		var circle=new PCBCircle(x, y, d/2, (int)Grid.MM_TO_COORD(0.5),Layer.BOARD_OUTLINE_LAYER);
		board.add(circle);
	}
	
}
