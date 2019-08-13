package com.mynetpcb.core.dialog.config;


import com.mynetpcb.core.dialog.config.panels.PreferenceInspector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalIconFactory;


public class PreferencesDialog extends JDialog implements ListSelectionListener{
   
    private JPanel basePanel = new JPanel();
    private JPanel bottomPanel=new JPanel(new FlowLayout());
    private JList preferenceList = new JList(new Object[]{"Connection","Repository","computer"});
    private PreferenceInspector preferenceInspector=new PreferenceInspector();
    
    public PreferencesDialog(Frame f, String Caption) {
        super(f, Caption, true);  
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(530,400));
        Init();
        this.setResizable(false);

    }
    private void Init(){
        basePanel.setLayout(new BorderLayout());
        preferenceList.setPreferredSize(new Dimension(150,500));
        //***create icons
        Map<Object, Icon> icons = new HashMap<Object, Icon>();
        icons.put("Connection",
                MetalIconFactory.getFileChooserDetailViewIcon());
        icons.put("Repository",
                MetalIconFactory.getTreeFolderIcon());
        icons.put("computer",
                MetalIconFactory.getTreeComputerIcon());        

        preferenceList.setCellRenderer(new IconListRenderer(icons));
        preferenceList.addListSelectionListener(this);
        //***Prefernce List         
        basePanel.add(preferenceList,BorderLayout.WEST);
        bottomPanel.setPreferredSize(new Dimension(500,50));
        preferenceList.setSelectedIndex(0);
        //***Prefernce Items
        preferenceInspector.setPreferredSize(new Dimension(400,400));
        basePanel.add(preferenceInspector,BorderLayout.CENTER);
        
        //***bottom
        JButton button=new JButton("Close");
        button.addActionListener(
        new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                  PreferencesDialog.this.dispose();
                }
               });
        bottomPanel.add(button);
                         
        basePanel.add(bottomPanel,BorderLayout.SOUTH);
        
        this.getContentPane().add(basePanel);
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {        
            preferenceInspector.build((String)preferenceList.getSelectedValue());
        }
    }
}
