package com.mynetpcb;


import com.mynetpcb.circuit.dialog.panel.myNetPCBPanel;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

// Author:      Sergey Iliev
// Copyright:   (c) 2013 Sergey Iliev <sergei_iliev@yahoo.com>
// Licence:     myNetPCB licence

public class myNetPCB extends JFrame{

    private final myNetPCBPanel basePanel;
      
    public myNetPCB(String Caption) {
        super(Caption);        
        //***initialize configuration
        Configuration.Initilize(false);
        Configuration.get().read();
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        basePanel=new myNetPCBPanel(this.getRootPane(),this);
        
        this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    if(basePanel.getUnitComponent().getModel().isChanged()){                        
                        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(myNetPCB.this, "There is a changed circuit.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {                                       
                           myNetPCB.this.dispose();
                           System.exit(0);
                        }                      
                    }else{  
                      System.exit(0);
                    }    
                }
            });
        //***register on open listener
        this.addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    Circuit circuit=new Circuit(1600,800);
                    basePanel.getUnitComponent().getModel().Add(circuit);
                    basePanel.getUnitComponent().getModel().registerInitialState();
                    basePanel.getUnitComponent().getModel().setActiveUnit(circuit.getUUID());
                    basePanel.getUnitComponent().fireContainerEvent(new ContainerEvent(null, ContainerEvent.RENAME_CONTAINER));
                    basePanel.getUnitComponent().getModel().fireUnitEvent(new UnitEvent(circuit, UnitEvent.SELECT_UNIT));
                    basePanel.getUnitComponent().componentResized(null);
                    basePanel.getUnitComponent().revalidate();
                    
                }
            });

    }

    public static void main(String[] args) {
        Utilities.setUILookAndFeel();
        Utilities.setUIFont (new javax.swing.plaf.FontUIResource(new Font("Verdana",Font.PLAIN, 12)));
        myNetPCB myNetPCB = new myNetPCB("myNetPCB");
        //myNetPCB.setSize(900, 800);
        myNetPCB.setExtendedState(JFrame.MAXIMIZED_BOTH);
        myNetPCB.pack();
        myNetPCB.setLocationRelativeTo(null);
        myNetPCB.setVisible(true);
       
    }

}

