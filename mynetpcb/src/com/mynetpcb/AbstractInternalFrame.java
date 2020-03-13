package com.mynetpcb;

import javax.swing.JInternalFrame;

public class AbstractInternalFrame extends JInternalFrame{
    
    public AbstractInternalFrame(String name) {
        super(name,
              true, //resizable
              true, //closable
              true, //maximizable
              true);//


    }
    
    
}
