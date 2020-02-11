package com.mynetpcb;

import com.mynetpcb.core.utils.Utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainPanel extends JPanel{
    public MainPanel() {
        setLayout(new GridLayout(4,1));
        //this.setBorder(new EmptyBorder(0,0,0,0));
        init();
    }
    
    private void init(){
        //first row
        JPanel  firstRowPanel=new JPanel(new GridLayout(1, 1));
        JPanel controlButtonsPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton b=new JButton("Symbols");
        controlButtonsPanel.add(b);
        firstRowPanel.add(controlButtonsPanel);
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
        
        button=new JButton();
        button.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/footprint_icon.png"));
        button.setBackground(Color.white);
        button.setPreferredSize(new Dimension(130,130));
        padsPanel.add(button);        
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
        
        button=new JButton();
        button.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/board_icon.png"));
        button.setBackground(Color.white);
        button.setPreferredSize(new Dimension(143,130));
        boardPanel.add(button);        
        thirdRowPanel.add(boardPanel);          
        
        this.add(thirdRowPanel);
        
        JPanel  forthRowPanel=new JPanel(new GridLayout(1, 1));
        forthRowPanel.setBackground(Color.white);
        this.add(forthRowPanel);
    }
}
