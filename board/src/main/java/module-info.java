module com.mynetpcb.board {
	requires  java.desktop;
	requires  com.mynetpcb.d2;
	requires  com.mynetpcb.core;
	requires  com.mynetpcb.pad;	
	requires  com.mynetpcb.gerber;
	
	
	exports  com.mynetpcb.board.shape;	
	exports  com.mynetpcb.board.unit;	
	exports  com.mynetpcb.board.component;
	exports  com.mynetpcb.board.container;
	exports  com.mynetpcb.board.dialog.panel;
	exports  com.mynetpcb.board.dialog.print;
	exports  com.mynetpcb.board.dialog.save;
}