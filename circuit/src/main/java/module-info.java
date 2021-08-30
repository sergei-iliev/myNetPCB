module com.mynetpcb.circuit {
	requires  java.desktop;
	requires  com.mynetpcb.d2;
	requires  com.mynetpcb.core;
	requires  com.mynetpcb.symbol;	
	
	exports  com.mynetpcb.circuit.shape;	
	exports  com.mynetpcb.circuit.unit;	
	exports  com.mynetpcb.circuit.component;
	exports  com.mynetpcb.circuit.container;
	exports  com.mynetpcb.circuit.dialog.panel;
	exports  com.mynetpcb.circuit.dialog.print;
	exports  com.mynetpcb.circuit.dialog.save;
}