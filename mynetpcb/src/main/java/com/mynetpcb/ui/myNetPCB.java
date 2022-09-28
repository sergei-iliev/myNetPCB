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
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/circuit.png")));
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0,
                  screenSize.width,
                  screenSize.height-inset);
        //Set up the GUI.
        JDesktopPane desktop = new JDesktopPane(); //a specialized layered pane        
        desktop.setLayout(new GridLayout(1, 1));
        setContentPane(desktop);        
        //setJMenuBar(createMenuBar());
        mainPanel= new MainPanel(this,desktop);        
        desktop.add(mainPanel);
        
        this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {              
                   mainPanel.onMainFrameClose(); 
                }
            });
    }
 
 
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