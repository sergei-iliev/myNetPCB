package com.mynetpcb.fontbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class FontGlyphComponent extends JComponent {

	public FontGlyphComponent() {
	   
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(250, 200);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.RED);
	    g.fillRect(0,0,800,400);
		// Draw Text
		g.drawString("This is my custom Panel!", 10, 20);
	}
}
