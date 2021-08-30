package com.mynetpcb.circuit.dialog.print;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.dialog.print.PrintDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CircuitPrintDialog extends PrintDialog implements ChangeListener{
    
    private JRadioButton actualSize,fitToPageSize,customSize;    
    
    public CircuitPrintDialog(Window owner, UnitComponent unitComponent,String caption) {
        super(owner,unitComponent,caption,null);
        this.setSize(350, 200); 
    }
    
    @Override
    protected JPanel initDialogContent() {
        JPanel main = new JPanel();
        main.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH ;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy
            = 0;
        main.add(createColorPanel(), c);
                
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        main.add(createPagePanel(), c);
        return main;

    }

    protected JPanel createPagePanel() {
        JPanel sidegroup = new JPanel();
        sidegroup.setLayout(new BoxLayout(sidegroup, BoxLayout.Y_AXIS));
        Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, "Page");
        sidegroup.setBorder(title);

        ButtonGroup bgroup = new ButtonGroup();
        actualSize = new JRadioButton("Actual Size");
        fitToPageSize = new JRadioButton("Fit To Page");
        customSize = new JRadioButton("Custom Size");
        
        actualSize.setSelected(true);
        
        bgroup.add(actualSize);
        bgroup.add(fitToPageSize);
        bgroup.add(customSize);
        
        customSize.addChangeListener(this);  
            
        sidegroup.add(actualSize);
        sidegroup.add(fitToPageSize);
        sidegroup.add(customSize);
        
        sizeCB=new JComboBox<Double>(new Double[]{0.2,0.4,0.6,0.8,1.2,1.4,1.6,1.8,2.0});
        sizeCB.setEditable(true);
        sizeCB.setEnabled(false);
        sidegroup.add(sizeCB);
        
        sidegroup.setAlignmentY(JPanel.TOP_ALIGNMENT);
        
        return sidegroup;
    }
    @Override
    protected PrintContext createContext() {
        
        PrintContext context= super.createContext();
        context.setTag("circuit");
        //scale
        if(customSize.isSelected()){
            context.setCustomSizeRatio((Double)sizeCB.getSelectedItem());
        }else if(fitToPageSize.isSelected()){
            context.setCustomSizeRatio(-1);
        }
        
        return context;
    }


    @Override
    public void stateChanged(ChangeEvent e) {
       sizeCB.setEnabled(customSize.isSelected());                       
    }
}
