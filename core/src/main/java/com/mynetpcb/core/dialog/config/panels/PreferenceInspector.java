package com.mynetpcb.core.dialog.config.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;


public class PreferenceInspector extends JPanel{
    
    private AbstractPreferencePanel panel; 
    
    public PreferenceInspector() {
        this.setLayout(new BorderLayout());       
    }
    
    private void Initialize(){
      if(panel!=null)
        panel.Initialize();  
    }
    private void Release(){
      if(panel!=null)
        panel.Release();   
    }
    
    public void build(String preferencePanelName){
        if(preferencePanelName==null||(panel!=null&&panel.getRegisteredName().equals(preferencePanelName)))
           return;  
        
        if(preferencePanelName.equalsIgnoreCase("connection")){        
            //***release
            Release();
            this.removeAll();     
            panel=new ConnectionPanel();
            //***init
            Initialize();
            this.add(panel,BorderLayout.CENTER);
            this.updateUI();
        }    
        if(preferencePanelName.equalsIgnoreCase("repository")){        
            //***release
            Release();
            this.removeAll();     
            panel=new RepositoryPanel();
            //***init
            Initialize();
            this.add(panel,BorderLayout.CENTER);
            this.updateUI();
        }          
    }
    
    
    
}
