package com.mynetpcb.fontbuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MainPanel extends JPanel{
	private JPanel topPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	
	public MainPanel() {		
	  init();
	}
	private void init() {		
		setLayout(new BorderLayout());		
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton loadButton=new JButton("Load");
		topPanel.add(loadButton);
		
		
		centerPanel.setBackground(Color.black);
		this.add(topPanel, BorderLayout.NORTH);
		this.add(new FontGlyphComponent(), BorderLayout.CENTER);
	}
}
