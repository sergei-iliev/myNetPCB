package com.mynetpcb.ui;

import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.ui.board.BoardInternalFrame;
import com.mynetpcb.ui.footprint.FootprintInternalFrame;
import com.mynetpcb.ui.myNetPCB.MainFrameListener;
import com.mynetpcb.ui.symbol.SymbolInternalFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class MainPanel extends JPanel implements InternalFrameListener,MainFrameListener, ActionListener{
    
    private final JDesktopPane desktop;
    private JButton symbolButton,footprintButton,boardButton;
    private AbstractInternalFrame selectedFrame;
    
    public MainPanel(JDesktopPane desktop) {
        this.desktop=desktop;       
        setLayout(new GridBagLayout());      
        init();
    }
    private void init(){
        GridBagConstraints c = new GridBagConstraints();
         
        /*HEADER*/
        JPanel header=createHeader();
        
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 40;      //make this component tall
        c.weightx = 1.0;
        c.weighty=3;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        this.add(header, c);
         
        /*BODY*/
        JPanel body=createBody();         
  
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty=1;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        this.add(body, c);
         
        /*FOOTER*/
        JPanel footer=createFooter(); 
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 40;      //make this component tall
        c.weightx = 0.0;
        c.weighty=1;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 2;
        this.add(footer, c);        
    }
    private JPanel createFooter(){
        JPanel  forthRowPanel=new JPanel(new GridLayout(1, 1));
        forthRowPanel.setBackground(Color.white);
        return forthRowPanel;
    }
    private JPanel createBody(){
        JPanel  thirdRowPanel=new JPanel(new GridLayout(1,4));
        JPanel symbolsPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        symbolsPanel.setBorder(new EmptyBorder(0,0,0,0));
        symbolsPanel.setBackground(Color.white);  
        
        symbolButton=new JButton();
        symbolButton.addActionListener(this);
        symbolButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/symbol_icon.png"));
        symbolButton.setBackground(Color.white);
        symbolButton.setPreferredSize(new Dimension(130,130));
        symbolsPanel.add(symbolButton);
        thirdRowPanel.add(symbolsPanel);
        
        JPanel padsPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        padsPanel.setBorder(new EmptyBorder(0,0,0,0));
        padsPanel.setBackground(Color.white);        
        
        footprintButton=new JButton();
        footprintButton.addActionListener(this);
        footprintButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/footprint_icon.png"));
        footprintButton.setBackground(Color.white);
        footprintButton.setPreferredSize(new Dimension(130,130));
        padsPanel.add(footprintButton);  
        
        thirdRowPanel.add(padsPanel);      
        
        JPanel circuitPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        circuitPanel.setBorder(new EmptyBorder(0,0,0,0));
        circuitPanel.setBackground(Color.white);        
        
        JButton button=new JButton();
        button.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/circuit_icon.png"));
        button.setBackground(Color.white);
        button.setPreferredSize(new Dimension(130,130));
        circuitPanel.add(button);        
        thirdRowPanel.add(circuitPanel);  

        JPanel boardPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        boardPanel.setBorder(new EmptyBorder(0,0,0,0));
        boardPanel.setBackground(Color.white);        
        
        boardButton=new JButton();
        boardButton.addActionListener(this);
        boardButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/board_icon.png"));
        boardButton.setBackground(Color.white);
        boardButton.setPreferredSize(new Dimension(143,130));
        boardPanel.add(boardButton);        
        thirdRowPanel.add(boardPanel); 
        return thirdRowPanel;
    }
    private JPanel createHeader(){
        JPanel  firstRowPanel=new JPanel(new BorderLayout());
        firstRowPanel.setBackground(Color.white);
        
        JPanel panel=new JPanel();
        panel.setBackground(Color.white);
        firstRowPanel.add(panel,BorderLayout.WEST);
        
        JPanel background=new JPanel();
     
        background.setBorder(new EmptyBorder(90, 90, 90, 90));
        background.setBackground(Color.white);
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
  
        
        JLabel title=new JLabel("Create Design Innovate");
        title.setFont(title.getFont().deriveFont(50.0f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        background.add(title,BorderLayout.CENTER);
        
        title=new JLabel("Free and open-source schematic capture and pcb design tool");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(15.0f));
        background.add(title,BorderLayout.CENTER);
        
        
        firstRowPanel.add(background,BorderLayout.CENTER);
        
        panel=new JPanel();
        panel.setBackground(Color.white);
        firstRowPanel.add(panel,BorderLayout.EAST); 
        return firstRowPanel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        
        if(event.getSource()==footprintButton){
            selectedFrame=new FootprintInternalFrame();
            selectedFrame.setVisible(true); //necessary as of 1.3            
            desktop.removeAll();
            desktop.add(selectedFrame);
            selectedFrame.addInternalFrameListener(this);            
        }
        if(event.getSource()==boardButton){
            selectedFrame =new BoardInternalFrame();
            selectedFrame.setVisible(true); //necessary as of 1.3            
            desktop.removeAll();
            desktop.add(selectedFrame);
            selectedFrame.addInternalFrameListener(this);
        }   
        if(event.getSource()==symbolButton){
            selectedFrame =new SymbolInternalFrame();
            selectedFrame.setVisible(true); //necessary as of 1.3            
            desktop.removeAll();
            desktop.add(selectedFrame);
            selectedFrame.addInternalFrameListener(this);
        }         
    }
    
    @Override
    public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {
        // TODO Implement this method
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
        // TODO Implement this method
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
        selectedFrame=null;
        desktop.removeAll();
        desktop.add(this);
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
        selectedFrame=null;        
        desktop.removeAll();
        desktop.add(this);
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {

    }

    @Override
    public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
        // TODO Implement this method
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {
        // TODO Implement this method
    }

    @Override
    public void onMainFrameClose() {
            if(selectedFrame!=null&&selectedFrame.isChanged()){                        
                if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this, "There is a changed circuit.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {                                                           
                    System.exit(0);
                }                      
            }else{  
                   System.exit(0);
            }   
    }
}