package com.mynetpcb.board.dialog.print;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.dialog.print.PrintDialog;
import com.mynetpcb.core.pad.Layer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class BoardPrintDialog extends PrintDialog {

   
    protected JCheckBox fsilk, fmask, bmask, bsilk, topcb, bottomcb;
    protected JCheckBox mirrorcb;
    
    public BoardPrintDialog(Window owner, UnitComponent unitComponent,String caption) {
        super(owner, unitComponent,caption);        
    }

    protected JPanel initDialogContent(){
        JPanel main = new JPanel();
        main.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH ;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        main.add(createSidePanel(), c);
        
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        main.add(createLayoutPanel(), c);
        

        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        main.add(createColorPanel(), c);
        

        c.weightx = 0.5;
        c.gridx = 3;
        c.gridy = 0;
        main.add(createPagePanel(), c);
        
        return main;
    }
    
    private JPanel createSidePanel() {
        JPanel sidegroup = new JPanel();
        sidegroup.setLayout(new BoxLayout(sidegroup, BoxLayout.Y_AXIS));
        Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, "Copper Side");
        sidegroup.setBorder(title);

        topcb = new JCheckBox("Top       ");
        sidegroup.add(topcb);
        topcb.setSelected(true);
        
        bottomcb = new JCheckBox("Bottom    ");
        sidegroup.add(bottomcb);
        sidegroup.setAlignmentY(JPanel.TOP_ALIGNMENT);
        return sidegroup;
    }

    private JPanel createLayoutPanel() {
        JPanel sidegroup = new JPanel();
        sidegroup.setLayout(new BoxLayout(sidegroup, BoxLayout.Y_AXIS));
        Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, "Layout");
        sidegroup.setBorder(title);

        fsilk = new JCheckBox("Top Silkscreen");
        sidegroup.add(fsilk);
        fsilk.setSelected(true);
        
        fmask = new JCheckBox("Top Soldermask");
        sidegroup.add(fmask);
        fmask.setSelected(true);
        
        bmask = new JCheckBox("Bottom Soldermask");
        sidegroup.add(bmask);

        bsilk = new JCheckBox("Bottom Silkscreen");
        sidegroup.add(bsilk);
        sidegroup.setAlignmentY(JPanel.TOP_ALIGNMENT);
        return sidegroup;
    }

    protected JPanel createPagePanel() {
        JPanel sidegroup = new JPanel();
        sidegroup.setLayout(new BoxLayout(sidegroup, BoxLayout.Y_AXIS));
        Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, "Page");
        sidegroup.setBorder(title);

        mirrorcb = new JCheckBox("Mirror");
        sidegroup.add(mirrorcb);

        sidegroup.setAlignmentY(JPanel.TOP_ALIGNMENT);
        return sidegroup;
    }
    
    @Override
    protected PrintContext createContext() {
        
        PrintContext context= super.createContext();
        context.setTag("board");
        
        int layermaskid=0;
        if(topcb.isSelected()){
            layermaskid|=Layer.LAYER_FRONT;
        }            
        if(bottomcb.isSelected()){
            layermaskid|=Layer.LAYER_BACK;
        }
        if(fsilk.isSelected()){
            layermaskid|=Layer.SILKSCREEN_LAYER_FRONT;
        }
        if(bsilk.isSelected()){
            layermaskid|=Layer.SILKSCREEN_LAYER_BACK;
        }
        if(fmask.isSelected()){
            layermaskid|=Layer.SOLDERMASK_LAYER_FRONT;
        }
        if(bmask.isSelected()){
            layermaskid|=Layer.SOLDERMASK_LAYER_BACK;
        }
        
        
        context.setLayermaskId(layermaskid);
        context.setIsMirrored(mirrorcb.isSelected());            

        return context;
    }
   

    
}
