package com.mynetpcb.core.dialog.config.panels;

import java.awt.event.ActionListener;

import javax.swing.JPanel;


public abstract class AbstractPreferencePanel extends JPanel implements ActionListener{

  public abstract String getRegisteredName();
  
  public abstract void Initialize();
  
  public abstract void Release();  
  
}
