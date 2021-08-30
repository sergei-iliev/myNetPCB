module com.mynetpcb.pad {
	requires  java.desktop; 
	requires  com.mynetpcb.d2;
	requires  com.mynetpcb.core;	 
	
	exports com.mynetpcb.pad.component;
	exports com.mynetpcb.pad.container;
	exports com.mynetpcb.pad.event;
	exports com.mynetpcb.pad.dialog;
	exports com.mynetpcb.pad.dialog.save;
	exports com.mynetpcb.pad.dialog.panel;
	exports com.mynetpcb.pad.dialog.panel.inspector;
	exports com.mynetpcb.pad.shape;
	exports com.mynetpcb.pad.shape.pad;
	exports com.mynetpcb.pad.unit;
}