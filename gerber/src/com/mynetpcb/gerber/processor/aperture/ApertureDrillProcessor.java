package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.board.shape.HoleShape;
import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.drill.ComponentDrillAttribute;
import com.mynetpcb.gerber.attribute.drill.MechanicalDrillAttribute;
import com.mynetpcb.gerber.attribute.drill.ViaDrillAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Pad;

import java.util.Collection;
import java.util.List;

public class ApertureDrillProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureDrillProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
        if(layermask==Layer.NPTH_LAYER_DRILL){
           //non plated
           processPads(board);
           processHoles(board);
        }else{
            //plated
           processVias(board); 
            
        }
    }
    
    private void processPads(Unit<? extends Shape> board ){
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        for(FootprintShape footrpint:footprints){
            Collection<Pad> pads=((Pinaware)footrpint).getPins();
            for(Pad pad:pads){
                if(pad.getType()==Pad.Type.THROUGH_HOLE ||pad.getType()==Pad.Type.CONNECTOR){
                    CircleAperture drill=new CircleAperture(); 
                    drill.setDiameter(pad.getDrill().getWidth());
                    drill.setAttribute(new ComponentDrillAttribute());
                    dictionary.add(drill);   
                }
            }
        }
    }
    
    private void processHoles(Unit<? extends Shape> board ){
        List<HoleShape> holes= board.getShapes(HoleShape.class);              
        for(HoleShape hole:holes){
            CircleAperture drill=new CircleAperture(); 
            drill.setDiameter(hole.getWidth());
            drill.setAttribute(new MechanicalDrillAttribute());
            dictionary.add(drill); 
        }  
                
            
        
    }
    
    private void processVias(Unit<? extends Shape> board ){
        List<ViaShape> vias= board.getShapes(ViaShape.class);              
        for(ViaShape via:vias){
            CircleAperture drill=new CircleAperture(); 
            drill.setDiameter(via.getThickness());
            drill.setAttribute(new ViaDrillAttribute());
            dictionary.add(drill); 
        } 
    }
}
