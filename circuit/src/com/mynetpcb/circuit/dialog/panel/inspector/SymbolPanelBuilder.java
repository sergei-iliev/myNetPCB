package com.mynetpcb.circuit.dialog.panel.inspector;


import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
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
    private JComboBox chipUnitOrientationCombo, chipUnitAlignmentCombo, chipReferenceOrientationCombo, chipReferenceAlignmentCombo;

    private JTextField chipNameField, chipReferenceField, chipUnitField,packageNameField;

    public SymbolPanelBuilder(CircuitComponent component) {
	super(component,new GridLayout(8, 1));
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

	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Text Orientation");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipReferenceOrientationCombo = new JComboBox(Text.Orientation.values());
	chipReferenceOrientationCombo.addActionListener(this);
	panel.add(chipReferenceOrientationCombo, BorderLayout.CENTER);
	layoutPanel.add(panel);
	//alignment
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Text Alignment");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipReferenceAlignmentCombo = new JComboBox();
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
             Texture texture=((Textable)getTarget()).getChipText().getTextureByTag("unit");   
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

	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Text Orientation");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipUnitOrientationCombo = new JComboBox(Text.Orientation.values());
	chipUnitOrientationCombo.addActionListener(this);
	panel.add(chipUnitOrientationCombo, BorderLayout.CENTER);
	layoutPanel.add(panel);
        
	//alignment
	panel = new JPanel();
	panel.setLayout(new BorderLayout());
	label = new JLabel("Text Alignment");
	label.setHorizontalAlignment(SwingConstants.CENTER);
	label.setPreferredSize(new Dimension(100, label.getHeight()));
	panel.add(label, BorderLayout.WEST);
	chipUnitAlignmentCombo = new JComboBox();
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

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == chipReferenceOrientationCombo) {
	    Texture text =
		((Textable)getTarget()).getChipText().getTextureByTag("reference");
	    text.setOrientation((text.getAlignment().getOrientation() == Text.Orientation. HORIZONTAL ? Text.Orientation.VERTICAL : Text.Orientation.HORIZONTAL));
	    validateAlignmentComboText(chipReferenceAlignmentCombo,text);
	    getComponent().Repaint();
	}
	if(e.getSource()==chipReferenceAlignmentCombo){
	    Texture text=((Textable)getTarget()).getChipText().getTextureByTag("reference");  
	    text.setAlignment(Text.Alignment.valueOf((String)chipReferenceAlignmentCombo.getSelectedItem()));
	    getComponent().Repaint();
	}  
	if (e.getSource() == chipUnitOrientationCombo) {
	    Texture text =
		((Textable)getTarget()).getChipText().getTextureByTag("unit");
	    text.setOrientation((text.getAlignment().getOrientation() == Text.Orientation.HORIZONTAL ? Text.Orientation.VERTICAL : Text.Orientation.HORIZONTAL));
	    validateAlignmentComboText(chipUnitAlignmentCombo,text);
	    getComponent().Repaint();
	}
	if(e.getSource()==chipUnitAlignmentCombo){
	    Texture text=((Textable)getTarget()).getChipText().getTextureByTag("unit");  
	    text.setAlignment(Text.Alignment.valueOf((String)chipUnitAlignmentCombo.getSelectedItem()));
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
	    Texture texture=symbol.getChipText().getTextureByTag("unit");                     
	    texture.setText(chipUnitField.getText());
            
	    getComponent().Repaint();
	}
	if (e.getSource() == chipReferenceField) {
	    Texture texture=symbol.getChipText().getTextureByTag("reference");                      
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

    public void updateUI() {
	SCHSymbol symbol = (SCHSymbol)getTarget();

	//fix empty labels
	Rectangle r= symbol.calculateShape();
        if(symbol.getChipText().getTextureByTag("unit").isEmpty()){
            symbol.getChipText().getTextureByTag("unit").getAnchorPoint().setLocation(r.getX()-10,r.getY()-10);
        }
        if(symbol.getChipText().getTextureByTag("reference").isEmpty()){
            symbol.getChipText().getTextureByTag("reference").getAnchorPoint().setLocation(r.getX(),r.getY());
        }
        
        chipUnitField.setText((symbol.getChipText().getTextureByTag("unit").getText() ==
			       null ? "" :
			       symbol.getChipText().getTextureByTag("unit").getText()));
	chipNameField.setText(symbol.getDisplayName());
	chipReferenceField.setText((symbol.getChipText().getTextureByTag("reference").getText() ==
				    null ? "" :
				    symbol.getChipText().getTextureByTag("reference").getText()));

        packageNameField.setText(symbol.getPackaging()==null?"": symbol.getPackaging().getFootprintName());
        
	validateAlignmentComboText(chipUnitAlignmentCombo,symbol.getChipText().getTextureByTag("unit")); 

	setSelectedIndex(chipUnitOrientationCombo,(symbol.getChipText().getTextureByTag("unit").getAlignment().getOrientation().ordinal()));

	setSelectedIndex(chipReferenceOrientationCombo,(symbol.getChipText().getTextureByTag("reference").getAlignment().getOrientation().ordinal()));
        
	validateAlignmentComboText(chipReferenceAlignmentCombo,symbol.getChipText().getTextureByTag("reference")); 
        
    }
}

