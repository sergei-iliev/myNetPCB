module com.mynetpcb.symbol {
	requires  java.desktop; 
	requires  com.mynetpcb.d2;
	requires  com.mynetpcb.core;	
    requires com.mynetpcb.pad;	
	
	exports com.mynetpcb.symbol.component;
	exports com.mynetpcb.symbol.container;
	exports com.mynetpcb.symbol.dialog;
	exports com.mynetpcb.symbol.dialog.save;
	exports com.mynetpcb.symbol.dialog.panel;
	exports com.mynetpcb.symbol.dialog.panel.inspector;
	exports com.mynetpcb.symbol.shape;
	exports com.mynetpcb.symbol.unit;
	exports com.mynetpcb.symbol.event;
}