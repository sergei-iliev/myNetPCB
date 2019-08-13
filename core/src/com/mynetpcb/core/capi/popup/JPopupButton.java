package com.mynetpcb.core.capi.popup;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class JPopupButton extends JButton{
    
    private final ActionListener listener;
    private final JPopupMenu popup;   
                                                         
    public JPopupButton(ActionListener listener) {
      this.listener = listener; 
      this.popup = new JPopupMenu();
      this.addMouseListener(new MouseAdapter() {
             public void mousePressed(MouseEvent e) {
                 popup.show(e.getComponent(), JPopupButton.this.getX() ,JPopupButton.this.getY() +JPopupButton.this.getBounds().height);
             }
      });
    }
    
    public JPopupButton addMenu(String name,String command){
        JMenuItem mi=new JMenuItem(name);mi.setActionCommand(command); mi.addActionListener(listener);
        this.popup.add(mi);
        return this;
    }
    public JPopupButton addRootMenu(String name,String command){
        JMenu mi=new JMenu(name);mi.setActionCommand(command);
        this.popup.add(mi);
        return this;
    }
    public JPopupButton addSeparator(){
        this.popup.addSeparator();
        return this;
    }
    public JPopupButton addSubMenu(String root,String subname,String subcommand){
        JMenu rootItem=null;
        for(int i=0;i<this.popup.getComponentCount();i++){
            if(this.popup.getComponent(i) instanceof JPopupMenu.Separator){
                continue;
            }
            JMenuItem item=(JMenuItem)this.popup.getComponent(i);
            if(item.getActionCommand().equals(root)){
              rootItem=(JMenu)item;  
            }
        }
        if(rootItem==null){
            throw new IllegalStateException("Root menu item with command name '"+root+"' not found");
        }
        
        JMenuItem mi=new JMenuItem(subname);mi.setActionCommand(subcommand); mi.addActionListener(listener);
        rootItem.add(mi);
        return this;
    }
    
    
}
