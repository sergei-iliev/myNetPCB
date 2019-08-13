package com.mynetpcb.core.dialog.config.panels;


import com.mynetpcb.core.capi.config.Configuration;

import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.plaf.metal.MetalIconFactory;


public class RepositoryPanel extends AbstractPreferencePanel{
    
    
    private JTextField workspaceField;
    
    private JTextField  libraryField;
        
    private JButton applyButton,chooseFolder1,chooseFolder2;
    
    public RepositoryPanel() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        //layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);  
        JLabel label1=new JLabel("Workspace");
        JLabel label2=new JLabel("Libraries"); 
        workspaceField=new JTextField("C:\\workspace");
        libraryField=new JTextField("C:\\libraries");        
        applyButton=new JButton("Apply");
        applyButton.addActionListener(this);
        chooseFolder1=new JButton();
        chooseFolder1.addActionListener(this);        
        chooseFolder1.setIcon(MetalIconFactory.getFileChooserDetailViewIcon());
        chooseFolder2=new JButton();
        chooseFolder2.addActionListener(this);
        chooseFolder2.setIcon(MetalIconFactory.getFileChooserDetailViewIcon());
        
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGroup(layout.createParallelGroup().
                   addComponent(label1).
                   addComponent(label2)                                           
        ).addGap(5);
        
        hGroup.addGroup(layout.createParallelGroup().
                     addComponent(libraryField,250,250,250).
                     addComponent(workspaceField,250,250,250).addComponent(applyButton)    
                        
        );
        
        hGroup.addGroup(layout.createParallelGroup().
                     addComponent(chooseFolder1,20,20,20).
                     addComponent(chooseFolder2,20,20,20)   
                        
        );
        
        layout.setHorizontalGroup(hGroup);
        
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                        addComponent(label2).addComponent(libraryField).addComponent(chooseFolder2,20,20,20)).addGap(5);


        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).
                      addComponent(label1).addComponent(workspaceField).addComponent(chooseFolder1,20,20,20)     
                      );
                     
        
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).
                          addComponent(applyButton)); 
          
        layout.setVerticalGroup(vGroup);    
    }

    public void Initialize() {
        Configuration config = Configuration.get();       
        config.read();   
        workspaceField.setText(config.getWorkspaceRoot().toString());
        libraryField.setText(config.getLibraryRoot().toString());   
    }

    public void Release() {
    }

    public String getRegisteredName() {
        return "repository";
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==chooseFolder1){
            JFileChooser fc=new JFileChooser();
            fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);
                   if (returnVal == JFileChooser.APPROVE_OPTION) {                      
                       workspaceField.setText(fc.getSelectedFile().getAbsolutePath());  
                   }          
          return;  
        }
        if(e.getSource()==chooseFolder2){
            JFileChooser fc=new JFileChooser();
            fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(this);
                   if (returnVal == JFileChooser.APPROVE_OPTION) {                      
                       libraryField.setText(fc.getSelectedFile().getAbsolutePath());  
                   }            
          return;  
        }
        if(e.getSource()==applyButton){
          Configuration config = Configuration.get();          
          config.setWorkspaceRoot(workspaceField.getText());
          config.setLibraryRoot(libraryField.getText());
          config.write();
          config.read();
          return;  
        }
        
    }
}
