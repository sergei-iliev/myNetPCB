package com.mynetpcb.pad.unit;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.pad.Packaging;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.pad.dialog.FootprintLoadDialog;
import com.mynetpcb.pad.shape.Pad;

import java.awt.Window;

import java.util.Collections;
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
    public void bringToFront(List<Shape> shapes,Shape target){
        
        Box box=target.getBoundingShape();
        int max=Integer.MIN_VALUE;
        for(Shape shape:shapes){
            if(shape==target){
                continue;
            }
            
            if(box.intersects(shape.getBoundingShape()) ){  //could be crossing
               max=Math.max(max,shapes.indexOf(shape));               
            }
        }
        if(max>Integer.MIN_VALUE){
            Collections.swap(shapes, max, shapes.indexOf(target));
        }
    }
    public void sendToBack(List<Shape> shapes,Shape target){
        
        Box box=target.getBoundingShape();
        int min=Integer.MAX_VALUE;
        for(Shape shape:shapes){
            if(shape==target){
                continue;
            }
            
            if(box.intersects(shape.getBoundingShape()) ){  //could be crossing
               min=Math.min(min,shapes.indexOf(shape));               
            }
        }
        if(min<Integer.MAX_VALUE){
            Collections.swap(shapes, min, shapes.indexOf(target));
        }
    }
    public Pad createPad(Footprint footprint){
        int size=footprint.getShapes(Pad.class).size();
        Pad pad=null;
        if(size==0){
            pad=new Pad(Grid.MM_TO_COORD(1.52),Grid.MM_TO_COORD(1.52));
        }else{
            
            Pad last=(footprint.<Pad>getShapes(Pad.class)).get(size-1);
            try {
                pad=last.clone();
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


