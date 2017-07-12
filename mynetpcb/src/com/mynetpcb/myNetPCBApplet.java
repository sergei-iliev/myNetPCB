package com.mynetpcb;


import com.mynetpcb.circuit.dialog.panel.myNetPCBPanel;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.remote.ReadConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JApplet;
import javax.swing.JOptionPane;


// Author:      Sergey Iliev
// Copyright:   (c) 2013 Sergey Iliev <sergei_iliev@yahoo.com>
// Licence:     myNetPCB licence

public class myNetPCBApplet  extends JApplet implements CommandListener {

    private  myNetPCBPanel basePanel;
    
    public void init() {
        super.init();
        Configuration.Initilize(true);
        
        Configuration.get().setHost(this.getCodeBase().getHost());
        
        if(getDocumentBase().getPort()!=-1){
            Configuration.get().setPort(getDocumentBase().getPort());
        }else{
            Configuration.get().setPort(80);
        }

        basePanel=new myNetPCBPanel(this.getRootPane(),this.getParentFrame());
        
        requestFocus();
        //****LOAD DEFAULT CIRCUIT
        Command reader=new ReadConnector(this,new RestParameterMap.ParameterBuilder("/circuits").addURI("DEMO").addURI("SolarControl").build(),Circuit.class);
        CommandExecutor.INSTANCE.addTask("ReadCircuit",reader);         
                
        
    }


    public void OnStart(Class sender) {
        DisabledGlassPane.block( this.getRootPane(),"Loading...");    
    }

    public void OnRecive(String result, Class sender) {
        //***circuit xml!
        if (sender.getSimpleName().equals("Circuit")) {
            basePanel.getUnitComponent().Clear();
            try {
                basePanel.getUnitComponent().getModel().Parse(result);
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
            //***unselect circuit
            basePanel.getUnitComponent().getModel().setActiveUnit(0);
            basePanel.getUnitComponent().getModel().getUnit().setSelected(false);
            basePanel.getUnitComponent().componentResized(null);
            //CircuitMgr.getInstance().connectSymbols(circuitComponent.getUnit());
            //***fire circuit selected
            basePanel.getUnitComponent().getModel().fireUnitEvent(new UnitEvent(basePanel.getUnitComponent().getModel().getUnit(), UnitEvent.SELECT_UNIT));
            basePanel.getUnitComponent().Repaint();
            basePanel.getUnitComponent().revalidate();
        }
    }

    public void OnFinish(Class sender) {
        DisabledGlassPane.unblock(this.getRootPane());  
    }

    public void OnError(String error) {
        DisabledGlassPane.unblock(this.getRootPane());
        JOptionPane.showMessageDialog(this, error, "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    public Frame getParentFrame() {
        Object parent = getParent();
        while (!(parent instanceof Frame)) {
            parent = ((Component)parent).getParent();
        }        
        return (Frame)parent;
    }

}
