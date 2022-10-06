package com.mynetpcb.fontbuilder;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;


public class FontBuilder extends JFrame{
	 public FontBuilder() {	        
	        super("Font Builder");
	        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        	       	      
	                  
	        //Set up the GUI.
	        JDesktopPane desktop = new JDesktopPane(); //a specialized layered pane        
	        desktop.setLayout(new GridLayout(1, 1));
	        setContentPane(desktop);        
	       	               
	        desktop.add(new MainPanel());
	        setPreferredSize(new Dimension(800, 600));
	        pack();
	        setLocationRelativeTo(null);
	        setVisible(true);
	  }
	  public static void main(String[] args) {	       
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                new FontBuilder();
	            }
	        });
	    }
}
