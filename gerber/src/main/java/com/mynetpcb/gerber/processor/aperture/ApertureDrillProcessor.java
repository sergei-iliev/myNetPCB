package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.board.shape.HoleShape;
import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
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
           processPads(board,false);
           processHoles(board);
        }else{           
        	//plated
           processPads(board,true);	
           processVias(board); 
            
        }
    }
    
    private void processPads(Unit<? extends Shape> board,boolean plated){
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        for(FootprintShape footprint:footprints){
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){
                if(pad.getType()==Pad.Type.THROUGH_HOLE ||pad.getType()==Pad.Type.CONNECTOR){
                	if(pad.getPlated()!=plated) {
                    	continue;
                    }
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
            drill.setDiameter(hole.getInner().r*2);
            drill.setAttribute(new MechanicalDrillAttribute());
            dictionary.add(drill); 
        }  
                
            
        
    }
    
    private void processVias(Unit<? extends Shape> board ){
        List<ViaShape> vias= board.getShapes(ViaShape.class);              
        for(ViaShape via:vias){
            CircleAperture drill=new CircleAperture(); 
            drill.setDiameter(via.getInner().r*2);
            drill.setAttribute(new ViaDrillAttribute());
            dictionary.add(drill); 
        } 
    }
}

