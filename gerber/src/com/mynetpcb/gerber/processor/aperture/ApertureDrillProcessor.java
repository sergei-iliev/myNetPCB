package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBHole;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.drill.ComponentDrillAttribute;
import com.mynetpcb.gerber.attribute.drill.MechanicalDrillAttribute;
import com.mynetpcb.gerber.attribute.drill.ViaDrillAttribute;
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
    public void process(Board board, int layermask) {
        if(layermask==Layer.NPTH_LAYER_DRILL){
           //non plated
           processPads(board);
           processHoles(board);
        }else{
            //plated
           processVias(board); 
            
        }
        //process pads
        
        //process vias
        
        //process mechanical holes
    }
    
    private void processPads(Board board){
        List<PCBFootprint> footprints= board.getShapes(PCBFootprint.class);              
        for(PCBFootprint footrpint:footprints){
            Collection<Pad> pads=footrpint.getPins();
            for(Pad pad:pads){
                if(pad.getType()==Pad.Type.THROUGH_HOLE){
                    CircleAperture drill=new CircleAperture(); 
                    drill.setDiameter(pad.getDrill().getWidth());
                    drill.setAttribute(new ComponentDrillAttribute());
                    dictionary.add(drill);   
                }
            }
        }
    }
    
    private void processHoles(Board board){
        List<PCBHole> holes= board.getShapes(PCBHole.class);              
        for(PCBHole hole:holes){
            CircleAperture drill=new CircleAperture(); 
            drill.setDiameter(hole.getWidth());
            drill.setAttribute(new MechanicalDrillAttribute());
            dictionary.add(drill); 
        }  
                
            
        
    }
    
    private void processVias(Board board){
        List<PCBVia> vias= board.getShapes(PCBVia.class);              
        for(PCBVia via:vias){
            CircleAperture drill=new CircleAperture(); 
            drill.setDiameter(via.getThickness());
            drill.setAttribute(new ViaDrillAttribute());
            dictionary.add(drill); 
        } 
    }
}
