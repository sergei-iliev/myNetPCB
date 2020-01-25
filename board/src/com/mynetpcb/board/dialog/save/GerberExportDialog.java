package com.mynetpcb.board.dialog.save;

//import com.mynetpcb.gerber.Excelon;
//import com.mynetpcb.gerber.Gerber;
import com.mynetpcb.board.dialog.print.BoardPrintDialog;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.gerber.Gerber;
import com.mynetpcb.gerber.capi.GerberServiceContext;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GerberExportDialog extends BoardPrintDialog implements ActionListener{
    
    public GerberExportDialog(Window owner, UnitComponent unitComponent) {
        super(owner, unitComponent, "Export "+unitComponent.getModel().getUnit().getUnitName());
        this.setSize(460, 220);
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

        JPanel main = new JPanel();
        main.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH ;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        main.add(createFootprintOptionsPanel(), c);
        
        base.add(main);
        
        return base;
    }
    
    private JPanel createFootprintOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, "Footprints");
        panel.setBorder(title);

        fmask = new JCheckBox("Footprint reference on silkscreen");
        panel.add(fmask);
        
        fsilk = new JCheckBox("Footprint value on silkscreen");
        panel.add(fsilk);
        
        bmask = new JCheckBox("Footprint shapes on silkscreen");
        panel.add(bmask);

        
        panel.setAlignmentY(JPanel.TOP_ALIGNMENT);
        return panel;
    }
    private GerberServiceContext buildServiceContext(){
        GerberServiceContext context=new GerberServiceContext();
        context.setParameter(GerberServiceContext.FOOTPRINT_REFERENCE_ON_SILKSCREEN, fmask.isSelected());
        context.setParameter(GerberServiceContext.FOOTPRINT_VALUE_ON_SILKSCREEN, fsilk.isSelected());
        context.setParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN, bmask.isSelected());
        return context;
    }
    private void export(String path,Board board) throws IOException{
        Gerber gerber=new Gerber(board); 
        GerberServiceContext context=buildServiceContext();
        gerber.build(context,path+"\\top.gbr",Layer.LAYER_FRONT);   
        gerber.build(context,path+"\\bottom.gbr",Layer.LAYER_BACK);  
        gerber.build(context,path+"\\top_silk.gbr",Layer.SILKSCREEN_LAYER_FRONT);
        gerber.build(context,path+"\\bottom_silk.gbr",Layer.SILKSCREEN_LAYER_BACK);
        
        //Excelon drill=new Excelon(board);
        //drill.build(context,path+"\\drill_npth.gbr", Layer.NPTH_LAYER_DRILL); 
        //drill.build(context,path+"\\drill_pth.gbr", Layer.PTH_LAYER_DRILL);
    }
}
