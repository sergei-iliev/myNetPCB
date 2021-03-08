package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class BoardPanelBuilder extends AbstractPanelBuilder< Shape> {

    private JTextField moduleNameField, widthField, heightField;

    private JComboBox unitsCombo, gridCombo;


    public BoardPanelBuilder(BoardComponent component) {
        super(component, new GridLayout(8, 1));        
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Board name");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        moduleNameField = new JTextField();
        moduleNameField.addKeyListener(this);
        panel.add(moduleNameField, BorderLayout.CENTER);
        layoutPanel.add(panel);
        //layer
//        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
//        label=new JLabel("Active Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
//        layerCombo=new JComboBox(Layer.BOARD_LAYERS);layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
//        layoutPanel.add(panel);
        //***Widht
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Width");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        widthField = new JTextField("");
        widthField.addKeyListener(this);
        panel.add(widthField, BorderLayout.CENTER);
        layoutPanel.add(panel);
        //***Height
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Height");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        heightField = new JTextField("");
        heightField.addKeyListener(this);
        panel.add(heightField, BorderLayout.CENTER);
        layoutPanel.add(panel);
        //***units
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Units");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        unitsCombo = new JComboBox(Grid.Units.values());
        unitsCombo.addActionListener(this);
        panel.add(unitsCombo, BorderLayout.CENTER);
        unitsCombo.setEnabled(false);
        layoutPanel.add(panel);
        //***grid
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Grid Raster");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        gridCombo = new JComboBox(new Double[] {
                                  2.54, 1.27, 0.635, 0.508, 0.254, 0.127, 0.0635, 0.0508, 0.0254, 0.0127,0.00508,0.00254,5.0, 2.5, 1.0,
                                  0.5, 0.25, 0.8, 0.2, 0.1, 0.05, 0.025, 0.01
            });
        gridCombo.addActionListener(this);
        panel.add(gridCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);

        //***Coordinate X
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Origin X");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        originX = new JTextField("");
        originX.addKeyListener(this);
        panel.add(originX, BorderLayout.CENTER);
        layoutPanel.add(panel);
        //***Coordinate Y
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Origin Y");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        originY = new JTextField("");
        originY.addKeyListener(this);
        panel.add(originY, BorderLayout.CENTER);
        layoutPanel.add(panel);
    }

    @Override
    public void updateUI() {
        if(getComponent().getModel().getUnit()==null){
          return;
        }
        widthField.setText(String.valueOf(Grid.COORD_TO_MM( getComponent().getModel().getUnit().getWidth())));
        
        heightField.setText(String.valueOf(Grid.COORD_TO_MM( getComponent().getModel().getUnit().getHeight()))); 
        
        moduleNameField.setText(getComponent().getModel().getUnit().getUnitName()); 
        

        setSelectedItem(gridCombo,getComponent().getModel().getUnit().getGrid().getGridValue());
        
        if(getComponent().getModel().getUnit().getCoordinateSystem()!=null){
          originX.setText(String.valueOf(Grid.COORD_TO_MM(getComponent().getModel().getUnit().getCoordinateSystem().getOrigin().x)));
          originY.setText(String.valueOf(Grid.COORD_TO_MM(getComponent().getModel().getUnit().getCoordinateSystem().getOrigin().y)));
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==gridCombo){
            getComponent().getModel().getUnit().getGrid().setGridValue((Double)gridCombo.getSelectedItem());
        }
        
        getComponent().Repaint();

    }
    
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        
        if(e.getSource()==this.moduleNameField){
           getComponent().getModel().getUnit().setUnitName(moduleNameField.getText());  
           getComponent().getModel().fireUnitEvent(new UnitEvent(getComponent().getModel().getUnit(), UnitEvent.RENAME_UNIT));
        }
        if(e.getSource()==this.heightField||e.getSource()==this.widthField){            
            getComponent().getModel().getUnit().setSize((int)Grid.MM_TO_COORD(Double.parseDouble(widthField.getText())),(int)Grid.MM_TO_COORD(Double.parseDouble(heightField.getText())));
            //***refresh scrollbars
            getComponent().componentResized(null);                
            getComponent().Repaint();
        }
        if(e.getSource()==originX||e.getSource()==originY){
            getComponent().getModel().getUnit().getCoordinateSystem().reset(Grid.MM_TO_COORD(Double.parseDouble(originX.getText())),Grid.MM_TO_COORD(Double.parseDouble(originY.getText())));   
            getComponent().Repaint();
        }
        

    }
}
