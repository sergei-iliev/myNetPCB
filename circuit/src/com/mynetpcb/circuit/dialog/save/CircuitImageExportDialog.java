package com.mynetpcb.circuit.dialog.save;

import com.mynetpcb.circuit.dialog.print.CircuitPrintDialog;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.print.PrintContext;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CircuitImageExportDialog extends CircuitPrintDialog implements ActionListener{
    
    public CircuitImageExportDialog(Window owner, UnitComponent unitComponent) {
        super(owner, unitComponent, "Export");
        this.setSize(360, 250);
        this.printButton.setText("Export");
    }
    
    @Override
    protected JPanel initDialogContent() {
        JPanel top = new JPanel();
        top.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH ;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy
            = 0;
        top.add(createColorPanel(), c);
                
        
        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
        base.add(top);
    
         
        JPanel first =createScalePanel();      
        DefaultComboBoxModel model=new DefaultComboBoxModel(new Double[]{0.4,0.6,0.8,1d,1.2,1.4,1.6,1.8,2d});
        sizeCB.setModel(model);
        sizeCB.setSelectedItem(1.2d);
        base.add(first);


        JPanel second=createFileSelectPanel("Name",JFileChooser.FILES_ONLY);
        base.add(second);

        return base;
    }    
    @Override
    protected PrintContext createContext() {
        
        PrintContext context=new PrintContext();
        context.setIsBlackAndWhite(bwrb.isSelected()); 
        context.setTag("circuit");
        //scale        
        context.setCustomSizeRatio((Double)sizeCB.getSelectedItem());                
        return context;
    }
    @Override
    public void actionPerformed(ActionEvent e) {        
        
        if (e.getActionCommand().equals("PRINT")) {
          if(targetFile.getText()==null||targetFile.getText().length()<3 ){
                JOptionPane.showMessageDialog(unitComponent.get().getDialogFrame().getParentFrame(),"Invalid file selection", "Error",
                                              JOptionPane.ERROR_MESSAGE); 
                return;
          }           
          unitComponent.get().Export(targetFile.getText(), createContext());
          this.close();
          return;
        }
        super.actionPerformed(e);
    }
}
