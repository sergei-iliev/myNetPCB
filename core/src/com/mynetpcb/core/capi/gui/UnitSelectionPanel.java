package com.mynetpcb.core.capi.gui;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;


public class UnitSelectionPanel extends JPanel implements MouseListener{
    
    private final UnitSelectionGrid selectionGrid;
        
    public UnitSelectionPanel() {
        selectionGrid=new UnitSelectionGrid(null);
        this.addMouseListener(this);
    }
    public UnitSelectionGrid getSelectionGrid(){
        return this.selectionGrid;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   
        Graphics2D g2 = (Graphics2D)g;
        selectionGrid.Paint(g2);
        g2.dispose();
    } 
    @Override
    public void setSize(int width,int height){
        super.setSize(width, height);
        super.setPreferredSize(new Dimension(width,height));              
    }
    
    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        selectionGrid.setEnabled(enabled); 
    }
    public void buildSelectionGrid(){
       selectionGrid.build();
       this.setSize(selectionGrid.getWidth(), selectionGrid.getHeight());
    }
    public void Clear(){
      setSize(1, 1); 
      selectionGrid.Clear();  
    }
    public void Release(){
      this.Clear();
      selectionGrid.Release(); 
      this.removeMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!isEnabled()) return;
        if(selectionGrid.processClick(e)){ 
          this.repaint();  
        }     
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }
}
