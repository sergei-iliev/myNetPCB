package com.mynetpcb.board.dialog.save;

import com.mynetpcb.board.dialog.print.BoardPrintDialog;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.print.PrintContext;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BoardImageExportDialog extends BoardPrintDialog implements ActionListener{
    
    public BoardImageExportDialog(Frame owner, UnitComponent unitComponent) {
        super(owner, unitComponent,  "Export "+unitComponent.getModel().getUnit().getUnitName());
        this.setSize(460, 270);
        this.printButton.setText("Export");
    }

    protected JPanel initDialogContent() {
        JPanel panel = super.initDialogContent();
        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
        base.add(panel);
    

        JPanel first =createScalePanel();      
        DefaultComboBoxModel model=new DefaultComboBoxModel(new Integer[]{72,100,200,300,400,500,600});
        sizeCB.setModel(model);
        sizeCB.setSelectedItem(500);
        base.add(first);


        JPanel second=createFileSelectPanel("Name",JFileChooser.FILES_ONLY);
        base.add(second);


        return base;
    }
    
    @Override
    protected PrintContext createContext() {
       PrintContext context=super.createContext();
       context.setCustomSizeRatio((Integer)sizeCB.getSelectedItem());
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
          unitComponent.get().export(targetFile.getText(), createContext());
          return;
        }
        super.actionPerformed(e);
    }

}
