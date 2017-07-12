package com.mynetpcb.board.dialog.panel;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.core.board.CompositeLayerable;
import com.mynetpcb.core.pad.Layer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LayersPanel extends JPanel implements ActionListener,ChangeListener{
    
    private final BoardComponent boardComponent;
    private JCheckBox FCU,BCU,FSILKS,BSILKS,BMASK,FMASK;
    private JComboBox layerCombo;
    
    public LayersPanel(BoardComponent boardComponent) {
        this.boardComponent = boardComponent;
        setLayout(new BorderLayout());
        
        JPanel basePanel=new JPanel(new GridLayout(0,1));
        
        JPanel panel=new JPanel(); panel.setLayout(new BorderLayout()); panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        JLabel label=new JLabel("Top Silkscreen"); label.setPreferredSize(new Dimension(150,label.getHeight())); panel.add(label,BorderLayout.WEST);
        FSILKS=new JCheckBox();FSILKS.addActionListener(this); FSILKS.setName(String.valueOf(Layer.SILKSCREEN_LAYER_FRONT)); FSILKS.setBackground(Color.cyan); panel.add(FSILKS,BorderLayout.EAST);
        basePanel.add(panel); 
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        label=new JLabel("Top Soldermask"); panel.add(label,BorderLayout.WEST);
        FMASK=new JCheckBox();FMASK.addActionListener(this); FMASK.setName(String.valueOf(Layer.SOLDERMASK_LAYER_FRONT));FMASK.setBackground(Color.magenta); panel.add(FMASK,BorderLayout.EAST);
        basePanel.add(panel); 
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        label=new JLabel("Top Copper"); panel.add(label,BorderLayout.WEST);
        FCU=new JCheckBox();FCU.addActionListener(this); FCU.setName(String.valueOf(Layer.LAYER_FRONT));FCU.setBackground(Color.red); panel.add(FCU,BorderLayout.EAST);
        basePanel.add(panel); 
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        label=new JLabel("Bottom Copper"); panel.add(label,BorderLayout.WEST);
        BCU=new JCheckBox();BCU.addActionListener(this); BCU.setName(String.valueOf(Layer.LAYER_BACK));BCU.setBackground(Color.green); panel.add(BCU,BorderLayout.EAST);
        basePanel.add(panel); 
        
        panel=new JPanel(); panel.setLayout(new BorderLayout());panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8)); 
        label=new JLabel("Bottom Soldermask"); panel.add(label,BorderLayout.WEST);
        BMASK=new JCheckBox();BMASK.addActionListener(this); BMASK.setName(String.valueOf(Layer.SOLDERMASK_LAYER_BACK));BMASK.setBackground(new Color(128,128,0)); panel.add(BMASK,BorderLayout.EAST);
        basePanel.add(panel); 
        
        panel=new JPanel(); panel.setLayout(new BorderLayout());panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8)); 
        label=new JLabel("Bottom Silkscreen"); panel.add(label,BorderLayout.WEST);
        BSILKS=new JCheckBox();BSILKS.addActionListener(this); BSILKS.setName(String.valueOf(Layer.SILKSCREEN_LAYER_BACK));BSILKS.setBackground(Color.magenta); panel.add(BSILKS,BorderLayout.EAST);
        basePanel.add(panel); 
        
        panel=new JPanel(); panel.setLayout(new BorderLayout());panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8)); 
        label=new JLabel("Active Side"); panel.add(label,BorderLayout.WEST);
        layerCombo=new JComboBox(Layer.Side.values());layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.EAST);                
        basePanel.add(panel); 
        
        this.add(basePanel, BorderLayout.NORTH);
    }

    public void updateUI(){
       if(boardComponent==null||boardComponent.getModel().getUnit()==null){
            return;
       }
       CompositeLayerable layerable=boardComponent.getModel().getUnit();
       
       FSILKS.setSelected(layerable.isLayerVisible(Layer.SILKSCREEN_LAYER_FRONT));    
       FMASK.setSelected(layerable.isLayerVisible(Layer.SOLDERMASK_LAYER_FRONT));    
       FCU.setSelected(layerable.isLayerVisible(Layer.LAYER_FRONT));    
       BCU.setSelected(layerable.isLayerVisible(Layer.LAYER_BACK));    
       BMASK.setSelected(layerable.isLayerVisible(Layer.SOLDERMASK_LAYER_BACK));    
       BSILKS.setSelected(layerable.isLayerVisible(Layer.SILKSCREEN_LAYER_BACK));    

       layerCombo.removeActionListener(this); 
       layerCombo.setSelectedItem(boardComponent.getModel().getUnit().getActiveSide());
       layerCombo.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(boardComponent==null||boardComponent.getModel().getUnit()==null){
             return;
        }
        if(e.getSource()==layerCombo){
             boardComponent.getModel().getUnit().setActiveSide((Layer.Side)layerCombo.getSelectedItem());           
        }else{
            String id=((JCheckBox)e.getSource()).getName();          
            CompositeLayerable layerable=boardComponent.getModel().getUnit();
            layerable.setLayerVisible(Integer.parseInt(id), ((JCheckBox)e.getSource()).isSelected());            
        }
        boardComponent.Repaint();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        if(index==2){
            updateUI(); 
        }
    }
}
