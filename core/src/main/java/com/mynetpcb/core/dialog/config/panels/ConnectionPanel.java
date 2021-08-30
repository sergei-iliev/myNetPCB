package com.mynetpcb.core.dialog.config.panels;


import com.mynetpcb.core.capi.config.Configuration;

import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class ConnectionPanel extends AbstractPreferencePanel{
    
    private JComboBox workCombo;
    
    private JTextField portField;
    
    private JTextField  serverField;
    
    private JButton applyButton;
    
    public ConnectionPanel() {
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setAutoCreateGaps(true);
      layout.setAutoCreateContainerGaps(true);      
      workCombo=new JComboBox(new String[]{"OFFLINE","ONLINE"});
      JLabel label=new JLabel("Work type");
      JLabel label1=new JLabel("Server");
      JLabel label2=new JLabel("Port");         
      applyButton=new JButton("Apply");
      applyButton.addActionListener(this);
      serverField=new JTextField("127.0.0.1");
      portField=new JTextField("80");            
      
      GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
      hGroup.addGroup(layout.createParallelGroup().
                 addComponent(label).
                 addComponent(label1).addComponent(label2)                                            
      );
      
      hGroup.addGroup(layout.createParallelGroup().
                   addComponent(workCombo,80,80,80).
                   addComponent(serverField,80,80,80).addComponent(portField,60,60,60).addComponent(applyButton)
      );
      
      layout.setHorizontalGroup(hGroup);
  
      GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
      vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                    addComponent(label).addComponent(workCombo));
      
      vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                      addComponent(label1).addComponent(serverField) );
      
      vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                        addComponent(label2).addComponent(portField) );      
      
      vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).
                        addComponent(applyButton)); 
        
      layout.setVerticalGroup(vGroup);
    }

    public void Initialize() {
        Configuration.get().read();
      workCombo.setSelectedIndex(!Configuration.get().isIsOnline()?0:1); 
      serverField.setText(Configuration.get().getHost());
      portField.setText(String.valueOf(Configuration.get().getPort()));
    }

    public void Release() {
 
    }

    public String getRegisteredName() {
        return "connection";
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==applyButton){
            Configuration config = Configuration.get();   
            config.setIsOnline(workCombo.getSelectedIndex()!=0?true:false);
            config.setHost(serverField.getText());
            config.setPort(Integer.parseInt(portField.getText()));
            config.write();         
        }
    }
}
