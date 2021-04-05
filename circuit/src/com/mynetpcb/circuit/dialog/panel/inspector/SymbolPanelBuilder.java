package com.mynetpcb.circuit.dialog.panel.inspector;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SymbolPanelBuilder extends AbstractPanelBuilder<Shape> {
    private JComboBox  chipUnitAlignmentCombo,  chipReferenceAlignmentCombo;

    private JTextField chipNameField, chipReferenceField, chipUnitField,packageNameField;

    public SymbolPanelBuilder(CircuitComponent component) {
	super(component,new GridLayout(6, 1));
	//***Chip Name
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Name");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipNameField = new JTextField("");
	chipNameField.addKeyListener(this);
	panel.add(chipNameField, BorderLayout.CENTER);
	layoutPanel.add(panel);
	//***Reference Name
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Reference");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipReferenceField = new JTextField("");
	chipReferenceField.addKeyListener(this);
	panel.add(chipReferenceField, BorderLayout.CENTER);
	layoutPanel.add(panel);

	//alignment
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Alignment");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipReferenceAlignmentCombo = new JComboBox(Texture.Alignment.values());
	chipReferenceAlignmentCombo.addActionListener(this);
	panel.add(chipReferenceAlignmentCombo, BorderLayout.CENTER);
	layoutPanel.add(panel);
        
	//***Unit
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Unit");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipUnitField = new JTextField();
        
        //add special greek characters
        JPopupMenu popup = new JPopupMenu();               
        chipUnitField.setComponentPopupMenu(popup);
        ActionListener listener=new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
             JMenuItem item=(JMenuItem)(actionEvent.getSource());       
             int j=chipUnitField.getCaretPosition();
             String str =(new StringBuffer(chipUnitField.getText())).insert(chipUnitField.getCaretPosition(),item.getActionCommand()).toString();
             chipUnitField.setText(str);             
             chipUnitField.setCaretPosition(j+1);
             Texture texture=((Textable)getTarget()).getTextureByTag("unit");   
             texture.setText(chipUnitField.getText());
             getComponent().Repaint();
            }
        };
         JMenuItem menuItem = new JMenuItem("\u03A9");
         menuItem.addActionListener(listener);
         popup.add(menuItem);
         menuItem = new JMenuItem("\u03B7");
         menuItem.addActionListener(listener);
         popup.add(menuItem);
         menuItem = new JMenuItem("\u03BC");
         menuItem.addActionListener(listener);
         popup.add(menuItem);
         menuItem = new JMenuItem("\u03C0");
         menuItem.addActionListener(listener);
         popup.add(menuItem);        
         menuItem = new JMenuItem("\u03C6");
         menuItem.addActionListener(listener);
         popup.add(menuItem);
         menuItem = new JMenuItem("\u03B8");
         menuItem.addActionListener(listener);
         popup.add(menuItem);
        
	chipUnitField.addKeyListener(this);
	panel.add(chipUnitField, BorderLayout.CENTER);
	layoutPanel.add(panel);

        
	//alignment
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Alignment");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipUnitAlignmentCombo = new JComboBox(Texture.Alignment.values());
	chipUnitAlignmentCombo.addActionListener(this);
	panel.add(chipUnitAlignmentCombo, BorderLayout.CENTER);
	layoutPanel.add(panel);

	//netlist part name
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Package Name");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	packageNameField = new JTextField();
	packageNameField.setEditable(false);
	panel.add(packageNameField, BorderLayout.CENTER);
	layoutPanel.add(panel);       
    }
    public void updateUI() {
        SCHSymbol symbol = (SCHSymbol)getTarget();
        chipNameField.setText(symbol.getDisplayName());
        //fix empty labels
    //      Rectangle r= symbol.calculateShape();
    //        if(symbol.getChipText().getTextureByTag("unit").isEmpty()){
    //            symbol.getChipText().getTextureByTag("unit").getAnchorPoint().setLocation(r.getX()-10,r.getY()-10);
    //        }
    //        if(symbol.getChipText().getTextureByTag("reference").isEmpty()){
    //            symbol.getChipText().getTextureByTag("reference").getAnchorPoint().setLocation(r.getX(),r.getY());
    //        }
    //
          chipUnitField.setText((symbol.getTextureByTag("unit").getText() ==
                                 null ? "" :
                                 symbol.getTextureByTag("unit").getText()));
          setSelectedItem(chipUnitAlignmentCombo,((SymbolFontTexture)symbol.getTextureByTag("unit")).getAlignment()); 
        
          chipReferenceField.setText((symbol.getTextureByTag("reference").getText() ==
                                      null ? "" :
                                      symbol.getTextureByTag("reference").getText()));
          setSelectedItem(chipReferenceAlignmentCombo,((SymbolFontTexture)symbol.getTextureByTag("reference")).getAlignment()); 
    //
    //        packageNameField.setText(symbol.getPackaging()==null?"": symbol.getPackaging().getFootprintName());
    //
            
        
    }
    public void actionPerformed(ActionEvent e) {
        SCHSymbol symbol = (SCHSymbol)getTarget();
	if(e.getSource()==chipReferenceAlignmentCombo){
           ((SymbolFontTexture)symbol.getTextureByTag("reference")).setAlignment((Texture.Alignment)chipReferenceAlignmentCombo.getSelectedItem());   
	    getComponent().Repaint();
	}  

	if(e.getSource()==chipUnitAlignmentCombo){
           ((SymbolFontTexture)symbol.getTextureByTag("unit")).setAlignment((Texture.Alignment)chipUnitAlignmentCombo.getSelectedItem());  
	    getComponent().Repaint();
	} 

    }

    @Override
    public void keyPressed(KeyEvent e) {    
	if (e.getKeyCode() != KeyEvent.VK_ENTER) {
	    return;
	}
        SCHSymbol symbol=(SCHSymbol)getTarget();
	//****chip handling
	if (e.getSource() == chipUnitField) {
	    Texture texture=symbol.getTextureByTag("unit");                     
	    texture.setText(chipUnitField.getText());
            
	    getComponent().Repaint();
	}
	if (e.getSource() == chipReferenceField) {
	    Texture texture=symbol.getTextureByTag("reference");                      
	    texture.setText(chipReferenceField.getText());          
	    getComponent().Repaint();
	}
	if (e.getSource() == chipNameField) {
	    symbol.setDisplayName(chipNameField.getText());
	    getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.RENAME_SHAPE));
	}

//        if (e.getSource() == packageNameField) {
//            ((Chip)getTarget()).setPackageName(packageNameField.getText());
//        }

    }


}

