package com.mynetpcb.ui;


import com.mynetpcb.core.capi.config.Configuration;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

// Author:      Sergey Iliev
// Copyright:   (c) 2013 Sergey Iliev <sergei_iliev@yahoo.com>
// Licence:     myNetPCB licence

public class myNetPCB extends JFrame {
    
    public interface MainFrameListener{
        void onMainFrameClose();
    }
    
    private MainPanel mainPanel;
    private JDesktopPane desktop;
    
    public myNetPCB() {
        super("myNetPCB");
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);
        //Set up the GUI.
        JDesktopPane desktop = new JDesktopPane(); //a specialized layered pane        
        desktop.setLayout(new GridLayout(1, 1));
        setContentPane(desktop);        
        //setJMenuBar(createMenuBar());
        mainPanel= new MainPanel(desktop);        
        desktop.add(mainPanel);
        
        this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {              
                   mainPanel.onMainFrameClose(); 
                }
            });
    }
 
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
//        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
//        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }
 
 
//    //Create a new internal frame.
//    protected void createFrame() {
//        AbstractInternalFrame frame = new AbstractInternalFrame("John");
//        frame.setVisible(true); //necessary as of 1.3
//        //desktop.setLayout(new GridLayout(1,1));
//        desktop.removeAll();
//        desktop.add(frame);
//        frame.addInternalFrameListener(new InternalFrameListener(){
//            @Override
//            public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {
//                // TODO Implement this method
//            }
//
//            @Override
//            public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
//                // TODO Implement this method
//            }
//
//            @Override
//            public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
//                desktop.removeAll();
//                desktop.add(mainPanel);
//            }
//
//            @Override
//            public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
//                desktop.removeAll();
//                desktop.add(mainPanel);
//            }
//
//            @Override
//            public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {
//                // TODO Implement this method
//            }
//
//            @Override
//            public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
//                // TODO Implement this method
//            }
//
//            @Override
//            public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {
//                // TODO Implement this method
//            }
//        });
//        try {
//            frame.setMaximum(true);
//            frame.setSelected(true);
//        } catch (java.beans.PropertyVetoException e) {}
//    }
 
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Make sure we have nice window decorations.
                JFrame.setDefaultLookAndFeelDecorated(true);
                
                //***initialize configuration
                Configuration.Initilize(false);
                Configuration.get().read();
                
                //Create and set up the window.
                myNetPCB frame = new myNetPCB();
                
                //Display the window.
                frame.setVisible(true);
            }
        });
    }
   
}