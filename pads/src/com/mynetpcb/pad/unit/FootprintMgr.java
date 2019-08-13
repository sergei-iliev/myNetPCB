package com.mynetpcb.pad.unit;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.pad.Packaging;
import com.mynetpcb.pad.dialog.FootprintLoadDialog;
import com.mynetpcb.pad.shape.Drill;
import com.mynetpcb.pad.shape.Pad;

import java.awt.Window;

import java.util.List;


public final class FootprintMgr extends UnitMgr<Footprint,Shape> {
    private static FootprintMgr footprintMgr;
    
    private FootprintMgr() {
    
    }
    
    public static synchronized FootprintMgr getInstance() {
      if(footprintMgr==null)
        footprintMgr=new FootprintMgr();
      return footprintMgr;
    }   
    
    public Pad createPad(Footprint footprint){
        int size=footprint.getShapes(Pad.class).size();
        Pad pad=null;
        if(size==0){
            pad=new Pad(0,0,Grid.MM_TO_COORD(1.52),Grid.MM_TO_COORD(1.52));
            pad.setDrill(new Drill(Grid.MM_TO_COORD(0.8),Grid.MM_TO_COORD(0.8)));
        }else{
            
            Pad last=((List<Pad>)footprint.<Pad>getShapes(Pad.class)).get(0);
            try {
                pad=last.clone();
                pad.setLocation(0, 0);
                //remove text
                pad.getChipText().getTextureByTag("number").setText("");
                pad.getChipText().getTextureByTag("netvalue").setText("");
            } catch (CloneNotSupportedException e) {
              e.printStackTrace();
            }
        }
        return pad;
    }

    /**
     *Avoid duplication of code when assigning a footprint to symbol/schsymbol
     * @param parent owning the dialog
     * @param packaging target assignment
     */
    public void assignPackage(Window parent,Packaging packaging){
         AbstractLoadDialog.Builder builder=new FootprintLoadDialog.Builder();
         AbstractLoadDialog footprintLoadDialog =builder.setWindow(parent).setCaption("Assign Footprint").setPackaging(packaging).build();

            
//        FootprintLoadDialog footprintLoadDialog =
//            new FootprintLoadDialog(parent,"Assign Footprint",packaging);
        
        footprintLoadDialog.pack();
        footprintLoadDialog.setLocationRelativeTo(null); //centers on screen
        footprintLoadDialog.setVisible(true);
        
        if(footprintLoadDialog.getSelectedModel()==null){
          footprintLoadDialog.dispose();  
          return;
        } 
        packaging.setFootprintLibrary(footprintLoadDialog.getSelectedModel().getLibraryName());
        packaging.setFootprintCategory(footprintLoadDialog.getSelectedModel().getCategoryName());
        packaging.setFootprintFileName(footprintLoadDialog.getSelectedModel().getFileName());
        packaging.setFootprintName(footprintLoadDialog.getSelectedModel().getUnit().getUnitName());
        
        footprintLoadDialog.dispose();           
    }
    
}


