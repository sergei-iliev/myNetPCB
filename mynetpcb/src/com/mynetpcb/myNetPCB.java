package com.mynetpcb;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

// Author:      Sergey Iliev
// Copyright:   (c) 2013 Sergey Iliev <sergei_iliev@yahoo.com>
// Licence:     myNetPCB licence

public class myNetPCB extends JFrame
                               implements ActionListener {
    JDesktopPane desktop;
 
    public myNetPCB() {
        super("myNetPCB");
 
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);
        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
  
        
        setContentPane(desktop);        
        //setJMenuBar(createMenuBar());
 
        desktop.setLayout(new GridLayout(1, 1));
        desktop.add(new MainPanel());
        
        createFrame(); //create first "window"
        //Make dragging a little faster but perhaps uglier.
        //desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }
 
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
 
        //Set up the lone menu.
        JMenu menu = new JMenu("Document");
        menu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu);
 
        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
        menuItem.addActionListener(this);
        menu.add(menuItem);
 
        //Set up the second menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(this);
        menu.add(menuItem);
 
        return menuBar;
    }
 
    //React to menu selections.
    public void actionPerformed(ActionEvent e) {
        if ("new".equals(e.getActionCommand())) { //new
            createFrame();
        } else { //quit
            quit();
        }
    }
 
    //Create a new internal frame.
    protected void createFrame() {
        AbstractInternalFrame frame = new AbstractInternalFrame("John");
        frame.setVisible(true); //necessary as of 1.3
        //desktop.setLayout(new GridLayout(1,1));
        desktop.removeAll();
        desktop.add(frame);
        frame.addInternalFrameListener(new InternalFrameListener(){
            @Override
            public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {
                // TODO Implement this method
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
                // TODO Implement this method
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
                desktop.removeAll();
                desktop.add(new MainPanel());
            }

            @Override
            public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
                // TODO Implement this method
            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {
                // TODO Implement this method
            }

            @Override
            public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
                // TODO Implement this method
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {
                // TODO Implement this method
            }
        });
        try {
            frame.setMaximum(true);
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
 
    //Quit the application.
    protected void quit() {
        System.exit(0);
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */

 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Make sure we have nice window decorations.
                JFrame.setDefaultLookAndFeelDecorated(true);
                
                //Create and set up the window.
                myNetPCB frame = new myNetPCB();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                //Display the window.
                frame.setVisible(true);
            }
        });
    }
    
    public static class MyInternalFrame extends JInternalFrame {
        static int openFrameCount = 0;
        static final int xOffset = 30, yOffset = 30;
     
        public MyInternalFrame() {
            super("Document #" + (++openFrameCount), 
                  true, //resizable
                  true, //closable
                  true, //maximizable
                  true);//iconifiable
     
            //...Create the GUI and put it in the window...
     
            //...Then set the window size or call pack...
            setSize(300,300);
     
            //Set the window's location.
            setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
        }
    }    
}