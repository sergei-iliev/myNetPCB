package com.mynetpcb.board.dialog.save;

//import com.mynetpcb.gerber.Excelon;
//import com.mynetpcb.gerber.Gerber;
import com.mynetpcb.board.dialog.print.BoardPrintDialog;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.Excelon;
import com.mynetpcb.gerber.Gerber;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GerberExportDialog extends BoardPrintDialog implements ActionListener{
    
    public GerberExportDialog(Window owner, UnitComponent unitComponent) {
        super(owner, unitComponent, "Export "+unitComponent.getModel().getUnit().getUnitName());
        this.setSize(460, 270);
        this.printButton.setText("Export");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getActionCommand().equals("PRINT")) {
          if(targetFile.getText()==null||targetFile.getText().length()==0 ){
                JOptionPane.showMessageDialog(unitComponent.get().getDialogFrame().getParentFrame(),"Invalid folder selection", "Error",
                                              JOptionPane.ERROR_MESSAGE); 
                return;
          } 
          try{
            export(targetFile.getText(),(Board)unitComponent.get().getModel().getUnit());
            this.close();
          }catch(IOException ex){
              ex.printStackTrace();
          }
          return;
        }
        super.actionPerformed(e);
    }
    
    protected JPanel initDialogContent() {       
        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
        


        JPanel second=createFileSelectPanel("Folder",JFileChooser.DIRECTORIES_ONLY);
        base.add(second);

        base.add(new JLabel("EXPERIMENTAL/WORK IN PROGRESS"));  
        return base;
    }
    private void export(String path,Board board) throws IOException{
        Gerber gerber=new Gerber(board);              
        gerber.build(path+"\\top.gbr",Layer.LAYER_FRONT);   
        gerber.build(path+"\\bottom.gbr",Layer.LAYER_BACK);  
        gerber.build(path+"\\top_silk.gbr",Layer.SILKSCREEN_LAYER_FRONT);
        gerber.build(path+"\\bottom_silk.gbr",Layer.SILKSCREEN_LAYER_BACK);
        
        Excelon drill=new Excelon(board);
        drill.build(path+"\\drill_npth.gbr", Layer.NPTH_LAYER_DRILL); 
        drill.build(path+"\\drill_pth.gbr", Layer.PTH_LAYER_DRILL);
    }
}
