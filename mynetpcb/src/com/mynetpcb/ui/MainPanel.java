package com.mynetpcb.ui;

import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.ui.board.BoardInternalFrame;
import com.mynetpcb.ui.footprint.FootprintInternalFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class MainPanel extends JPanel implements InternalFrameListener, ActionListener{
    
    private final JDesktopPane desktop;
    private JButton footprintButton,boardButton;
    
    public MainPanel(JDesktopPane desktop) {
        this.desktop=desktop;       
        setLayout(new GridLayout(4,1));      
        init();
    }
    
    private void init(){
        //first row
        JPanel  firstRowPanel=new JPanel(new GridLayout(1, 1));
        firstRowPanel.setBackground(Color.white);
        //JPanel controlButtonsPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
        //JButton b=new JButton("Symbols");
        //controlButtonsPanel.add(b);
        //firstRowPanel.add(controlButtonsPanel);
        this.add(firstRowPanel);
        
        JPanel  secondRowPanel=new JPanel(new GridLayout(1, 1));
        secondRowPanel.setBackground(Color.white);
        this.add(secondRowPanel);
        
        //third row
        JPanel  thirdRowPanel=new JPanel(new GridLayout(1, 4));
        JPanel symbolsPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        symbolsPanel.setBorder(new EmptyBorder(0,0,0,0));
        symbolsPanel.setBackground(Color.white);  
        
        JButton button=new JButton();
        button.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/symbol_icon.png"));
        button.setBackground(Color.white);
        button.setPreferredSize(new Dimension(130,130));
        symbolsPanel.add(button);
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
        
        button=new JButton();
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
        
        this.add(thirdRowPanel);
        
        JPanel  forthRowPanel=new JPanel(new GridLayout(1, 1));
        forthRowPanel.setBackground(Color.white);
        this.add(forthRowPanel);
    }
    @Override
    public void actionPerformed(ActionEvent event) {
        
        if(event.getSource()==footprintButton){
            FootprintInternalFrame frame=new FootprintInternalFrame();
            frame.setVisible(true); //necessary as of 1.3            
            desktop.removeAll();
            desktop.add(frame);
            frame.addInternalFrameListener(this);
        }
        if(event.getSource()==boardButton){
            BoardInternalFrame frame=new BoardInternalFrame();
            frame.setVisible(true); //necessary as of 1.3            
            desktop.removeAll();
            desktop.add(frame);
            frame.addInternalFrameListener(this);
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
        desktop.removeAll();
        desktop.add(this);
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
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

}
